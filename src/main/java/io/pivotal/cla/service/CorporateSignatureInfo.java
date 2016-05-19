package io.pivotal.cla.service;

import java.util.List;

import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.CorporateSignature;
import lombok.Data;

@Data
public class CorporateSignatureInfo {
	private final ContributorLicenseAgreement contributorLicenseAgreement;
	private final CorporateSignature corporateSignature;
	private final List<String> gitHubOrganizations;
}
