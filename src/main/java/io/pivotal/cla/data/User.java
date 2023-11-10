/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.pivotal.cla.data;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class User implements Serializable {
	@Id
	@Column(name = "github_login")
	private String gitHubLogin;
	private String name;
	private String accessToken;
	@JsonProperty("avatar_url")
	private String avatarUrl;
	@ElementCollection
	@Column(name = "email")
	@CollectionTable(name = "user_email", joinColumns = @JoinColumn(name = "github_login"))
	private Set<String> emails;
	@Transient
	private boolean admin;
	@Transient
	private boolean claAuthor;
	@Transient
	private boolean adminAccessRequested;
	private static final long serialVersionUID = 7285385090438382841L;

	public User() {
	}

	public User(User user) {
		this.gitHubLogin = user.gitHubLogin;
		this.name = user.name;
		this.accessToken = user.accessToken;
		this.avatarUrl = user.avatarUrl;
		this.emails = user.emails;
		this.admin = user.admin;
		this.claAuthor = user.claAuthor;
		this.adminAccessRequested = user.adminAccessRequested;
	}

	public String getGitHubLogin() {
		return this.gitHubLogin;
	}

	public String getName() {
		return this.name;
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public String getAvatarUrl() {
		return this.avatarUrl;
	}

	public Set<String> getEmails() {
		return this.emails;
	}

	public boolean isAdmin() {
		return this.admin;
	}

	public boolean isClaAuthor() {
		return this.claAuthor;
	}

	public boolean isAdminAccessRequested() {
		return this.adminAccessRequested;
	}

	public void setGitHubLogin(final String gitHubLogin) {
		this.gitHubLogin = gitHubLogin;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setAccessToken(final String accessToken) {
		this.accessToken = accessToken;
	}

	public void setAvatarUrl(final String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public void setEmails(final Set<String> emails) {
		this.emails = emails;
	}

	public void setAdmin(final boolean admin) {
		this.admin = admin;
	}

	public void setClaAuthor(final boolean claAuthor) {
		this.claAuthor = claAuthor;
	}

	public void setAdminAccessRequested(final boolean adminAccessRequested) {
		this.adminAccessRequested = adminAccessRequested;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "User(gitHubLogin=" + this.getGitHubLogin() + ", name=" + this.getName() + ", accessToken=" + this.getAccessToken() + ", avatarUrl=" + this.getAvatarUrl() + ", emails=" + this.getEmails() + ", admin=" + this.isAdmin() + ", claAuthor=" + this.isClaAuthor() + ", adminAccessRequested=" + this.isAdminAccessRequested() + ")";
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof User)) return false;
		final User other = (User) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$gitHubLogin = this.getGitHubLogin();
		final java.lang.Object other$gitHubLogin = other.getGitHubLogin();
		if (this$gitHubLogin == null ? other$gitHubLogin != null : !this$gitHubLogin.equals(other$gitHubLogin)) return false;
		final java.lang.Object this$name = this.getName();
		final java.lang.Object other$name = other.getName();
		if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
		final java.lang.Object this$accessToken = this.getAccessToken();
		final java.lang.Object other$accessToken = other.getAccessToken();
		if (this$accessToken == null ? other$accessToken != null : !this$accessToken.equals(other$accessToken)) return false;
		final java.lang.Object this$avatarUrl = this.getAvatarUrl();
		final java.lang.Object other$avatarUrl = other.getAvatarUrl();
		if (this$avatarUrl == null ? other$avatarUrl != null : !this$avatarUrl.equals(other$avatarUrl)) return false;
		final java.lang.Object this$emails = this.getEmails();
		final java.lang.Object other$emails = other.getEmails();
		if (this$emails == null ? other$emails != null : !this$emails.equals(other$emails)) return false;
		if (this.isClaAuthor() != other.isClaAuthor()) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof User;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $gitHubLogin = this.getGitHubLogin();
		result = result * PRIME + ($gitHubLogin == null ? 43 : $gitHubLogin.hashCode());
		final java.lang.Object $name = this.getName();
		result = result * PRIME + ($name == null ? 43 : $name.hashCode());
		final java.lang.Object $accessToken = this.getAccessToken();
		result = result * PRIME + ($accessToken == null ? 43 : $accessToken.hashCode());
		final java.lang.Object $avatarUrl = this.getAvatarUrl();
		result = result * PRIME + ($avatarUrl == null ? 43 : $avatarUrl.hashCode());
		final java.lang.Object $emails = this.getEmails();
		result = result * PRIME + ($emails == null ? 43 : $emails.hashCode());
		result = result * PRIME + (this.isClaAuthor() ? 79 : 97);
		return result;
	}
}
