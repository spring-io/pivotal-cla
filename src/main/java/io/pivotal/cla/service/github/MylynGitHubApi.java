/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.pivotal.cla.service.github;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.pivotal.cla.config.ClaOAuthConfig;
import io.pivotal.cla.config.OAuthClientCredentials;
import io.pivotal.cla.data.User;
import io.pivotal.cla.egit.github.core.ContextCommitStatus;
import io.pivotal.cla.egit.github.core.Email;
import io.pivotal.cla.egit.github.core.EventsRepositoryHook;
import io.pivotal.cla.egit.github.core.service.ContextCommitService;
import io.pivotal.cla.egit.github.core.service.EmailService;
import io.pivotal.cla.service.MigratePullRequestStatusRequest;
import lombok.Data;
import lombok.SneakyThrows;

@Component
public class MylynGitHubApi implements GitHubApi {
	private static final String AUTHORIZE_URI = "login/oauth/access_token";
	public final static String CONTRIBUTING_FILE = "CONTRIBUTING";
	public final static String ADMIN_MAIL_SUFFIX = "@pivotal.io";

	ClaOAuthConfig oauthConfig;

	String authorizeUrl;

	RestTemplate rest = new RestTemplate();

	@Autowired
	public MylynGitHubApi(ClaOAuthConfig oauthConfig) {
		super();
		this.oauthConfig = oauthConfig;
		this.authorizeUrl = oauthConfig.getGitHubBaseUrl() + AUTHORIZE_URI;
	}

	@Override
	@SneakyThrows
	public List<String> findRepositoryNames(String accessToken) {
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
		GitHubClient client = new GitHubClient(oauthConfig.getGitHubApiHost(), oauthConfig.getPort(), oauthConfig.getScheme());
		client.setOAuth2Token(accessToken);
		return client;
	}

	@SneakyThrows
	public void save(PullRequestStatus commitStatus) {
		String repoId = commitStatus.getRepoId();
		String accessToken = commitStatus.getAccessToken();
		if (accessToken == null) {
			return;
		}

		String claName ="Contributor License Agreement";
		String thankYou = "Thank you for signing the";
		String pleasSign = "Please sign the";

		boolean success = commitStatus.isSuccess();
		RepositoryId id = RepositoryId.createFromId(repoId);
		GitHubClient client = createClient(accessToken);
		ContextCommitService commitService = new ContextCommitService(client);
		ContextCommitStatus status = new ContextCommitStatus();
		status.setDescription(success ? String.format("%s %s!", thankYou, claName)
				:  String.format("%s %s!", pleasSign, claName));

		status.setState(success ? CommitStatus.STATE_SUCCESS : CommitStatus.STATE_FAILURE);
		status.setContext("ci/pivotal-cla");
		status.setUrl(commitStatus.getUrl());
		status.setTargetUrl(status.getUrl());

		commitService.createStatus(id, commitStatus.getSha(), status);

		String claLinkMarkdown = String.format("[%s](%s)", claName, status.getUrl());
		String userMentionMarkdown = String.format("@%s", commitStatus.getGitHubUsername());

		GitHubClient commentClient = createClient(oauthConfig.getPivotalClaAccessToken());
		IssueService issues = new IssueService(commentClient);
		List<String> claUserComments = getCommentsByClaUser(issues, id, commitStatus);

		if(success) {

			String body = String.format("%s %s %s!", userMentionMarkdown, thankYou, claLinkMarkdown);
			if(claUserComments.contains(body)) {
				return;
			}
			issues.createComment(id, commitStatus.getPullRequestId(), body);
		} else {
			String body = String.format("%s %s %s!", userMentionMarkdown, pleasSign, claLinkMarkdown);
			if(claUserComments.contains(body)) {
				return;
			}
			issues.createComment(id, commitStatus.getPullRequestId(), body);
		}
	}

