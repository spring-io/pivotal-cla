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
package io.pivotal.cla.mvc.github;

import static io.pivotal.cla.egit.github.core.event.GithubEvents.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import io.pivotal.cla.egit.github.core.PullRequestId;
import io.pivotal.cla.egit.github.core.event.RepositoryAware;
import io.pivotal.cla.egit.github.core.event.RepositoryIssueCommentPayload;
import io.pivotal.cla.egit.github.core.event.RepositoryPullRequestPayload;
import io.pivotal.cla.egit.github.core.event.RepositoryPullRequestReviewCommentPayload;
import io.pivotal.cla.egit.github.core.event.SenderAware;
import io.pivotal.cla.mvc.util.UrlBuilder;
import io.pivotal.cla.service.ClaPullRequestStatusRequest;
import io.pivotal.cla.service.ClaService;
import io.pivotal.cla.service.github.GitHubApi;
import io.pivotal.cla.service.github.PullRequestStatus;

@RestController
@PreAuthorize("@gitHubSignature.check(#request.getHeader('X-Hub-Signature'), #body)")
public class GitHubHooksController {

	private static final Set<String> ACCEPTED_EVENTS = new HashSet<>(
			Arrays.asList(ISSUE_COMMENT, PULL_REQUEST_REVIEW_COMMENT, PULL_REQUEST));
	private static final Map<String, Class<? extends RepositoryAware>> PAYLOAD_TYPES = new HashMap<>();

	static {
		PAYLOAD_TYPES.put(ISSUE_COMMENT, RepositoryIssueCommentPayload.class);
		PAYLOAD_TYPES.put(PULL_REQUEST_REVIEW_COMMENT,
				RepositoryPullRequestReviewCommentPayload.class);
		PAYLOAD_TYPES.put(PULL_REQUEST, RepositoryPullRequestPayload.class);
	}

	@Autowired
	ClaService claService;
	@Autowired
	GitHubApi gitHubApi;

	@RequestMapping(value = "/github/hooks/pull_request/{cla}", headers = "X-GitHub-Event=ping")
	public String pullRequestPing(HttpServletRequest request, @RequestBody String body,
			@PathVariable String cla) throws Exception {
		return "SUCCESS";
	}

	/**
	 * @param request
	 * @param body
	 * @param cla
	 * @param githubEvent
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/github/hooks/pull_request/{cla}")
	public ResponseEntity<String> pullRequest(HttpServletRequest request,
			@RequestBody String body, @PathVariable String cla,
			@RequestHeader("X-GitHub-Event") String githubEvent) throws Exception {

		if (!ACCEPTED_EVENTS.contains(githubEvent)) {
			return ResponseEntity.badRequest().body(
					String.format("X-Github-Event: %s not acceptable", githubEvent));
		}

		Gson gson = GsonUtils.createGson();
		RepositoryAware payload = gson.fromJson(body, PAYLOAD_TYPES.get(githubEvent));

		PullRequest pullRequest = getPullRequest(payload);

		if (pullRequest == null) {
			return ResponseEntity.badRequest().body("Not related to a Pull request");
		}

		User senderUser = getSender(payload);

		if (senderUser.getLogin().equals(gitHubApi.getGitHubClaUserLogin())) {
			return ResponseEntity.ok("Skipping self-events");
		}

		User user = getPullRequestUser(payload);

		Repository repository = payload.getRepository();
		RepositoryId repoId = RepositoryId.createFromId(
				repository.getOwner().getLogin() + "/" + repository.getName());
		String sha = getPullRequestSha(repoId, pullRequest);
		String gitHubLogin = user.getLogin();

		PullRequestStatus status = new PullRequestStatus();
		status.setGitHubUsername(gitHubLogin);
		status.setPullRequestId(pullRequest.getNumber());
		status.setPullRequestBody(pullRequest.getBody());
		status.setRepoId(repoId.generateId());
		status.setSha(sha);
		String signUrl = UrlBuilder.signUrl().request(request).claName(cla)
				.repositoryId(status.getRepoId()).pullRequestId(status.getPullRequestId())
				.build();
		status.setUrl(signUrl);
		status.setPullRequestState(pullRequest.getState());

		String syncUrl = UrlBuilder.createSyncUrl(request, cla, status.getRepoId(),
				status.getPullRequestId());
		status.setSyncUrl(syncUrl);

		String faqUrl = UrlBuilder.createAboutUrl(request);
		status.setFaqUrl(faqUrl);

		ClaPullRequestStatusRequest pullRequestStatusRequest = new ClaPullRequestStatusRequest();
		pullRequestStatusRequest.setClaName(cla);
		pullRequestStatusRequest.setCommitStatus(status);
		claService.savePullRequestStatus(pullRequestStatusRequest);

		return ResponseEntity.ok("SUCCESS");
	}

	private String getPullRequestSha(RepositoryId repoId, PullRequest pullRequest) {

		if (pullRequest.getHead() != null) {
			return pullRequest.getHead().getSha();
		}

		return gitHubApi
				.getShaForPullRequest(PullRequestId.of(repoId, pullRequest.getNumber()));
	}

	private User getPullRequestUser(RepositoryAware payload) {

		if (payload instanceof RepositoryIssueCommentPayload) {
			Issue issue = ((RepositoryIssueCommentPayload) payload).getIssue();
			if (issue != null && issue.getPullRequest() != null) {
				return issue.getUser();
			}
		}

		PullRequest pullRequest = getPullRequest(payload);
		if (pullRequest != null && pullRequest.getUser() != null) {
			return pullRequest.getUser();
		}

		throw new IllegalStateException("Cannot determine User from payload");
	}

	private User getSender(RepositoryAware payload) {

		if (payload instanceof SenderAware) {
			return ((SenderAware) payload).getSender();
		}

		throw new IllegalStateException("Cannot determine Sender from payload");
	}

	private PullRequest getPullRequest(RepositoryAware payload) {

		PullRequest pullRequest = null;
		if (payload instanceof RepositoryPullRequestPayload) {
			pullRequest = ((RepositoryPullRequestPayload) payload).getPullRequest();
		}

		if (payload instanceof RepositoryPullRequestReviewCommentPayload) {
			pullRequest = ((RepositoryPullRequestReviewCommentPayload) payload)
					.getPullRequest();
		}

		if (payload instanceof RepositoryIssueCommentPayload) {
			Issue issue = ((RepositoryIssueCommentPayload) payload).getIssue();

			if (issue != null && issue.getPullRequest() != null) {
				pullRequest = issue.getPullRequest();

				pullRequest.setAssignee(issue.getAssignee());
				pullRequest.setComments(issue.getComments());
				pullRequest.setNumber(issue.getNumber());
				pullRequest.setUser(issue.getUser());
				pullRequest.setState(issue.getState());
				pullRequest.setBody(issue.getBody());
				pullRequest.setBodyHtml(issue.getBodyHtml());
				pullRequest.setBodyText(issue.getBodyText());
				pullRequest.setClosedAt(issue.getClosedAt());
				pullRequest.setCreatedAt(issue.getCreatedAt());
				pullRequest.setMilestone(issue.getMilestone());
			}
		}
		return pullRequest;
	}
}
