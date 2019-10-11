package io.pivotal.cla.service;

import io.pivotal.cla.service.github.PullRequestStatus;

public class ClaPullRequestStatusRequest {
	/**
	 * Used to default {@link PullRequestStatus#getSuccess()} if it is null.
	 */
	private String claName;
	private PullRequestStatus commitStatus;

	public ClaPullRequestStatusRequest() {
	}

	/**
	 * Used to default {@link PullRequestStatus#getSuccess()} if it is null.
	 */
	public String getClaName() {
		return this.claName;
	}

	public PullRequestStatus getCommitStatus() {
		return this.commitStatus;
	}

	/**
	 * Used to default {@link PullRequestStatus#getSuccess()} if it is null.
	 */
	public void setClaName(final String claName) {
		this.claName = claName;
	}

	public void setCommitStatus(final PullRequestStatus commitStatus) {
		this.commitStatus = commitStatus;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof ClaPullRequestStatusRequest)) return false;
		final ClaPullRequestStatusRequest other = (ClaPullRequestStatusRequest) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$claName = this.getClaName();
		final java.lang.Object other$claName = other.getClaName();
		if (this$claName == null ? other$claName != null : !this$claName.equals(other$claName)) return false;
		final java.lang.Object this$commitStatus = this.getCommitStatus();
		final java.lang.Object other$commitStatus = other.getCommitStatus();
		if (this$commitStatus == null ? other$commitStatus != null : !this$commitStatus.equals(other$commitStatus)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof ClaPullRequestStatusRequest;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $claName = this.getClaName();
		result = result * PRIME + ($claName == null ? 43 : $claName.hashCode());
		final java.lang.Object $commitStatus = this.getCommitStatus();
		result = result * PRIME + ($commitStatus == null ? 43 : $commitStatus.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "ClaPullRequestStatusRequest(claName=" + this.getClaName() + ", commitStatus=" + this.getCommitStatus() + ")";
	}
}
