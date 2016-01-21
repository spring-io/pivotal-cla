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
package io.pivotal.cla;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Rob Winch
 *
 */
@Component
@ConfigurationProperties(prefix="security.oauth2")
public class ClaOAuthConfig {

	@Value("${security.oauth2.pivotal-cla.token}")
	private String pivotalClaAccessToken;

	private OAuthClientCredentials admin;

	private OAuthClientCredentials main;

	/**
	 * @return the pivotalClaAccessToken
	 */
	public String getPivotalClaAccessToken() {
		return pivotalClaAccessToken;
	}

	/**
	 * @param pivotalClaAccessToken the pivotalClaAccessToken to set
	 */
	public void setPivotalClaAccessToken(String pivotalClaAccessToken) {
		this.pivotalClaAccessToken = pivotalClaAccessToken;
	}

	/**
	 * @return the admin
	 */
	public OAuthClientCredentials getAdmin() {
		return admin;
	}

	/**
	 * @param admin the admin to set
	 */
	public void setAdmin(OAuthClientCredentials admin) {
		this.admin = admin;
	}

	/**
	 * @return the main
	 */
	public OAuthClientCredentials getMain() {
		return main;
	}

	/**
	 * @param main the main to set
	 */
	public void setMain(OAuthClientCredentials main) {
		this.main = main;
	}
}
