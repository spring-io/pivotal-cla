/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.pivotal.cla.mvc.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.UserRepository;
import io.pivotal.cla.mvc.util.UrlBuilder;
import io.pivotal.cla.security.GithubAuthenticationEntryPoint;
import io.pivotal.cla.security.Login;
import io.pivotal.cla.service.CurrentUserRequest;
import io.pivotal.cla.service.GitHubService;
import io.pivotal.cla.service.OAuthAccessTokenParams;

@Controller
public class OAuthController {
	AuthenticationSuccessHandler success = new SavedRequestAwareAuthenticationSuccessHandler();

	@Autowired
	GitHubService github;

	@Autowired
	UserRepository users;

	@RequestMapping("/login/oauth2/github")
	public void oauth(HttpServletRequest request, HttpServletResponse response, @RequestParam String code,
			@RequestParam String state) throws Exception {
		String actualState = (String) request.getSession().getAttribute("state");
		if(actualState == null || !actualState.equals(state)) {
			throw new InvalidSecretState();
		}

		boolean admin = GithubAuthenticationEntryPoint.isAdmin(state);

		OAuthAccessTokenParams params = new OAuthAccessTokenParams();
		params.setCallbackUrl(UrlBuilder.fromRequest(request).callbackUrl());
		params.setCode(code);
		params.setState(actualState);

		CurrentUserRequest userRequest = new CurrentUserRequest();
		userRequest.setOauthParams(params);
		userRequest.setRequestAdminAccess(admin);

		User user = github.getCurrentUser(userRequest);

		User existingUser = users.findOne(user.getGithubLogin());
		boolean isNewUser = existingUser == null;
		user.setNew(isNewUser);

		users.save(user);

		Authentication authentication = Login.loginAs(user);

		success.onAuthenticationSuccess(request, response, authentication);
	}

	@ExceptionHandler
	@ResponseStatus(code= HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String handleInvalidSecretState(InvalidSecretState error) {
		return "Invalid Secret State";
	}

	@SuppressWarnings("serial")
	static class InvalidSecretState extends RuntimeException {}
}