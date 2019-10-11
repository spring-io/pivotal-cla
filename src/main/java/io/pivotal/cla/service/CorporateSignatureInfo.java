package io.pivotal.cla.service;

import java.util.List;
import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.CorporateSignature;

public class CorporateSignatureInfo {
	private final ContributorLicenseAgreement contributorLicenseAgreement;
	private final CorporateSignature corporateSignature;
	private final List<String> gitHubOrganizations;

	@java.beans.ConstructorProperties({"contributorLicenseAgreement", "corporateSignature", "gitHubOrganizations"})
	public CorporateSignatureInfo(final ContributorLicenseAgreement contributorLicenseAgreement, final CorporateSignature corporateSignature, final List<String> gitHubOrganizations) {
		this.contributorLicenseAgreement = contributorLicenseAgreement;
		this.corporateSignature = corporateSignature;
		this.gitHubOrganizations = gitHubOrganizations;
	}

	public ContributorLicenseAgreement getContributorLicenseAgreement() {
		return this.contributorLicenseAgreement;
	}

	public CorporateSignature getCorporateSignature() {
		return this.corporateSignature;
	}

	public List<String> getGitHubOrganizations() {
		return this.gitHubOrganizations;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof CorporateSignatureInfo)) return false;
		final CorporateSignatureInfo other = (CorporateSignatureInfo) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$contributorLicenseAgreement = this.getContributorLicenseAgreement();
		final java.lang.Object other$contributorLicenseAgreement = other.getContributorLicenseAgreement();
		if (this$contributorLicenseAgreement == null ? other$contributorLicenseAgreement != null : !this$contributorLicenseAgreement.equals(other$contributorLicenseAgreement)) return false;
		final java.lang.Object this$corporateSignature = this.getCorporateSignature();
		final java.lang.Object other$corporateSignature = other.getCorporateSignature();
		if (this$corporateSignature == null ? other$corporateSignature != null : !this$corporateSignature.equals(other$corporateSignature)) return false;
		final java.lang.Object this$gitHubOrganizations = this.getGitHubOrganizations();
		final java.lang.Object other$gitHubOrganizations = other.getGitHubOrganizations();
		if (this$gitHubOrganizations == null ? other$gitHubOrganizations != null : !this$gitHubOrganizations.equals(other$gitHubOrganizations)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof CorporateSignatureInfo;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $contributorLicenseAgreement = this.getContributorLicenseAgreement();
		result = result * PRIME + ($contributorLicenseAgreement == null ? 43 : $contributorLicenseAgreement.hashCode());
		final java.lang.Object $corporateSignature = this.getCorporateSignature();
		result = result * PRIME + ($corporateSignature == null ? 43 : $corporateSignature.hashCode());
		final java.lang.Object $gitHubOrganizations = this.getGitHubOrganizations();
		result = result * PRIME + ($gitHubOrganizations == null ? 43 : $gitHubOrganizations.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "CorporateSignatureInfo(contributorLicenseAgreement=" + this.getContributorLicenseAgreement() + ", corporateSignature=" + this.getCorporateSignature() + ", gitHubOrganizations=" + this.getGitHubOrganizations() + ")";
	}
}