	@SneakyThrows
	public String getShaForPullRequest(PullRequestStatus commitStatus) {
		String repositoryId = commitStatus.getRepoId();
		int pullRequestId = commitStatus.getPullRequestId();
		String currentUserGitHubLogin = commitStatus.getGitHubUsername();

		String accessToken = commitStatus.getAccessToken();
		if (accessToken == null) {
			return null;
		}
		GitHubClient client = createClient(accessToken);
		RepositoryId id = RepositoryId.createFromId(repositoryId);

		PullRequestService service = new PullRequestService(client);
		PullRequest pullRequest = service.getPullRequest(id, pullRequestId);
		if (!pullRequest.getUser().getLogin().equals(currentUserGitHubLogin)) {
			return null;
		}

		return pullRequest.getHead().getSha();
	}

	@Override
	@SneakyThrows
	public List<PullRequestStatus> createUpdatePullRequestStatuses(
			MigratePullRequestStatusRequest request) {
		GitHubClient client = createClient(request.getAccessToken());
		PullRequestService pullRequestService = new PullRequestService(client);
		String commitStatusUrl = request.getCommitStatusUrl();
		String accessToken = request.getAccessToken();
		List<PullRequestStatus> results = new ArrayList<>();

		for(String repositoryId : request.getRepositoryIds()) {
			RepositoryId repository = RepositoryId.createFromId(repositoryId);
			List<PullRequest> repositoryPullRequests = pullRequestService.getPullRequests(repository, "open");

			for(PullRequest pullRequest : repositoryPullRequests) {
				PullRequestStatus status = new PullRequestStatus();
				String sha = pullRequest.getHead().getSha();
				status.setPullRequestId(pullRequest.getNumber());
				status.setRepoId(repositoryId);
				status.setSha(sha);
				status.setGitHubUsername(pullRequest.getUser().getLogin());
				status.setUrl(commitStatusUrl);
				status.setAccessToken(accessToken);

				results.add(status);
			}
		}
		return results;
	}

	@SneakyThrows
	private List<String> getCommentsByClaUser(IssueService issues, RepositoryId id, PullRequestStatus commitStatus) {
		String username = getCurrentGitHubUser(oauthConfig.getPivotalClaAccessToken()).getLogin();
		List<Comment> comments = issues.getComments(id, commitStatus.getPullRequestId());
		return comments.stream()
				.filter( c -> username.equals(c.getUser().getLogin()))
				.map(c-> c.getBody())
				.collect(Collectors.toList());
	}

	public User getCurrentUser(CurrentUserRequest request) {
		AccessTokenRequest tokenRequest = new AccessTokenRequest();
		tokenRequest.setCredentials(oauthConfig.getMain());
		tokenRequest.setOauthParams(request.getOauthParams());
		String accessToken = getToken(tokenRequest);

		EmailService emailService = EmailService.forOAuth(accessToken, oauthConfig);
		List<String> verifiedEmails = emailService.getEmails().stream().filter(e -> e.isVerified())
				.map(Email::getEmail).collect(Collectors.toList());
		org.eclipse.egit.github.core.User currentGitHubUser = getCurrentGitHubUser(accessToken);

		User user = new User();
		user.setName(currentGitHubUser.getName());
		user.setAccessToken(accessToken);
		user.setAvatarUrl(currentGitHubUser.getAvatarUrl());
		user.setEmails(new TreeSet<>(verifiedEmails));
		user.setGitHubLogin(currentGitHubUser.getLogin());
		user.setAdminAccessRequested(request.isRequestAdminAccess());
		boolean isAdmin = request.isRequestAdminAccess() && hasAdminEmail(user);
		user.setAdmin(isAdmin);
		if(isAdmin) {
			boolean isClaAuthor = isAuthor(user.getGitHubLogin(), accessToken);
			user.setClaAuthor(isClaAuthor);
		}
		return user;
	}

