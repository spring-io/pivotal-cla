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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.CommitStatus;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryHook;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.MarkdownService;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import io.pivotal.cla.config.ClaOAuthConfig;
import io.pivotal.cla.data.AccessToken;
import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.AccessTokenRepository;
import io.pivotal.cla.egit.github.core.ContextCommitStatus;
import io.pivotal.cla.egit.github.core.Email;
import io.pivotal.cla.egit.github.core.EventsRepositoryHook;
import io.pivotal.cla.egit.github.core.service.ContextCommitService;
import io.pivotal.cla.egit.github.core.service.EmailService;

@Component
public class MylynGithubService implements GitHubService {

	public final static String CONTRIBUTING_FILE = "CONTRIBUTING";
	public final static String ADMIN_MAIL_SUFFIX = "@pivotal.io";

	AccessTokenRepository tokenRepo;

	ClaOAuthConfig oauthConfig;

	AccessTokenService tokenService;

	RestTemplate rest = new RestTemplate();

	@Autowired
	public MylynGithubService(AccessTokenRepository tokenRepo, ClaOAuthConfig oauthConfig,
			AccessTokenService tokenService) {
		super();
		this.tokenRepo = tokenRepo;
		this.oauthConfig = oauthConfig;
		this.tokenService = tokenService;
	}

	@Override
	public List<String> findRepositoryNames(String accessToken) throws IOException {
		GitHubClient client = createClient(accessToken);

		RepositoryService service = new RepositoryService(client);
		List<Repository> repositories = service.getRepositories();
		List<String> repoSlugs = new ArrayList<>();
		for (Repository r : repositories) {
			org.eclipse.egit.github.core.User owner = r.getOwner();
			repoSlugs.add(owner.getLogin() + "/" + r.getName());
		}
		return repoSlugs;
	}

	private GitHubClient createClient(String accessToken) {
		GitHubClient client = new GitHubClient(oauthConfig.getHost(), oauthConfig.getPort(), oauthConfig.getScheme());
		client.setOAuth2Token(accessToken);
		return client;
	}

