/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.pivotal.cla.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.egit.github.core.CommitStatus;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryHook;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verifier;
import com.github.scribejava.core.oauth.OAuthService;

import io.pivotal.cla.data.AccessToken;
import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.AccessTokenRepository;
import io.pivotal.cla.egit.github.core.ContextCommitStatus;
import io.pivotal.cla.egit.github.core.Email;
import io.pivotal.cla.egit.github.core.EventsRepositoryHook;
import io.pivotal.cla.egit.github.core.service.ContextCommitService;
import io.pivotal.cla.egit.github.core.service.EmailService;
import io.pivotal.cla.scribe.ScribeOAuthFactory;

@Component
public class MylynGithubService implements GitHubService {
	@Autowired
	ScribeOAuthFactory oauthFactory;

	@Autowired
	AccessTokenRepository tokenRepo;

	@Override
	public List<String> findRepositoryNames(String accessToken) throws IOException {
		GitHubClient client = new GitHubClient();
		client.setOAuth2Token(accessToken);

		RepositoryService service = new RepositoryService(client);
		List<Repository> repositories = service.getRepositories();
		List<String> repoSlugs = new ArrayList<>();
		for (Repository r : repositories) {
			org.eclipse.egit.github.core.User owner = r.getOwner();
			repoSlugs.add(owner.getLogin() + "/" + r.getName());
		}
		return repoSlugs;
	}

	public void save(io.pivotal.cla.service.CommitStatus commitStatus) {
		String repoId = commitStatus.getRepoId();
		AccessToken token = tokenRepo.findOne(repoId);
		if (token == null) {
			return;
		}
		boolean success = commitStatus.isSuccess();
		RepositoryId id = RepositoryId.createFromId(repoId);
		GitHubClient client = createClient(token.getToken());
		ContextCommitService commitService = new ContextCommitService(client);
		ContextCommitStatus status = new ContextCommitStatus();
		status.setDescription(success ? "Thank you for signing the Contributor License Agreement!"
				: "Please sign the Contributor Licenese Agreement!");
		status.setState(success ? CommitStatus.STATE_SUCCESS : CommitStatus.STATE_FAILURE);
		status.setContext("ci/pivotal-cla");
		status.setUrl(commitStatus.getUrl());
		status.setTargetUrl(status.getUrl());

		try {
			commitService.createStatus(id, commitStatus.getSha(), status);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private GitHubClient createClient(String accessToken) {
		GitHubClient client = new GitHubClient();
		client.setOAuth2Token(accessToken);
		return client;
	}

	public User getCurrentUser(HttpServletRequest request, String code) {
		Verifier verifier = new Verifier(code);
		OAuthService service = oauthFactory.serviceBuilder(request).build();

		final Token accessToken = service.getAccessToken(null, verifier);
		String token = accessToken.getToken();

		GitHubClient client = createClient(token);

		org.eclipse.egit.github.core.service.UserService githubUsers = new org.eclipse.egit.github.core.service.UserService(
				client);
		EmailService emailService = EmailService.forOAuth(token);

		try {
			List<String> verifiedEmails = emailService.getEmails().stream().filter(e -> e.isVerified())
					.map(Email::getEmail).collect(Collectors.toList());
			org.eclipse.egit.github.core.User githubUser = githubUsers.getUser();

			User user = new User();
			user.setName(githubUser.getName());
			user.setAccessToken(token);
			user.setAvatarUrl(githubUser.getAvatarUrl());
			user.setEmails(new HashSet<>(verifiedEmails));
			user.setGithubLogin(githubUser.getLogin());

			return user;

		} catch (IOException fail) {
			throw new RuntimeException(fail);
		}
	}

	public User getCurrentAdmin(HttpServletRequest request, String code) {
		User admin = getCurrentUser(request, code);
		admin.setAdmin(admin.getEmails().stream().anyMatch(e -> e.endsWith("@pivotal.io")));
		return admin;
	}

	@Override
	public List<String> createPullRequestHooks(String accessToken, List<String> repositoryIds, String githubEventUrl)
			throws IOException {
		GitHubClient client = createClient(accessToken);
		RepositoryService service = new RepositoryService(client);
		List<String> hookUrls = new ArrayList<>();

		for (String repository : repositoryIds) {
			AccessToken token = new AccessToken();
			token.setGithubId(repository);
			token.setToken(accessToken);

			tokenRepo.save(token);

			EventsRepositoryHook hook = createHook(githubEventUrl);
			RepositoryHook createdHook = service.createHook(RepositoryId.createFromId(repository), hook);
			String hookUrl = createdHook.getUrl();
			hookUrls.add(hookUrl);
		}
		return hookUrls;
	}

	private EventsRepositoryHook createHook(String url) {
		Map<String, String> config = new HashMap<>();
		config.put("url", url);
		config.put("content_type", "json");
		EventsRepositoryHook hook = new EventsRepositoryHook();
		hook.setActive(true);
		hook.addEvent("pull_request");
		hook.setName("web");
		hook.setConfig(config);
		return hook;
	}
}
