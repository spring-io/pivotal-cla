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
package io.pivotal.cla.service.github;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.CommitStatus;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.RepositoryHook;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.MarkdownService;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.pivotal.cla.config.ClaOAuthConfig;
import io.pivotal.cla.config.OAuthClientCredentials;
import io.pivotal.cla.data.User;
import io.pivotal.cla.egit.github.core.ContextCommitStatus;
import io.pivotal.cla.egit.github.core.Email;
import io.pivotal.cla.egit.github.core.EventsRepositoryHook;
import io.pivotal.cla.egit.github.core.PullRequestId;
import io.pivotal.cla.egit.github.core.WithPermissionsRepository;
import io.pivotal.cla.egit.github.core.event.GithubEvents;
import io.pivotal.cla.egit.github.core.service.ContextCommitService;
import io.pivotal.cla.egit.github.core.service.EmailService;
import io.pivotal.cla.egit.github.core.service.WithPermissionsRepositoryService;
import io.pivotal.cla.mvc.util.UrlBuilder;
import io.pivotal.cla.service.MigratePullRequestStatusRequest;
import lombok.Data;
import lombok.SneakyThrows;

/**
 * @author Rob Winch
 * @author Mark Paluch
 */
@Component
public class MylynGitHubApi implements GitHubApi {

	private static final String AUTHORIZE_URI = "login/oauth/access_token";
	public final static String CONTRIBUTING_FILE = "CONTRIBUTING";
	public final static String ADMIN_MAIL_SUFFIX = "@pivotal.io";
	public final static Pattern PULL_REQUEST_CALLBACK_PATTERN = Pattern.compile(".*" + UrlBuilder.pullRequestHookCallbackPath("") + "([a-zA-Z0-9\\-\\s\\%\\+]*)(\\?.*)?");

	public final static String CONTRIBUTOR_LICENSE_AGREEMENT = "Contributor License Agreement";
	public final static String THIS_PR_CONTAINS_AN_OBVIOUS_FIX = "This Pull Request contains an obvious fix";
	public final static String OBVIOUS_FIX_CLA_NOT_REQUIRED = String.format(
			"%s. Signing the %s not necessary.", THIS_PR_CONTAINS_AN_OBVIOUS_FIX, CONTRIBUTOR_LICENSE_AGREEMENT);
	public final static String THANK_YOU = "Thank you for signing the";
	public final static String PLEASE_SIGN = "Please sign the";
	public final static String OBVIOUS_FIX = "obvious fix";
	public final static String TO_MANUALLY_SYNCHRONIZE_THE_STATUS = "to manually synchronize the status of this Pull Request";
	public final static String FREQUENTLY_ASKED_QUESTIONS = "frequently asked questions";

	final ClaOAuthConfig oauthConfig;
	final String authorizeUrl;
	final RestTemplate rest = new RestTemplate();

	@Autowired
	public MylynGitHubApi(ClaOAuthConfig oauthConfig) {
		super();
		this.oauthConfig = oauthConfig;
		this.authorizeUrl = oauthConfig.getGitHubBaseUrl() + AUTHORIZE_URI;
	}

