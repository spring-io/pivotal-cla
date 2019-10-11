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
package io.pivotal.cla.service.github;

import java.util.List;

/**
 * @author Rob Winch
 */
public class CreatePullRequestHookRequest {
	private String accessToken;
	private List<String> repositoryIds;
	private String gitHubEventUrl;
	private String secret;

	public CreatePullRequestHookRequest() {
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public List<String> getRepositoryIds() {
		return this.repositoryIds;
	}

	public String getGitHubEventUrl() {
		return this.gitHubEventUrl;
	}

	public String getSecret() {
		return this.secret;
	}

	public void setAccessToken(final String accessToken) {
		this.accessToken = accessToken;
	}

	public void setRepositoryIds(final List<String> repositoryIds) {
		this.repositoryIds = repositoryIds;
	}

	public void setGitHubEventUrl(final String gitHubEventUrl) {
		this.gitHubEventUrl = gitHubEventUrl;
	}

	public void setSecret(final String secret) {
		this.secret = secret;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof CreatePullRequestHookRequest)) return false;
		final CreatePullRequestHookRequest other = (CreatePullRequestHookRequest) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$accessToken = this.getAccessToken();
		final java.lang.Object other$accessToken = other.getAccessToken();
		if (this$accessToken == null ? other$accessToken != null : !this$accessToken.equals(other$accessToken)) return false;
		final java.lang.Object this$repositoryIds = this.getRepositoryIds();
		final java.lang.Object other$repositoryIds = other.getRepositoryIds();
		if (this$repositoryIds == null ? other$repositoryIds != null : !this$repositoryIds.equals(other$repositoryIds)) return false;
		final java.lang.Object this$gitHubEventUrl = this.getGitHubEventUrl();
		final java.lang.Object other$gitHubEventUrl = other.getGitHubEventUrl();
		if (this$gitHubEventUrl == null ? other$gitHubEventUrl != null : !this$gitHubEventUrl.equals(other$gitHubEventUrl)) return false;
		final java.lang.Object this$secret = this.getSecret();
		final java.lang.Object other$secret = other.getSecret();
		if (this$secret == null ? other$secret != null : !this$secret.equals(other$secret)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof CreatePullRequestHookRequest;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $accessToken = this.getAccessToken();
		result = result * PRIME + ($accessToken == null ? 43 : $accessToken.hashCode());
		final java.lang.Object $repositoryIds = this.getRepositoryIds();
		result = result * PRIME + ($repositoryIds == null ? 43 : $repositoryIds.hashCode());
		final java.lang.Object $gitHubEventUrl = this.getGitHubEventUrl();
		result = result * PRIME + ($gitHubEventUrl == null ? 43 : $gitHubEventUrl.hashCode());
		final java.lang.Object $secret = this.getSecret();
		result = result * PRIME + ($secret == null ? 43 : $secret.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "CreatePullRequestHookRequest(accessToken=" + this.getAccessToken() + ", repositoryIds=" + this.getRepositoryIds() + ", gitHubEventUrl=" + this.getGitHubEventUrl() + ", secret=" + this.getSecret() + ")";
	}
}
