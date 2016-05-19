package io.pivotal.cla.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.pivotal.cla.data.AccessToken;
import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.CorporateSignature;
import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.AccessTokenRepository;
import io.pivotal.cla.data.repository.ContributorLicenseAgreementRepository;
import io.pivotal.cla.data.repository.CorporateSignatureRepository;
import io.pivotal.cla.service.github.GitHubApi;
import io.pivotal.cla.service.github.UpdatePullRequestStatusRequest;

@Component
public class ClaService {
	final GitHubApi gitHub;
	final AccessTokenRepository accessTokenRepository;
	final CorporateSignatureRepository corporateSignatureRepository;
	final ContributorLicenseAgreementRepository contributorLicenseAgreementRepository;

	@Autowired
	public ClaService(GitHubApi gitHub, AccessTokenRepository accessTokenRepository, ContributorLicenseAgreementRepository contributorLicenseAgreementRepository, CorporateSignatureRepository corporateSignatureRepository) {
		super();
		this.gitHub = gitHub;
		this.accessTokenRepository = accessTokenRepository;
		this.contributorLicenseAgreementRepository = contributorLicenseAgreementRepository;
		this.corporateSignatureRepository = corporateSignatureRepository;

	}

	public void updatePullRequest(UpdatePullRequestStatusRequest updatePullRequest) {
		if(updatePullRequest == null) {
			return;
		}
		AccessToken accessToken = accessTokenRepository.findOne(updatePullRequest.getRepositoryId());
		if(accessToken != null) {
			updatePullRequest.setAccessToken(accessToken.getToken());
			gitHub.save(updatePullRequest);
		}
	}

	public CorporateSignatureInfo findCorporateSignatureInfoFor(String claName, User user) {
		List<String> gitHubOrganizations = gitHub.getOrganizations(user.getGitHubLogin());
		CorporateSignature corporateSignature = corporateSignatureRepository.findSignature(claName, gitHubOrganizations, user.getEmails());
		ContributorLicenseAgreement contributorLicenseAgreement = corporateSignature == null
				? contributorLicenseAgreementRepository.findByNameAndPrimaryTrue(claName)
				: corporateSignature.getCla();

		return new CorporateSignatureInfo(contributorLicenseAgreement, corporateSignature, gitHubOrganizations);
	}
}
