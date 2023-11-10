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
package io.pivotal.cla.service.github;

import io.pivotal.cla.data.repository.AccessTokenRepository;

public class PullRequestStatus {
	public static final String UNKNOWN_PULL_REQUEST_STATE = "unknown";
	public static final String OPEN_PULL_REQUEST_STATE = "open";
	private int pullRequestId;
	private String repoId;
	private String sha;
	private Boolean success;
	private String url;
	/**
	 * The URL used to sync the Pull Request. For example,
	 * https://cla.pivotal.io/sync/pivotal?repositoryId=spring-projects/spring-security&pullRequestId=10
	 */
	private String syncUrl;
	/**
	 * The URL to the FAQ. For example,
	 * https://cla.pivotal.io/about
	 */
	private String faqUrl;
	private String gitHubUsername;
	private boolean admin;
	/**
	 * The Access Token used for updating the commit status. This is typically
	 * looked up using the {@link AccessTokenRepository} by the repoId.
	 */
	private String accessToken;
	private String pullRequestState;
	private String pullRequestBody;

	public boolean isSuccess() {
		return Boolean.TRUE.equals(success);
	}

	/**
	 * The URL used to instruct the user of how to resolve the commit status. For example,
	 * https://cla.pivotal.io/sign/pivotal?repositoryId=spring-projects/spring-security&pullRequestId=10
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	public PullRequestStatus() {
	}

	public int getPullRequestId() {
		return this.pullRequestId;
	}

	public String getRepoId() {
		return this.repoId;
	}

	public String getSha() {
		return this.sha;
	}

	public Boolean getSuccess() {
		return this.success;
	}

	/**
	 * The URL used to sync the Pull Request. For example,
	 * https://cla.pivotal.io/sync/pivotal?repositoryId=spring-projects/spring-security&pullRequestId=10
	 */
	public String getSyncUrl() {
		return this.syncUrl;
	}

	/**
	 * The URL to the FAQ. For example,
	 * https://cla.pivotal.io/about
	 */
	public String getFaqUrl() {
		return this.faqUrl;
	}

	public String getGitHubUsername() {
		return this.gitHubUsername;
	}

	public boolean isAdmin() {
		return this.admin;
	}

	/**
	 * The Access Token used for updating the commit status. This is typically
	 * looked up using the {@link AccessTokenRepository} by the repoId.
	 */
	public String getAccessToken() {
		return this.accessToken;
	}

	public String getPullRequestState() {
		return this.pullRequestState;
	}

	public String getPullRequestBody() {
		return this.pullRequestBody;
	}

	public void setPullRequestId(final int pullRequestId) {
		this.pullRequestId = pullRequestId;
	}

	public void setRepoId(final String repoId) {
		this.repoId = repoId;
	}

	public void setSha(final String sha) {
		this.sha = sha;
	}

