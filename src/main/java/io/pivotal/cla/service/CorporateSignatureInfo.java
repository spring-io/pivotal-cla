package io.pivotal.cla.service;

import java.util.List;

import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.CorporateSignature;
import lombok.Data;

@Data
public class CorporateSignatureInfo {
	final ContributorLicenseAgreement contributorLicenseAgreement;
	final CorporateSignature corporateSignature;
	final List<String> gitHubOrganizations;
}
