package io.pivotal.cla.mvc.admin;

import java.io.Serializable;
import java.util.List;

public class UpdatePullRequestStatusesForm implements Serializable {
	private String claName;
	private List<String> repositories;
	private static final long serialVersionUID = 9167204453927088962L;

	public UpdatePullRequestStatusesForm() {
	}

	public String getClaName() {
		return this.claName;
	}

	public List<String> getRepositories() {
		return this.repositories;
	}

	public void setClaName(final String claName) {
		this.claName = claName;
	}

	public void setRepositories(final List<String> repositories) {
		this.repositories = repositories;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof UpdatePullRequestStatusesForm)) return false;
		final UpdatePullRequestStatusesForm other = (UpdatePullRequestStatusesForm) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$claName = this.getClaName();
		final java.lang.Object other$claName = other.getClaName();
		if (this$claName == null ? other$claName != null : !this$claName.equals(other$claName)) return false;
		final java.lang.Object this$repositories = this.getRepositories();
		final java.lang.Object other$repositories = other.getRepositories();
		if (this$repositories == null ? other$repositories != null : !this$repositories.equals(other$repositories)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof UpdatePullRequestStatusesForm;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $claName = this.getClaName();
		result = result * PRIME + ($claName == null ? 43 : $claName.hashCode());
		final java.lang.Object $repositories = this.getRepositories();
		result = result * PRIME + ($repositories == null ? 43 : $repositories.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "UpdatePullRequestStatusesForm(claName=" + this.getClaName() + ", repositories=" + this.getRepositories() + ")";
	}
}
