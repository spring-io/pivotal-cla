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

import com.github.scribejava.core.oauth.OAuthService;

import io.pivotal.cla.scribe.ScribeOAuthFactory;

public class ScribeAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private String scope;

	private ScribeOAuthFactory factory;

	public ScribeAuthenticationEntryPoint(String scope, ScribeOAuthFactory factory) {
		this.scope = scope;
		this.factory = factory;
	}

	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		String secretState = statePrefix() + UUID.randomUUID().toString();

		request.getSession().setAttribute("state", secretState);

		final OAuthService service = factory
				.serviceBuilder(request).state(secretState)
				.scope(scope)
				.build();

		String redirectUrl = service.getAuthorizationUrl(null);

		response.sendRedirect(redirectUrl);
	}

	public static boolean isAdmin(String secretState) {
		return secretState != null && secretState.startsWith("ADMIN");
	}

	private String statePrefix() {
		return scope.contains("repo:status") ? "ADMIN" : "USER";
	}
}