	public void setSuccess(final Boolean success) {
		this.success = success;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	/**
	 * The URL used to sync the Pull Request. For example,
	 * https://cla.pivotal.io/sync/pivotal?repositoryId=spring-projects/spring-security&pullRequestId=10
	 */
	public void setSyncUrl(final String syncUrl) {
		this.syncUrl = syncUrl;
	}

	/**
	 * The URL to the FAQ. For example,
	 * https://cla.pivotal.io/about
	 */
	public void setFaqUrl(final String faqUrl) {
		this.faqUrl = faqUrl;
	}

	public void setGitHubUsername(final String gitHubUsername) {
		this.gitHubUsername = gitHubUsername;
	}

	public void setAdmin(final boolean admin) {
		this.admin = admin;
	}

	/**
	 * The Access Token used for updating the commit status. This is typically
	 * looked up using the {@link AccessTokenRepository} by the repoId.
	 */
	public void setAccessToken(final String accessToken) {
		this.accessToken = accessToken;
	}

	public void setPullRequestState(final String pullRequestState) {
		this.pullRequestState = pullRequestState;
	}

	public void setPullRequestBody(final String pullRequestBody) {
		this.pullRequestBody = pullRequestBody;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof PullRequestStatus)) return false;
		final PullRequestStatus other = (PullRequestStatus) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		if (this.getPullRequestId() != other.getPullRequestId()) return false;
		final java.lang.Object this$repoId = this.getRepoId();
		final java.lang.Object other$repoId = other.getRepoId();
		if (this$repoId == null ? other$repoId != null : !this$repoId.equals(other$repoId)) return false;
		final java.lang.Object this$sha = this.getSha();
		final java.lang.Object other$sha = other.getSha();
		if (this$sha == null ? other$sha != null : !this$sha.equals(other$sha)) return false;
		final java.lang.Object this$success = this.getSuccess();
		final java.lang.Object other$success = other.getSuccess();
		if (this$success == null ? other$success != null : !this$success.equals(other$success)) return false;
		final java.lang.Object this$url = this.getUrl();
		final java.lang.Object other$url = other.getUrl();
		if (this$url == null ? other$url != null : !this$url.equals(other$url)) return false;
		final java.lang.Object this$syncUrl = this.getSyncUrl();
		final java.lang.Object other$syncUrl = other.getSyncUrl();
		if (this$syncUrl == null ? other$syncUrl != null : !this$syncUrl.equals(other$syncUrl)) return false;
		final java.lang.Object this$faqUrl = this.getFaqUrl();
		final java.lang.Object other$faqUrl = other.getFaqUrl();
		if (this$faqUrl == null ? other$faqUrl != null : !this$faqUrl.equals(other$faqUrl)) return false;
		final java.lang.Object this$gitHubUsername = this.getGitHubUsername();
		final java.lang.Object other$gitHubUsername = other.getGitHubUsername();
		if (this$gitHubUsername == null ? other$gitHubUsername != null : !this$gitHubUsername.equals(other$gitHubUsername)) return false;
		if (this.isAdmin() != other.isAdmin()) return false;
		final java.lang.Object this$accessToken = this.getAccessToken();
		final java.lang.Object other$accessToken = other.getAccessToken();
		if (this$accessToken == null ? other$accessToken != null : !this$accessToken.equals(other$accessToken)) return false;
		final java.lang.Object this$pullRequestState = this.getPullRequestState();
		final java.lang.Object other$pullRequestState = other.getPullRequestState();
		if (this$pullRequestState == null ? other$pullRequestState != null : !this$pullRequestState.equals(other$pullRequestState)) return false;
		final java.lang.Object this$pullRequestBody = this.getPullRequestBody();
		final java.lang.Object other$pullRequestBody = other.getPullRequestBody();
		if (this$pullRequestBody == null ? other$pullRequestBody != null : !this$pullRequestBody.equals(other$pullRequestBody)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof PullRequestStatus;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		result = result * PRIME + this.getPullRequestId();
		final java.lang.Object $repoId = this.getRepoId();
		result = result * PRIME + ($repoId == null ? 43 : $repoId.hashCode());
		final java.lang.Object $sha = this.getSha();
		result = result * PRIME + ($sha == null ? 43 : $sha.hashCode());
		final java.lang.Object $success = this.getSuccess();
		result = result * PRIME + ($success == null ? 43 : $success.hashCode());
		final java.lang.Object $url = this.getUrl();
		result = result * PRIME + ($url == null ? 43 : $url.hashCode());
		final java.lang.Object $syncUrl = this.getSyncUrl();
		result = result * PRIME + ($syncUrl == null ? 43 : $syncUrl.hashCode());
		final java.lang.Object $faqUrl = this.getFaqUrl();
		result = result * PRIME + ($faqUrl == null ? 43 : $faqUrl.hashCode());
		final java.lang.Object $gitHubUsername = this.getGitHubUsername();
		result = result * PRIME + ($gitHubUsername == null ? 43 : $gitHubUsername.hashCode());
		result = result * PRIME + (this.isAdmin() ? 79 : 97);
		final java.lang.Object $accessToken = this.getAccessToken();
		result = result * PRIME + ($accessToken == null ? 43 : $accessToken.hashCode());
		final java.lang.Object $pullRequestState = this.getPullRequestState();
		result = result * PRIME + ($pullRequestState == null ? 43 : $pullRequestState.hashCode());
		final java.lang.Object $pullRequestBody = this.getPullRequestBody();
		result = result * PRIME + ($pullRequestBody == null ? 43 : $pullRequestBody.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "PullRequestStatus(pullRequestId=" + this.getPullRequestId() + ", repoId=" + this.getRepoId() + ", sha=" + this.getSha() + ", success=" + this.getSuccess() + ", url=" + this.getUrl() + ", syncUrl=" + this.getSyncUrl() + ", faqUrl=" + this.getFaqUrl() + ", gitHubUsername=" + this.getGitHubUsername() + ", admin=" + this.isAdmin() + ", accessToken=" + this.getAccessToken() + ", pullRequestState=" + this.getPullRequestState() + ", pullRequestBody=" + this.getPullRequestBody() + ")";
	}
}