	public void save(io.pivotal.cla.service.CommitStatus commitStatus) {
		String repoId = commitStatus.getRepoId();
		AccessToken token = tokenRepo.findOne(repoId);
		if (token == null) {
			return;
		}

		String claName ="Contributor License Agreement";
		String thankYou = "Thank you for signing the";
		String pleasSign = "Please sign the";

		boolean success = commitStatus.isSuccess();
		RepositoryId id = RepositoryId.createFromId(repoId);
		GitHubClient client = createClient(token.getToken());
		ContextCommitService commitService = new ContextCommitService(client);
		ContextCommitStatus status = new ContextCommitStatus();
		status.setDescription(success ? String.format("%s %s!", thankYou, claName)
				:  String.format("%s %s!", pleasSign, claName));

		status.setState(success ? CommitStatus.STATE_SUCCESS : CommitStatus.STATE_FAILURE);
		status.setContext("ci/pivotal-cla");
		status.setUrl(commitStatus.getUrl());
		status.setTargetUrl(status.getUrl());

		try {
			commitService.createStatus(id, commitStatus.getSha(), status);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		String claLinkMarkdown = String.format("[%s](%s)", claName, status.getUrl());
		String userMentionMarkdown = String.format("@%s", commitStatus.getGithubUsername());

		GitHubClient commentClient = createClient(oauthConfig.getPivotalClaAccessToken());
		IssueService issues = new IssueService(commentClient);
		List<String> claUserComments = getCommentsByClaUser(issues, id, commitStatus);

		if(success) {

			String body = String.format("%s %s %s!", userMentionMarkdown, thankYou, claLinkMarkdown);
			if(claUserComments.contains(body)) {
				return;
			}
			try {
				issues.createComment(id, commitStatus.getPullRequestId(), body);
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			String body = String.format("%s %s %s!", userMentionMarkdown, pleasSign, claLinkMarkdown);
			if(claUserComments.contains(body)) {
				return;
			}
			try {
				issues.createComment(id, commitStatus.getPullRequestId(), body);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void save(UpdatePullRequestStatusRequest pullShaRequest) throws IOException {
		String repositoryId = pullShaRequest.getRepositoryId();
		int pullRequestId = pullShaRequest.getPullRequestId();
		String currentUserGithubLogin = pullShaRequest.getCurrentUserGithubLogin();

		AccessToken token = tokenRepo.findOne(repositoryId);
		if(token == null) {
			return;
		}
		GitHubClient client = new GitHubClient();
		client.setOAuth2Token(token.getToken());
		RepositoryId id = RepositoryId.createFromId(repositoryId);

		PullRequestService service = new PullRequestService(client);
		PullRequest pullRequest = service.getPullRequest(id, pullRequestId);
		if (!pullRequest.getUser().getLogin().equals(currentUserGithubLogin)) {
			return;
		}

		String sha = pullRequest.getHead().getSha();
		io.pivotal.cla.service.CommitStatus status = new io.pivotal.cla.service.CommitStatus();
		status.setPullRequestId(pullRequest.getNumber());
		status.setRepoId(repositoryId);
		status.setSha(sha);
		status.setSuccess(true);
		status.setGithubUsername(currentUserGithubLogin);

		save(status);
	}

	private List<String> getCommentsByClaUser(IssueService issues, RepositoryId id, io.pivotal.cla.service.CommitStatus commitStatus) {

		try {
			String username = getCurrentGithubUser(oauthConfig.getPivotalClaAccessToken()).getLogin();
			List<Comment> comments = issues.getComments(id, commitStatus.getPullRequestId());
			return comments.stream()
					.filter( c -> username.equals(c.getUser().getLogin()))
					.map(c-> c.getBody())
					.collect(Collectors.toList());
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public User getCurrentUser(CurrentUserRequest request) {
		AccessTokenRequest tokenRequest = new AccessTokenRequest();
		tokenRequest.setCredentials(oauthConfig.getMain());
		tokenRequest.setOauthParams(request.getOauthParams());
		String accessToken = tokenService.getToken(tokenRequest);
		try {

			EmailService emailService = EmailService.forOAuth(accessToken, oauthConfig);
			List<String> verifiedEmails = emailService.getEmails().stream().filter(e -> e.isVerified())
					.map(Email::getEmail).collect(Collectors.toList());
			org.eclipse.egit.github.core.User githubUser = getCurrentGithubUser(accessToken);

			User user = new User();
			user.setName(githubUser.getName());
			user.setAccessToken(accessToken);
			user.setAvatarUrl(githubUser.getAvatarUrl());
			user.setEmails(new TreeSet<>(verifiedEmails));
			user.setGithubLogin(githubUser.getLogin());
			user.setAdminAccessRequested(request.isRequestAdminAccess());
			user.setAdmin(request.isRequestAdminAccess() && hasAdminEmail(user));
			return user;

		} catch (IOException fail) {
			throw new RuntimeException(fail);
		}
	}

	private org.eclipse.egit.github.core.User getCurrentGithubUser(String accessToken) throws IOException {
		GitHubClient client = createClient(accessToken);

		org.eclipse.egit.github.core.service.UserService githubUsers = new org.eclipse.egit.github.core.service.UserService(
				client);
		return githubUsers.getUser();
	}

	public List<String> getOrganizations(String username) throws IOException {
		OrganizationService orgs = new OrganizationService(createClient(oauthConfig.getPivotalClaAccessToken()));
		List<org.eclipse.egit.github.core.User> organizations = orgs.getOrganizations(username);
		return organizations.stream().map( o -> o.getLogin()).sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
	}

	private boolean hasAdminEmail(User user) {
		return user.getEmails().stream().anyMatch(e -> e.endsWith(ADMIN_MAIL_SUFFIX));
	}

	@Override
	public List<String> createPullRequestHooks(CreatePullRequestHookRequest request)
			throws IOException {
		String accessToken = request.getAccessToken();
		List<String> repositoryIds = request.getRepositoryIds();
		String githubEventUrl = request.getGithubEventUrl();

		GitHubClient client = createClient(accessToken);
		RepositoryService service = new RepositoryService(client);
		List<String> hookUrls = new ArrayList<>();

		for (String repository : repositoryIds) {
			AccessToken token = new AccessToken();
			token.setId(repository);
			token.setToken(accessToken);

			tokenRepo.save(token);

			RepositoryId repositoryId = RepositoryId.createFromId(repository);
			EventsRepositoryHook hook = createHook(githubEventUrl, request.getSecret());

			List<RepositoryHook> hooks = service.getHooks(repositoryId);
			Optional<RepositoryHook> optional = hooks.stream().filter(h -> hasUrl(hook, githubEventUrl)).findFirst();

			long hookId;
			if (optional.isPresent()) {
				// we must always update because the secret is not exposed
				hook.setId(optional.get().getId());
				hook.setActive(true);
				RepositoryHook editHook = service.editHook(repositoryId, hook);
				hookId = editHook.getId();
			} else {
				RepositoryHook createdHook = service.createHook(repositoryId, hook);
				hookId = createdHook.getId();
			}

			hookUrls.add("https://github.com/" + repository + "/settings/hooks/" + hookId);
		}

		return hookUrls;
	}

	private boolean hasUrl(RepositoryHook hook, String githubEventUrl) {

		if(hook.getConfig() != null){
			if(githubEventUrl.endsWith(hook.getConfig().get("url"))){
				return true;
			}
		}

		return false;
	}

	public ContributingUrlsResponse getContributingUrls(List<String> repositoryIds) {
		Set<String> remainingRepositoryIds = new LinkedHashSet<>(repositoryIds);

		Map<String,String> mdUrls = createEditLinks(remainingRepositoryIds, String.format("%s.md", CONTRIBUTING_FILE));
		remainingRepositoryIds.removeAll(mdUrls.keySet());
		Map<String,String> adocUrls = createEditLinks(remainingRepositoryIds, String.format("%s.adoc", CONTRIBUTING_FILE));
		remainingRepositoryIds.removeAll(adocUrls.keySet());
		List<String> newUrls = createNewLinks(remainingRepositoryIds, String.format("%s.adoc", CONTRIBUTING_FILE));

		ContributingUrlsResponse response = new ContributingUrlsResponse();
		response.setMarkdown(mdUrls.values());
		response.setAsciidoc(new ArrayList<>(adocUrls.values()));
		response.getAsciidoc().addAll(newUrls);

		return response;
	}

	@Override
	public String markdownToHtml(String accessToken, String markdown) {
		MarkdownService markdownService = new MarkdownService(createClient(accessToken));
		try {
			return markdownService.getHtml(markdown, "gfm");
		} catch(IOException e) {
			throw new RuntimeException("Couldn't convert markdown", e);
		}
	}

	private Map<String,String> createEditLinks(Collection<String> repoIds, String fileName) {
		Map<String,String> urls = new HashMap<>();
		for(String id : repoIds) {
			String url = oauthConfig.getBaseUrl() + id +"/edit/master/" + fileName;
			if(urlExists(url)) {
				urls.put(id, url);
			}
		}
		return urls;
	}

	private List<String> createNewLinks(Collection<String> repoIds, String fileName) {
		List<String> urls = new ArrayList<>();
		for(String id : repoIds) {
			String url = oauthConfig.getBaseUrl() + id +"/new/master?filename=" + fileName;
			urls.add(url);
		}
		return urls;
	}

	private boolean urlExists(String url) {
		try {
			rest.getForEntity(url, String.class);
			return true;
		}catch(HttpClientErrorException notFound) {
			return false;
		}
	}

	private EventsRepositoryHook createHook(String url, String secret) {
		Map<String, String> config = new HashMap<>();
		config.put("url", url);
		config.put("content_type", "json");
		config.put("secret", secret);
		EventsRepositoryHook hook = new EventsRepositoryHook();
		hook.setActive(true);
		hook.addEvent("pull_request");
		hook.setName("web");
		hook.setConfig(config);
		return hook;
	}
}
