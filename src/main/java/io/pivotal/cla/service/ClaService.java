package io.pivotal.cla.service;

import java.util.HashSet;
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
import io.pivotal.cla.data.repository.UserRepository;
import io.pivotal.cla.service.github.CommitStatus;
import io.pivotal.cla.service.github.GitHubApi;
import io.pivotal.cla.service.github.UpdatePullRequestStatusRequest;
import lombok.SneakyThrows;

@Component
public class ClaService {
	final GitHubApi gitHub;
	final AccessTokenRepository accessTokenRepository;
	final CorporateSignatureRepository corporateSignatureRepository;
	final ContributorLicenseAgreementRepository contributorLicenseAgreementRepository;
	final IndividualSignatureRepository individualSignatureRepository;
	final UserRepository userRepository;

	@Autowired
	public ClaService(GitHubApi gitHub, AccessTokenRepository accessTokenRepository,
			ContributorLicenseAgreementRepository contributorLicenseAgreementRepository, UserRepository userRepository,
			CorporateSignatureRepository corporateSignatureRepository,
			IndividualSignatureRepository individualSignatureRepository) {
		super();
		this.gitHub = gitHub;
		this.accessTokenRepository = accessTokenRepository;
		this.contributorLicenseAgreementRepository = contributorLicenseAgreementRepository;
		this.userRepository = userRepository;
		this.corporateSignatureRepository = corporateSignatureRepository;
		this.individualSignatureRepository = individualSignatureRepository;

	}

	public void updatePullRequest(String claName, UpdatePullRequestStatusRequest updatePullRequest) {
		if(updatePullRequest == null) {
			return;
		}

		String gitHubLogin = updatePullRequest.getCurrentUserGitHubLogin();
		boolean hasSigned = hasSigned(gitHubLogin, claName);
		updatePullRequest.setSuccess(hasSigned);
		updatePullRequest(updatePullRequest);
	}

	public void updatePullRequest(UpdatePullRequestStatusRequest updatePullRequest) {
		if(updatePullRequest.getAccessToken() == null) {
			AccessToken accessToken = accessTokenRepository.findOne(updatePullRequest.getRepositoryId());
			if(accessToken == null) {
				return;
			}
			updatePullRequest.setAccessToken(accessToken.getToken());
		}

		gitHub.save(updatePullRequest);
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

	public void saveCommitStatus(String claName, CommitStatus commitStatus) {
		String gitHubLogin = commitStatus.getGitHubUsername();
		if(commitStatus.getSuccess() == null) {
			boolean hasSigned = hasSigned(gitHubLogin, claName);
			commitStatus.setSuccess(hasSigned);
		}
		if(commitStatus.getAccessToken() == null) {
			AccessToken accessToken = accessTokenRepository.findOne(commitStatus.getRepoId());
			String accessTokenValue = accessToken == null ? null : accessToken.getToken();
			if(accessTokenValue == null) {
				return;
			}
			commitStatus.setAccessToken(accessTokenValue);
		}
		gitHub.save(commitStatus);
	}

	@SneakyThrows
	public void migratePullRequestStatus(String claName, MigratePullRequestStatusRequest request) {
		List<CommitStatus> commitStatuses = gitHub.createUpdatePullRequestStatuses(request);
		for(CommitStatus status : commitStatuses) {
			boolean success = hasSigned(status.getGitHubUsername(), claName);
			status.setSuccess(success);
			gitHub.save(status);
		}
	}

	private boolean hasSigned(String gitHubLogin, String claName) {

		User user = userRepository.findOne(gitHubLogin);
		if(user == null) {
			user = new User();
			user.setGitHubLogin(gitHubLogin);
			user.setEmails(new HashSet<>());
		}
		return hasSigned(user, claName);
	}

	public boolean hasSigned(User user, String claName) {
		if(claName == null) {
			return false;
		}
		IndividualSignature signedIndividual = findIndividualSignaturesFor(user, claName);

		if(signedIndividual != null) {
			return true;
		}

		CorporateSignatureInfo corporateSignatureInfo = findCorporateSignatureInfoFor(claName, user);
		CorporateSignature corporateSignature = corporateSignatureInfo.getCorporateSignature();
		return corporateSignature != null;
	}
}
