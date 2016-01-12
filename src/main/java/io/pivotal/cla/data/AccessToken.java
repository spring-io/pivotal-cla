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
package io.pivotal.cla.data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AccessToken {

	@Id
	private String githubId;

	private String token;

	public AccessToken() {
	}

	public AccessToken(String githubId, String token) {
		this.githubId = githubId;
		this.token = token;
	}

	/**
	 * @return the githubId
	 */
	public String getGithubId() {
		return githubId;
	}

	/**
	 * @param githubId
	 *            the githubId to set
	 */
	public void setGithubId(String githubId) {
		this.githubId = githubId;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token
	 *            the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
}
