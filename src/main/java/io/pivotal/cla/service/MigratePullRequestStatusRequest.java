package io.pivotal.cla.service;

import java.util.List;

public class MigratePullRequestStatusRequest {
	/**
	 * Repository ids in the format of spring-projects/spring-security
	 */
	private final List<String> repositoryIds;
	private final String commitStatusUrl;
	private final String accessToken;
	private final String faqUrl;
	private final String baseSyncUrl;

	MigratePullRequestStatusRequest(final List<String> repositoryIds, final String commitStatusUrl, final String accessToken, final String faqUrl, final String baseSyncUrl) {
		this.repositoryIds = repositoryIds;
		this.commitStatusUrl = commitStatusUrl;
		this.accessToken = accessToken;
		this.faqUrl = faqUrl;
		this.baseSyncUrl = baseSyncUrl;
	}


	public static class MigratePullRequestStatusRequestBuilder {
		private List<String> repositoryIds;
		private String commitStatusUrl;
		private String accessToken;
		private String faqUrl;
		private String baseSyncUrl;

		MigratePullRequestStatusRequestBuilder() {
		}

		public MigratePullRequestStatusRequestBuilder repositoryIds(final List<String> repositoryIds) {
			this.repositoryIds = repositoryIds;
			return this;
		}

		public MigratePullRequestStatusRequestBuilder commitStatusUrl(final String commitStatusUrl) {
			this.commitStatusUrl = commitStatusUrl;
			return this;
		}

		public MigratePullRequestStatusRequestBuilder accessToken(final String accessToken) {
			this.accessToken = accessToken;
			return this;
		}

		public MigratePullRequestStatusRequestBuilder faqUrl(final String faqUrl) {
			this.faqUrl = faqUrl;
			return this;
		}

		public MigratePullRequestStatusRequestBuilder baseSyncUrl(final String baseSyncUrl) {
			this.baseSyncUrl = baseSyncUrl;
			return this;
		}

		public MigratePullRequestStatusRequest build() {
			return new MigratePullRequestStatusRequest(repositoryIds, commitStatusUrl, accessToken, faqUrl, baseSyncUrl);
		}

		@java.lang.Override
		public java.lang.String toString() {
			return "MigratePullRequestStatusRequest.MigratePullRequestStatusRequestBuilder(repositoryIds=" + this.repositoryIds + ", commitStatusUrl=" + this.commitStatusUrl + ", accessToken=" + this.accessToken + ", faqUrl=" + this.faqUrl + ", baseSyncUrl=" + this.baseSyncUrl + ")";
		}
	}

	public static MigratePullRequestStatusRequestBuilder builder() {
		return new MigratePullRequestStatusRequestBuilder();
	}

	/**
	 * Repository ids in the format of spring-projects/spring-security
	 */
	public List<String> getRepositoryIds() {
		return this.repositoryIds;
	}

	public String getCommitStatusUrl() {
		return this.commitStatusUrl;
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public String getFaqUrl() {
		return this.faqUrl;
	}

	public String getBaseSyncUrl() {
		return this.baseSyncUrl;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof MigratePullRequestStatusRequest)) return false;
		final MigratePullRequestStatusRequest other = (MigratePullRequestStatusRequest) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$repositoryIds = this.getRepositoryIds();
		final java.lang.Object other$repositoryIds = other.getRepositoryIds();
		if (this$repositoryIds == null ? other$repositoryIds != null : !this$repositoryIds.equals(other$repositoryIds)) return false;
		final java.lang.Object this$commitStatusUrl = this.getCommitStatusUrl();
		final java.lang.Object other$commitStatusUrl = other.getCommitStatusUrl();
		if (this$commitStatusUrl == null ? other$commitStatusUrl != null : !this$commitStatusUrl.equals(other$commitStatusUrl)) return false;
		final java.lang.Object this$accessToken = this.getAccessToken();
		final java.lang.Object other$accessToken = other.getAccessToken();
		if (this$accessToken == null ? other$accessToken != null : !this$accessToken.equals(other$accessToken)) return false;
		final java.lang.Object this$faqUrl = this.getFaqUrl();
		final java.lang.Object other$faqUrl = other.getFaqUrl();
		if (this$faqUrl == null ? other$faqUrl != null : !this$faqUrl.equals(other$faqUrl)) return false;
		final java.lang.Object this$baseSyncUrl = this.getBaseSyncUrl();
		final java.lang.Object other$baseSyncUrl = other.getBaseSyncUrl();
		if (this$baseSyncUrl == null ? other$baseSyncUrl != null : !this$baseSyncUrl.equals(other$baseSyncUrl)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof MigratePullRequestStatusRequest;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $repositoryIds = this.getRepositoryIds();
		result = result * PRIME + ($repositoryIds == null ? 43 : $repositoryIds.hashCode());
		final java.lang.Object $commitStatusUrl = this.getCommitStatusUrl();
		result = result * PRIME + ($commitStatusUrl == null ? 43 : $commitStatusUrl.hashCode());
		final java.lang.Object $accessToken = this.getAccessToken();
		result = result * PRIME + ($accessToken == null ? 43 : $accessToken.hashCode());
		final java.lang.Object $faqUrl = this.getFaqUrl();
		result = result * PRIME + ($faqUrl == null ? 43 : $faqUrl.hashCode());
		final java.lang.Object $baseSyncUrl = this.getBaseSyncUrl();
		result = result * PRIME + ($baseSyncUrl == null ? 43 : $baseSyncUrl.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "MigratePullRequestStatusRequest(repositoryIds=" + this.getRepositoryIds() + ", commitStatusUrl=" + this.getCommitStatusUrl() + ", accessToken=" + this.getAccessToken() + ", faqUrl=" + this.getFaqUrl() + ", baseSyncUrl=" + this.getBaseSyncUrl() + ")";
	}
}
