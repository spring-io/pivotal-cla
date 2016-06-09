package io.pivotal.cla.service;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import io.pivotal.cla.service.github.PullRequestStatus;
import io.pivotal.cla.service.github.GitHubApi;
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

	public void savePullRequestStatus(ClaPullRequestStatusRequest request) {
		String claName = request.getClaName();
		PullRequestStatus commitStatus = request.getCommitStatus();
		String gitHubLogin = commitStatus.getGitHubUsername();
		if(commitStatus.getAccessToken() == null) {
			AccessToken accessToken = accessTokenRepository.findOne(commitStatus.getRepoId());
			if(accessToken == null) {
				return;
			}
			String token = accessToken.getToken();
			commitStatus.setAccessToken(token);
		}
		if(commitStatus.getSha() == null) {
			String sha = gitHub.getShaForPullRequest(commitStatus);
			if(sha == null) {
				return;
			}
			commitStatus.setSha(sha);
		}
		if(commitStatus.getSuccess() == null) {
			boolean hasSigned = hasSigned(gitHubLogin, claName);
			commitStatus.setSuccess(hasSigned);
		}
		gitHub.save(commitStatus);
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

	@SneakyThrows
	public void migratePullRequestStatus(String claName, MigratePullRequestStatusRequest request) {
		List<PullRequestStatus> commitStatuses = gitHub.createUpdatePullRequestStatuses(request);
		long oneSecondInMs = TimeUnit.SECONDS.toMillis(1L);
		for(PullRequestStatus status : commitStatuses) {
			boolean success = hasSigned(status.getGitHubUsername(), claName);
			status.setSuccess(success);
			gitHub.save(status);

			// necessary to help prevent abuse rate limits
			// https://developer.github.com/guides/best-practices-for-integrators/#dealing-with-abuse-rate-limits
			Thread.sleep(oneSecondInMs);
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
