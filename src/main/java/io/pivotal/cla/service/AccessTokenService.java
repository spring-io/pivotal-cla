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
package io.pivotal.cla.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.pivotal.cla.OAuthClientCredentials;

/**
 * @author Rob Winch
 *
 */
@Component
public class AccessTokenService {
	public static String AUTHORIZE_URI = "https://github.com/login/oauth/access_token";

	private RestTemplate rest = new RestTemplate();

	public String getToken(AccessTokenRequest request) {
		OAuthAccessTokenParams oauthParams = request.getOauthParams();
		Map<String, String> params = new HashMap<String, String>();
		OAuthClientCredentials credentials = request.getCredentials();

		params.put("client_id", credentials.getClientId());
		params.put("client_secret", credentials.getClientSecret());
		params.put("code", oauthParams.getCode());
		params.put("state", oauthParams.getState());
		params.put("redirect_url", oauthParams.getCallbackUrl());

		ResponseEntity<AccessToken> token = rest.postForEntity(AUTHORIZE_URI, params, AccessToken.class);

		return token.getBody().getAccessToken();
	}

	static class AccessToken {
		@JsonProperty("access_token")
		private String accessToken;
		@JsonProperty("token_type")
		private String tokenType;
		private String scope;
		/**
		 * @return the accessToken
		 */
		public String getAccessToken() {
			return accessToken;
		}
		/**
		 * @param accessToken the accessToken to set
		 */
		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}
		/**
		 * @return the tokenType
		 */
		public String getTokenType() {
			return tokenType;
		}
		/**
		 * @param tokenType the tokenType to set
		 */
		public void setTokenType(String tokenType) {
			this.tokenType = tokenType;
		}
		/**
		 * @return the scope
		 */
		public String getScope() {
			return scope;
		}
		/**
		 * @param scope the scope to set
		 */
		public void setScope(String scope) {
			this.scope = scope;
		}
	}
}