	private String getToken(AccessTokenRequest request) {
		OAuthAccessTokenParams oauthParams = request.getOauthParams();
		Map<String, String> params = new HashMap<String, String>();
		OAuthClientCredentials credentials = request.getCredentials();

		params.put("client_id", credentials.getClientId());
		params.put("client_secret", credentials.getClientSecret());
		params.put("code", oauthParams.getCode());
		params.put("state", oauthParams.getState());
		params.put("redirect_url", oauthParams.getCallbackUrl());

		ResponseEntity<AccessTokenResponse> token = rest.postForEntity(this.authorizeUrl, params, AccessTokenResponse.class);

		return token.getBody().getAccessToken();
	}

	@Data
	static class AccessTokenResponse {
		@JsonProperty("access_token")
		private String accessToken;
		@JsonProperty("token_type")
		private String tokenType;
		private String scope;

	}

	@SneakyThrows
	private org.eclipse.egit.github.core.User getCurrentGitHubUser(String accessToken) {
		GitHubClient client = createClient(accessToken);

		org.eclipse.egit.github.core.service.UserService githubUsers = new org.eclipse.egit.github.core.service.UserService(
				client);
		return githubUsers.getUser();
	}

	@SneakyThrows
	public List<String> getOrganizations(String username) {
		OrganizationService orgs = new OrganizationService(createClient(oauthConfig.getPivotalClaAccessToken()));
		List<org.eclipse.egit.github.core.User> organizations = orgs.getOrganizations(username);
		return organizations.stream().map( o -> o.getLogin()).sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
	}

	private boolean hasAdminEmail(User user) {
		return user.getEmails().stream().anyMatch(e -> e.endsWith(ADMIN_MAIL_SUFFIX));
	}

	private boolean isAuthor(String username, String accessToken) {
		try {
			ResponseEntity<String> entity = rest.getForEntity(oauthConfig.getGitHubApiBaseUrl() + "/teams/{id}/memberships/{username}?access_token={token}", String.class, "2006839", username, accessToken);
			return entity.getStatusCode().value() == 200;
		} catch(HttpClientErrorException e) {
			if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
				return false;
			}
			throw e;
		}
	}

	@Override
	@SneakyThrows
	public List<String> createPullRequestHooks(CreatePullRequestHookRequest request)
			{
		String accessToken = request.getAccessToken();
		List<String> repositoryIds = request.getRepositoryIds();
		String gitHubEventUrl = request.getGitHubEventUrl();

		GitHubClient client = createClient(accessToken);
		RepositoryService service = new RepositoryService(client);
		List<String> hookUrls = new ArrayList<>();

		for (String repository : repositoryIds) {
			RepositoryId repositoryId = RepositoryId.createFromId(repository);
			EventsRepositoryHook hook = createHook(gitHubEventUrl, request.getSecret());

			List<RepositoryHook> hooks = service.getHooks(repositoryId);
			Optional<RepositoryHook> optional = hooks.stream().filter(h -> hasUrl(hook, gitHubEventUrl)).findFirst();

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
		response.setMarkdown(new ArrayList<>(mdUrls.values()));
		response.setAsciidoc(new ArrayList<>(adocUrls.values()));
		response.getAsciidoc().addAll(newUrls);

		return response;
	}

	@Override
	@SneakyThrows
	public String markdownToHtml(String accessToken, String markdown) {
		MarkdownService markdownService = new MarkdownService(createClient(accessToken));
		return markdownService.getHtml(markdown, "gfm");
	}

	private Map<String,String> createEditLinks(Collection<String> repoIds, String fileName) {
		Map<String,String> urls = new HashMap<>();
		for(String id : repoIds) {
			String url = oauthConfig.getGitHubBaseUrl() + id +"/edit/master/" + fileName;
			if(urlExists(url)) {
				urls.put(id, url);
			}
		}
		return urls;
	}

	private List<String> createNewLinks(Collection<String> repoIds, String fileName) {
		List<String> urls = new ArrayList<>();
		for(String id : repoIds) {
			String url = oauthConfig.getGitHubBaseUrl() + id +"/new/master?filename=" + fileName;
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
