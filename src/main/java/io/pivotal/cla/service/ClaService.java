package io.pivotal.cla.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import io.pivotal.cla.data.AccessToken;
import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.CorporateSignature;
import io.pivotal.cla.data.IndividualSignature;
import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.AccessTokenRepository;
import io.pivotal.cla.data.repository.ContributorLicenseAgreementRepository;
import io.pivotal.cla.data.repository.CorporateSignatureRepository;
import io.pivotal.cla.data.repository.IndividualSignatureRepository;
import io.pivotal.cla.service.github.GitHubApi;
import io.pivotal.cla.service.github.UpdatePullRequestStatusRequest;

@Component
public class ClaService {
	final GitHubApi gitHub;
	final AccessTokenRepository accessTokenRepository;
	final CorporateSignatureRepository corporateSignatureRepository;
	final ContributorLicenseAgreementRepository contributorLicenseAgreementRepository;
	final IndividualSignatureRepository individualSignatureRepository;

	@Autowired
	public ClaService(GitHubApi gitHub, AccessTokenRepository accessTokenRepository, ContributorLicenseAgreementRepository contributorLicenseAgreementRepository, CorporateSignatureRepository corporateSignatureRepository, IndividualSignatureRepository individualSignatureRepository) {
		super();
		this.gitHub = gitHub;
		this.accessTokenRepository = accessTokenRepository;
		this.contributorLicenseAgreementRepository = contributorLicenseAgreementRepository;
		this.corporateSignatureRepository = corporateSignatureRepository;
		this.individualSignatureRepository = individualSignatureRepository;

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

	public IndividualSignature findIndividualSignaturesFor(User user, String claName) {
		PageRequest pageable = new PageRequest(0, 1);
		List<IndividualSignature> results = individualSignatureRepository.findSignaturesFor(pageable, user, claName);
		return results.isEmpty() ? null : results.get(0);
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
