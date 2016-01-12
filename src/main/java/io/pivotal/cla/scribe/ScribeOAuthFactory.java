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
package io.pivotal.cla.scribe;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.core.builder.ServiceBuilder;

import io.pivotal.cla.OAuthConfig;
import io.pivotal.cla.mvc.util.UrlBuilder;

@Component
public class ScribeOAuthFactory {
	private final OAuthConfig config;

	@Autowired
	public ScribeOAuthFactory(OAuthConfig config) {
		super();
		this.config = config;
	}

	public ServiceBuilder serviceBuilder(HttpServletRequest request) {
		String callback = UrlBuilder
				.fromRequest(request)
				.path("/login/oauth2/github")
				.build();

		return new ServiceBuilder()
				.provider(GitHubApi.class)
				.apiKey(config.getClientId())
				.apiSecret(config.getClientSecret())
				.callback(callback);
	}
}
