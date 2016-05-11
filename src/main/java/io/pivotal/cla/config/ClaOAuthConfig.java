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
package io.pivotal.cla.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @author Rob Winch
 *
 */
@Component
@ConfigurationProperties(prefix="security.oauth2")
@Data
public class ClaOAuthConfig {

	@Value("${security.oauth2.pivotal-cla.tokenSecret}")
	private String pivotalClaAccessToken;

	private OAuthClientCredentials main;

	private String scheme = "https";

	private int port = 443;

	private String gitHubApiHost = "api.github.com";

	private String accessTokenHost = "github.com";

	public String getGitHubApiBaseUrl() {
		return scheme + "://" + gitHubApiHost + ":" + port + "/";
	}


}
