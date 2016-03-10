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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.pivotal.cla.ClaOAuthConfig;
import io.pivotal.cla.OAuthClientCredentials;
import lombok.Data;

/**
 * @author Rob Winch
 *
 */
@Component
public class AccessTokenService {
	private static final String AUTHORIZE_URI = "/login/oauth/access_token";

	private RestTemplate rest = new RestTemplate();

	private String authorizeUrl;

	@Autowired
	public AccessTokenService(ClaOAuthConfig config) {
		this.authorizeUrl = config.getScheme() + "://" + config.getAccessTokenHost() + ":" + config.getPort() + AUTHORIZE_URI;
	}

	public String getToken(AccessTokenRequest request) {
		OAuthAccessTokenParams oauthParams = request.getOauthParams();
		Map<String, String> params = new HashMap<String, String>();
		OAuthClientCredentials credentials = request.getCredentials();

		params.put("client_id", credentials.getClientId());
		params.put("client_secret", credentials.getClientSecret());
		params.put("code", oauthParams.getCode());
		params.put("state", oauthParams.getState());
		params.put("redirect_url", oauthParams.getCallbackUrl());

		ResponseEntity<AccessToken> token = rest.postForEntity(this.authorizeUrl, params, AccessToken.class);

		return token.getBody().getAccessToken();
	}

	@Data
	static class AccessToken {
		@JsonProperty("access_token")
		private String accessToken;
		@JsonProperty("token_type")
		private String tokenType;
		private String scope;

	}
}
