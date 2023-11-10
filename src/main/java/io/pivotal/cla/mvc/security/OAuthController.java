/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.pivotal.cla.mvc.security;

import io.pivotal.cla.data.IndividualSignature;
import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.CorporateSignatureRepository;
import io.pivotal.cla.data.repository.IndividualSignatureRepository;
import io.pivotal.cla.data.repository.UserRepository;
import io.pivotal.cla.mvc.support.ImportedSignaturesSessionAttr;
import io.pivotal.cla.mvc.util.UrlBuilder;
import io.pivotal.cla.security.GitHubAuthenticationEntryPoint;
import io.pivotal.cla.security.Login;
import io.pivotal.cla.service.github.CurrentUserRequest;
import io.pivotal.cla.service.github.GitHubApi;
import io.pivotal.cla.service.github.OAuthAccessTokenParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class OAuthController {
	private AuthenticationSuccessHandler success = new SavedRequestAwareAuthenticationSuccessHandler();
	@Autowired
	private GitHubApi gitHub;
	@Autowired
	private IndividualSignatureRepository individual;
	@Autowired
	private CorporateSignatureRepository corporate;
	@Autowired
	private UserRepository users;

	@GetMapping("/login/oauth2/github")
	public void oauth(ImportedSignaturesSessionAttr importedSignaturesAttr, HttpServletRequest request, HttpServletResponse response, @RequestParam String code, @RequestParam String state) throws Exception {
		String actualState = (String) request.getSession().getAttribute("state");
		if (actualState == null || !actualState.equals(state)) {
			throw new InvalidSecretState();
		}
		boolean admin = GitHubAuthenticationEntryPoint.isAdmin(state);
		OAuthAccessTokenParams params = new OAuthAccessTokenParams();
		params.setCallbackUrl(UrlBuilder.fromRequest(request).callbackUrl());
		params.setCode(code);
		params.setState(actualState);
		CurrentUserRequest userRequest = new CurrentUserRequest();
		userRequest.setOauthParams(params);
		userRequest.setRequestAdminAccess(admin);
		User user = gitHub.getCurrentUser(userRequest);
		User existingUser = users.findOne(user.getGitHubLogin());
		boolean isNewUser = existingUser == null;
		users.save(user);
		Authentication authentication = Login.loginAs(user);
		if (isNewUser) {
			List<IndividualSignature> individualSignatures = individual.findSignaturesFor(PageRequest.of(0, 1), user);
			boolean signed = !individualSignatures.isEmpty();
			if (!signed) {
				List<String> organizations = gitHub.getOrganizations(user.getGitHubLogin());
				signed = !corporate.findSignatures(PageRequest.of(0, 1), organizations, user.getEmails()).isEmpty();
			}
			if (signed) {
				importedSignaturesAttr.setValue(true);
			}
		}
		success.onAuthenticationSuccess(request, response, authentication);
	}

	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String handleInvalidSecretState(InvalidSecretState error) {
		return "Invalid Secret State";
	}


	@SuppressWarnings("serial")
	static class InvalidSecretState extends RuntimeException {
	}
}
