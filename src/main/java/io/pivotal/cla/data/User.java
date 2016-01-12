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

import java.util.Collections;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class User {
	@Id
	private String githubLogin;

	private String name;

	private String accessToken;

	@JsonProperty("avatar_url")
	private String avatarUrl;

	@ElementCollection
	private Set<String> emails;

	private boolean admin;

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	/**
	 * @return the isAdmin
	 */
	public boolean isAdmin() {
		return admin;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken
	 *            the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @return the githubLogin
	 */
	public String getGithubLogin() {
		return githubLogin;
	}

	/**
	 * @param githubLogin
	 *            the githubLogin to set
	 */
	public void setGithubLogin(String githubLogin) {
		this.githubLogin = githubLogin;
	}

	/**
	 * @return the avatarUrl
	 */
	public String getAvatarUrl() {
		return avatarUrl;
	}

	/**
	 * @param avatarUrl
	 *            the avatarUrl to set
	 */
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	/**
	 * @return the emails
	 */
	public Set<String> getEmails() {
		return Collections.unmodifiableSet(emails);
	}

	/**
	 * @param emails
	 *            the emails to set
	 */
	public void setEmails(Set<String> emails) {
		this.emails = emails;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [githubLogin=" + githubLogin + ", accessToken=" + accessToken + ", avatarUrl=" + avatarUrl
				+ ", emails=" + emails + "]";
	}
}
