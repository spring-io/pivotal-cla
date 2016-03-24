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
package io.pivotal.cla.security;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.util.UriComponentsBuilder;

import io.pivotal.cla.config.OAuthClientCredentials;
import io.pivotal.cla.mvc.util.UrlBuilder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GithubAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private OAuthClientCredentials config;
	private String scope;

	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		String secretState = statePrefix() + UUID.randomUUID().toString();

		request.getSession().setAttribute("state", secretState);

		String callbackUrl = UrlBuilder.fromRequest(request).callbackUrl();
		String redirectUrl = UriComponentsBuilder.fromHttpUrl("https://github.com/login/oauth/authorize")
			.queryParam("client_id", config.getClientId())
			.queryParam("redirect_uri", callbackUrl)
			.queryParam("state", secretState)
			.queryParam("scope", scope)
			.build()
			.toUriString();

		response.sendRedirect(redirectUrl);
	}

	public static boolean isAdmin(String secretState) {
		return secretState != null && secretState.startsWith("ADMIN");
	}

	private String statePrefix() {
		return scope.contains("repo:status") ? "ADMIN" : "USER";
	}
}