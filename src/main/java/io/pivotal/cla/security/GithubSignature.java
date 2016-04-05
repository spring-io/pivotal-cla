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

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;

import io.pivotal.cla.data.AccessToken;
import io.pivotal.cla.data.repository.AccessTokenRepository;

/**
 * @author Rob Winch
 *
 */
@Component
public class GithubSignature {
	static final String SIGNATURE_PREFIX = "sha1=";

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	private AccessTokenRepository accessTokens;

	@Autowired
	public GithubSignature(AccessTokenRepository accessTokens) {
		super();
		this.accessTokens = accessTokens;
	}

	public boolean check(String githubSignature, String body) throws Exception {
		if(githubSignature == null || !githubSignature.startsWith(SIGNATURE_PREFIX)) {
			return false;
		}
		AccessToken expectedToken = accessTokens.findOne(AccessToken.CLA_ACCESS_TOKEN_ID);
		if(expectedToken == null) {
			return false;
		}

		String providedHmac = githubSignature.substring(SIGNATURE_PREFIX.length());

		byte[] providedHmacBytes = Hex.decode(providedHmac);

		byte[] expectedBytes = sign(body, expectedToken.getToken());

		return MessageDigest.isEqual(providedHmacBytes, expectedBytes);
	}

	public String create(String body, String token) throws Exception {
		return SIGNATURE_PREFIX + new String(Hex.encode(sign(body, token)));
	}

	private byte[] sign(String body, String token)
			throws NoSuchAlgorithmException, InvalidKeyException {
		SecretKeySpec signingKey = new SecretKeySpec(token.getBytes(), HMAC_SHA1_ALGORITHM);
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);
		byte[] expectedBytes = mac.doFinal(body.getBytes());
		return expectedBytes;
	}
}