	@Override
	@SneakyThrows
	public List<String> findRepositoryNamesWithAdminPermission(String accessToken) {
		GitHubClient client = createClient(accessToken);

		WithPermissionsRepositoryService service = new WithPermissionsRepositoryService(client);
		List<WithPermissionsRepository> repositories = service.getPermissionRepositories();
		List<String> repoSlugs = new ArrayList<>();
		for (WithPermissionsRepository r : repositories) {
			if(!r.getPermissions().isAdmin()) {
				continue;
			}
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

		if(commitStatus.shouldUpdatePullRequest()) {
			PullRequestId pullRequestId = PullRequestId.of(RepositoryId.createFromId(repoId), commitStatus.getPullRequestId());

			boolean hasSignedCla = commitStatus.isSuccess();
			GitHubClient client = createClient(accessToken);

			String claUserLogin = getGitHubClaUserLogin();
			List<Comment> comments = getComments(pullRequestId, getIssueService());

			boolean obviousFix = isObviousFix(pullRequestId, comments, claUserLogin, commitStatus.getPullRequestBody());

			ContextCommitStatus status = createCommitStatusIfNecessary(pullRequestId, commitStatus, hasSignedCla, obviousFix, client);
			createOrUpdatePullRequestComment(pullRequestId, commitStatus, hasSignedCla, obviousFix, status, comments, claUserLogin);
		}
	}

	/**
	 * Returns whether the pull-request is marked as obvious fix by having an issue/review comment stating it's an obvious fix.
	 * @param pullRequestId
	 * @param comments
	 * @param claUserLogin  @return
	 * @param pullRequestBody
	 */
	private boolean isObviousFix(PullRequestId pullRequestId, List<Comment> comments, String claUserLogin, String pullRequestBody) {

		if (hasObviousFixBody(pullRequestBody)) {
			return true;
		}

		if (hasObviousFixComment(comments, claUserLogin)) {
			return true;
		}

		if (hasObviousFixComment(getComments(pullRequestId, getPullRequestService()), claUserLogin)) {
			return true;
		}

		return false;
	}

	private boolean hasObviousFixBody(String pullRequestBody) {
		return pullRequestBody != null && pullRequestBody.contains(OBVIOUS_FIX);
	}

	private boolean hasObviousFixComment(Collection<? extends Comment> comments, String claUserLogin) {

		Optional<? extends Comment> obviousFixComment = comments.stream() //
				.filter(comment -> comment.getUser() != null && !claUserLogin.equals(comment.getUser().getLogin())) //
				.filter(comment -> comment.getBody().contains(OBVIOUS_FIX)) //
				.findFirst();

		return obviousFixComment.isPresent();
	}

	private ContextCommitStatus createCommitStatusIfNecessary(PullRequestId pullRequestId, PullRequestStatus commitStatus,
			boolean hasSignedCla, boolean obviousFix, GitHubClient client) {

		ContextCommitService commitService = new ContextCommitService(client);
		ContextCommitStatus status = new ContextCommitStatus();

		String description;

		if (obviousFix) {
			description = OBVIOUS_FIX_CLA_NOT_REQUIRED;
		} else if (hasSignedCla) {
			description = String.format("%s %s!", THANK_YOU, CONTRIBUTOR_LICENSE_AGREEMENT);
		} else {
			description = String.format("%s %s!", PLEASE_SIGN, CONTRIBUTOR_LICENSE_AGREEMENT);
		}

		status.setDescription(description);

		status.setState((hasSignedCla || obviousFix) ? CommitStatus.STATE_SUCCESS : CommitStatus.STATE_FAILURE);
		status.setContext("ci/pivotal-cla");
		status.setUrl(commitStatus.getUrl());
		status.setTargetUrl(status.getUrl());

		List<ContextCommitStatus> statuses = commitService.getContextStatuses(pullRequestId.getRepositoryId(),
				commitStatus.getSha());
		if (!statuses.stream().anyMatch(s -> matches(status, s))) {
			commitService.createStatus(pullRequestId.getRepositoryId(), commitStatus.getSha(), status);
		}
		return status;
	}

	private void createOrUpdatePullRequestComment(PullRequestId pullRequestId, PullRequestStatus commitStatus,
			boolean hasSignedCla, boolean obviousFix, ContextCommitStatus status, List<Comment> comments, String claUserLogin)
			throws IOException {

		String claLinkMarkdown = String.format("[%s](%s)", CONTRIBUTOR_LICENSE_AGREEMENT, status.getUrl());
		String userMentionMarkdown = String.format("@%s", commitStatus.getGitHubUsername());

		IssueService issues = getIssueService();
		List<Comment> claUserComments = comments.stream() //
				.filter(comment -> comment.getUser().getLogin().equals(claUserLogin)) //
				.collect(Collectors.toList());

		if (hasSignedCla) {

			String body = String.format("%s %s %s!", userMentionMarkdown, THANK_YOU, claLinkMarkdown);
			if (claUserComments.stream().anyMatch(c -> c.getBody().contains(PLEASE_SIGN))) {

				if (claUserComments.stream().anyMatch(c -> c.getBody().contains(THANK_YOU))) {
					return;
				}

				issues.createComment(pullRequestId.getRepositoryId(), commitStatus.getPullRequestId(), body);
			}
		} else {

			String sync = String.format("\n\n[Click here](%s) %s.", commitStatus.getSyncUrl(),
					TO_MANUALLY_SYNCHRONIZE_THE_STATUS);
			String faq = String.format("\n\nSee the [FAQ](%s) for %s.", commitStatus.getFaqUrl(), FREQUENTLY_ASKED_QUESTIONS);
			String oldBody = String.format("%s %s %s!", userMentionMarkdown, PLEASE_SIGN, claLinkMarkdown);
			String body = String.format("%s%s%s", oldBody, sync, faq);

			if (obviousFix) {
				if(claUserComments.stream().anyMatch(c -> c.getBody().contains(PLEASE_SIGN)) &&
						claUserComments .stream().noneMatch(comment -> comment.getBody().contains(THIS_PR_CONTAINS_AN_OBVIOUS_FIX))) {
					createObviousFixCommentIfNecessary(pullRequestId, userMentionMarkdown, issues, claUserComments);
				}
				return;
			}

			if (claUserComments.stream().anyMatch(c -> c.getBody().contains(FREQUENTLY_ASKED_QUESTIONS)
					&& c.getBody().contains(TO_MANUALLY_SYNCHRONIZE_THE_STATUS))) {
				return;
			}

			Optional<Comment> oldComment = claUserComments.stream().filter(c -> c.getBody().trim().contains(PLEASE_SIGN))
					.findFirst();
			if (oldComment.isPresent()) {
				Comment toEdit = oldComment.get();
				toEdit.setBody(body);
				issues.editComment(pullRequestId.getRepositoryId(), toEdit);
			} else {
				issues.createComment(pullRequestId.getRepositoryId(), pullRequestId.getId(), body);
			}
		}
	}

	/**
	 * Add a "disarming" comment.
	 * @param pullRequestId
	 * @param userMentionMarkdown
	 * @param issues
	 * @param claUserComments
	 * @throws IOException
	 */
	private void createObviousFixCommentIfNecessary(PullRequestId pullRequestId, String userMentionMarkdown,
			IssueService issues, List<Comment> claUserComments) throws IOException {

		// only if not already present and if a comment says "please sign the CLA"
		if (claUserComments.stream().anyMatch(c -> c.getBody().contains(PLEASE_SIGN))
				&& !claUserComments.stream().anyMatch(c -> c.getBody().contains(OBVIOUS_FIX_CLA_NOT_REQUIRED))) {

			if (claUserComments.stream().anyMatch(c -> c.getBody().contains(THANK_YOU))) {
				return;
			}

			String claNotRequiredBody = String.format("%s %s", userMentionMarkdown, OBVIOUS_FIX_CLA_NOT_REQUIRED);
			issues.createComment(pullRequestId.getRepositoryId(), pullRequestId.getId(), claNotRequiredBody);
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
		String githubLoginForContributor = pullRequest.getUser().getLogin();
		if(commitStatus.isAdmin()) {
			commitStatus.setGitHubUsername(githubLoginForContributor);
		}else if (!githubLoginForContributor.equals(currentUserGitHubLogin)) {
			return null;
		}

		return pullRequest.getHead().getSha();
	}

	@SneakyThrows
	public String getShaForPullRequest(PullRequestId pullRequestId) {

		PullRequestService service = getPullRequestService();
		PullRequest pullRequest = service.getPullRequest(pullRequestId.getRepositoryId(),
				pullRequestId.getId());

		if (pullRequest == null) {
			throw new IllegalArgumentException(
					String.format("Cannot find Pull-request %s#%s",
							pullRequestId.getRepositoryId(), pullRequestId.getId()));
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
				String syncUrl = UriComponentsBuilder.fromHttpUrl(request.getBaseSyncUrl())
						.queryParam("repositoryId", repositoryId)
						.queryParam("pullRequestId", pullRequest.getNumber())
						.build()
						.toUriString();
				status.setPullRequestId(pullRequest.getNumber());
				status.setRepoId(repositoryId);
				status.setSha(sha);
				status.setGitHubUsername(pullRequest.getUser().getLogin());
				status.setUrl(commitStatusUrl);
				status.setAccessToken(accessToken);
				status.setFaqUrl(request.getFaqUrl());
				status.setSyncUrl(syncUrl);
				status.setPullRequestState(pullRequest.getState());

				results.add(status);
			}
		}
		return results;
	}

	@SneakyThrows
	private List<Comment> getComments(PullRequestId pullRequestId, IssueService service) {
		return service.getComments(pullRequestId.getRepositoryId(), pullRequestId.getId());
	}

	@SneakyThrows
	private List<CommitComment> getComments(PullRequestId pullRequestId, PullRequestService service) {
		return service.getComments(pullRequestId.getRepositoryId(), pullRequestId.getId());
	}

	public User getCurrentUser(CurrentUserRequest request) {
		AccessTokenRequest tokenRequest = new AccessTokenRequest();
		tokenRequest.setCredentials(oauthConfig.getMain());
		tokenRequest.setOauthParams(request.getOauthParams());
		String accessToken = getToken(tokenRequest);

		Set<String> verifiedEmails = getVerifiedEmails(accessToken);
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

	public Set<String> getVerifiedEmails(String accessToken) {
		EmailService emailService = EmailService.forOAuth(accessToken, oauthConfig);
		return emailService.getEmails().stream().filter(e -> e.isVerified())
				.map(Email::getEmail).collect(Collectors.toSet());
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
		return organizations.stream().map(o -> o.getLogin()).sorted(String.CASE_INSENSITIVE_ORDER).collect(Collectors.toList());
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
			Optional<RepositoryHook> optional = hooks.stream().filter(h -> hasUrl(h, gitHubEventUrl)).findFirst();

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
			if(githubEventUrl.equals(hook.getConfig().get("url"))){
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

	@Override
	@SneakyThrows
	public Set<String> findAssociatedClaNames(String repoId, String accessToken) {

		GitHubClient client = createClient(accessToken);
		RepositoryService service = new RepositoryService(client);

		RepositoryId repositoryId = RepositoryId.createFromId(repoId);

		List<RepositoryHook> hooks = service.getHooks(repositoryId);
		Set<String> claNames = hooks.stream() //
				.filter(h -> StringUtils.hasText(h.getConfig().get("url"))) //
				.filter(RepositoryHook::isActive) //
				.map(h -> h.getConfig().get("url")) //
				.filter(PULL_REQUEST_CALLBACK_PATTERN.asPredicate()) //
				.map(url -> getClaName(url, PULL_REQUEST_CALLBACK_PATTERN)) //
				.collect(Collectors.toSet());

		return claNames;
	}

	public String getGitHubClaUserLogin() {
		return getCurrentGitHubUser(oauthConfig.getPivotalClaAccessToken()).getLogin();
	}

	private IssueService getIssueService() {
		GitHubClient commentClient = createClient(oauthConfig.getPivotalClaAccessToken());
		return new IssueService(commentClient);
	}

	private PullRequestService getPullRequestService() {
		GitHubClient commentClient = createClient(oauthConfig.getPivotalClaAccessToken());
		return new PullRequestService(commentClient);
	}

	private boolean matches(ContextCommitStatus expected, ContextCommitStatus actual) {
		return expected.getContext().equals(actual.getContext()) && expected.getState().equals(actual.getState())
				&& expected.getDescription().equals(actual.getDescription());
	}

	@Override
	@SneakyThrows
	public Optional<PullRequest> findPullRequest(String repoId, int pullRequestId, String accessToken) {

		GitHubClient client = createClient(accessToken);
		PullRequestService service = new PullRequestService(client);

		try {
			return Optional.ofNullable(service.getPullRequest(RepositoryId.createFromId(repoId), pullRequestId));
		}
		catch (RequestException e) {

			if(e.getStatus() == HttpStatus.NOT_FOUND.value()){
				return Optional.empty();
			}

			throw e;
		}
	}

	private String getClaName(String url, Pattern pattern) {

		Matcher matcher = pattern.matcher(url);
		matcher.find();

		return matcher.group(1);
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
		hook.addEvent(GithubEvents.ISSUE_COMMENT);
		hook.addEvent(GithubEvents.PULL_REQUEST);
		hook.addEvent(GithubEvents.PULL_REQUEST_REVIEW_COMMENT);
		hook.setName("web");
		hook.setConfig(config);
		return hook;
	}

	@Data
	private  static class AccessTokenResponse {
		@JsonProperty("access_token")
		String accessToken;
		@JsonProperty("token_type")
		String tokenType;
		String scope;

	}
}
