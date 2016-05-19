package io.pivotal.cla.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.pivotal.cla.data.AccessToken;
import io.pivotal.cla.data.repository.AccessTokenRepository;
import io.pivotal.cla.service.github.GitHubApi;
import io.pivotal.cla.service.github.UpdatePullRequestStatusRequest;

@Component
public class ClaService {
	final GitHubApi gitHub;
	final AccessTokenRepository accessTokenRepository;

	@Autowired
	public ClaService(GitHubApi gitHub, AccessTokenRepository accessTokenRepository) {
		super();
		this.gitHub = gitHub;
		this.accessTokenRepository = accessTokenRepository;
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
}
