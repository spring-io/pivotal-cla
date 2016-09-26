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
package io.pivotal.cla.mvc.github;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.StreamUtils;

import io.pivotal.cla.data.AccessToken;
import io.pivotal.cla.data.User;
import io.pivotal.cla.egit.github.core.PullRequestId;
import io.pivotal.cla.egit.github.core.event.GithubEvents;
import io.pivotal.cla.security.GitHubSignature;
import io.pivotal.cla.security.WithSigningUserFactory;
import io.pivotal.cla.service.github.GitHubApi;
import io.pivotal.cla.service.github.PullRequestStatus;
import io.pivotal.cla.webdriver.BaseWebDriverTests;
import lombok.SneakyThrows;

public class GitHubHooksControllerTests extends BaseWebDriverTests {

	AccessToken accessToken;

	@Autowired
	GitHubSignature oauth;

	@Autowired
	GitHubApi gitHubApiMock;

	@Before
	public void setupAccessToken() {
		accessToken = new AccessToken(AccessToken.CLA_ACCESS_TOKEN_ID, "GitHubHooksControllerTests_access_token");
		when(mockTokenRepo.findOne(AccessToken.CLA_ACCESS_TOKEN_ID)).thenReturn(accessToken);
	}

	@Test
	public void ping() throws Exception {
		mockMvc.perform(hookRequest().header("X-GitHub-Event", "ping").content(getPayload("pull_request.json")))
				.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void pingNoToken() throws Exception {
		accessToken = null;

		mockMvc.perform(hookRequest().header("X-GitHub-Event", "ping").content(getPayload("pull_request.json")))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void userNeverAuthenticated() throws Exception {
		when(mockTokenRepo.findOne("rwinch/176_test"))
				.thenReturn(new AccessToken("rwinch/176_test", "mock_access_token_value"));

		mockMvc.perform(hookRequest().header("X-GitHub-Event", GithubEvents.PULL_REQUEST).content(getPayload("pull_request.json")))
			.andExpect(status().isOk());

		ArgumentCaptor<PullRequestStatus> statusCaptor = ArgumentCaptor.forClass(PullRequestStatus.class);
		verify(mockGitHub).save(statusCaptor.capture());

		PullRequestStatus status = statusCaptor.getValue();
		assertThat(status.getRepoId()).isEqualTo("rwinch/176_test");
		assertThat(status.getAccessToken()).isEqualTo("mock_access_token_value");
		assertThat(status.getPullRequestId()).isEqualTo(2);
		assertThat(status.getSha()).isEqualTo("a6befb598a35c1c206e1bf7bbb3018f4403b9610");
		assertThat(status.getUrl()).isEqualTo("http://localhost/sign/pivotal?repositoryId=rwinch/176_test&pullRequestId=2");
		assertThat(status.getFaqUrl()).endsWith("/faq");
		assertThat(status.getSyncUrl()).contains("/sync/pivotal?repositoryId=rwinch/176_test&pullRequestId=2");
		assertThat(status.isSuccess()).isFalse();
	}

	@Test
	public void userNeverAuthenticatedNoToken() throws Exception {
		accessToken = null;

		when(mockTokenRepo.findOne("rwinch/176_test"))
			.thenReturn(new AccessToken("rwinch/176_test", "mock_access_token_value"));

		mockMvc.perform(hookRequest().header("X-GitHub-Event", GithubEvents.PULL_REQUEST).content(getPayload("pull_request.json")))
			.andExpect(status().isUnauthorized());
	}

	@Test
	public void gitHubAccessTokenIsNull() throws Exception {
		mockMvc.perform(hookRequest()
				.header("X-GitHub-Event", GithubEvents.PULL_REQUEST)
				.content(getPayload("pull_request.json")))
				.andExpect(status().isOk());
	}

	@Test
	public void gitHubAccessTokenIsNullNoToken() throws Exception {
		accessToken = null;

		mockMvc.perform(hookRequest()
				.header("X-GitHub-Event", GithubEvents.PULL_REQUEST)
				.content(getPayload("pull_request.json")))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void rejectUnknownEventPayload() throws Exception {

		User user = WithSigningUserFactory.create();
		when(mockUserRepo.findOne(anyString())).thenReturn(user);
		when(mockTokenRepo.findOne("rwinch/176_test"))
			.thenReturn(new AccessToken("rwinch/176_test", "mock_access_token_value"));
		when(mockIndividualSignatureRepository.findSignaturesFor(any(), any(), anyString())).thenReturn(Arrays.asList(individualSignature));

		mockMvc.perform(hookRequest().header("X-GitHub-Event", "unknown").content(getPayload("pull_request.json")))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void skipOwnRequests() throws Exception {

		User user = WithSigningUserFactory.create();
		when(mockUserRepo.findOne(anyString())).thenReturn(user);
		when(mockTokenRepo.findOne("rwinch/176_test"))
			.thenReturn(new AccessToken("rwinch/176_test", "mock_access_token_value"));
		when(gitHubApiMock.getGitHubClaUserLogin()).thenReturn("robwinch");
		when(mockIndividualSignatureRepository.findSignaturesFor(any(), any(), anyString())).thenReturn(Arrays.asList(individualSignature));

		mockMvc.perform(hookRequest().header("X-GitHub-Event", GithubEvents.PULL_REQUEST_REVIEW_COMMENT)
				.content(getPayload("pull_request_review_comment.json"))).andExpect(status().isOk());

		verify(gitHubApiMock).getGitHubClaUserLogin();
		verifyNoMoreInteractions(gitHubApiMock);
	}

	@Test
	public void skipRequestsWithoutPullRequest() throws Exception {
		User user = WithSigningUserFactory.create();
		when(mockUserRepo.findOne(anyString())).thenReturn(user);
		when(mockTokenRepo.findOne("rwinch/176_test"))
			.thenReturn(new AccessToken("rwinch/176_test", "mock_access_token_value"));
		when(mockIndividualSignatureRepository.findSignaturesFor(any(), any(), anyString())).thenReturn(Arrays.asList(individualSignature));

		mockMvc.perform(hookRequest().header("X-GitHub-Event", GithubEvents.PULL_REQUEST).content(getPayload("issue.json")))
			.andExpect(status().isBadRequest());

		verifyZeroInteractions(mockGitHub);
	}

	@Test
	public void markCommitStatusSuccessIndividual() throws Exception {
		User user = WithSigningUserFactory.create();
		when(mockUserRepo.findOne(anyString())).thenReturn(user);
		when(mockTokenRepo.findOne("rwinch/176_test"))
			.thenReturn(new AccessToken("rwinch/176_test", "mock_access_token_value"));
		when(mockIndividualSignatureRepository.findSignaturesFor(any(), any(), anyString())).thenReturn(Arrays.asList(individualSignature));

		mockMvc.perform(hookRequest().header("X-GitHub-Event", GithubEvents.PULL_REQUEST).content(getPayload("pull_request.json")))
			.andExpect(status().isOk());

		ArgumentCaptor<PullRequestStatus> statusCaptor = ArgumentCaptor.forClass(PullRequestStatus.class);
		verify(mockGitHub).save(statusCaptor.capture());

		PullRequestStatus status = statusCaptor.getValue();
		assertThat(status.getRepoId()).isEqualTo("rwinch/176_test");
		assertThat(status.getAccessToken()).isEqualTo("mock_access_token_value");
		assertThat(status.getPullRequestId()).isEqualTo(2);
		assertThat(status.getSha()).isEqualTo("a6befb598a35c1c206e1bf7bbb3018f4403b9610");
		assertThat(status.getUrl()).isEqualTo("http://localhost/sign/pivotal?repositoryId=rwinch/176_test&pullRequestId=2");
		assertThat(status.isSuccess()).isTrue();
		assertThat(status.getGitHubUsername()).isEqualTo(user.getGitHubLogin());
		assertThat(status.getPullRequestBody()).isNotEmpty();
	}

	@Test
	public void markCommitStatusSuccessIndividualAcceptingIssueComments() throws Exception {

		User user = WithSigningUserFactory.create();
		when(mockUserRepo.findOne(anyString())).thenReturn(user);
		when(mockTokenRepo.findOne("rwinch/176_test"))
			.thenReturn(new AccessToken("rwinch/176_test", "mock_access_token_value"));
		when(mockIndividualSignatureRepository.findSignaturesFor(any(), any(), anyString())).thenReturn(Arrays.asList(individualSignature));
		when(gitHubApiMock.getShaForPullRequest(any(PullRequestId.class))).thenReturn("a6befb598a35c1c206e1bf7bbb3018f4403b9610");

		mockMvc.perform(hookRequest().header("X-GitHub-Event", GithubEvents.ISSUE_COMMENT).content(getPayload("issue_comment.json")))
			.andExpect(status().isOk());

		ArgumentCaptor<PullRequestStatus> statusCaptor = ArgumentCaptor.forClass(PullRequestStatus.class);
		verify(mockGitHub).save(statusCaptor.capture());

		PullRequestStatus status = statusCaptor.getValue();
		assertThat(status.getRepoId()).isEqualTo("rwinch/176_test");
		assertThat(status.getAccessToken()).isEqualTo("mock_access_token_value");
		assertThat(status.getPullRequestId()).isEqualTo(2);
		assertThat(status.getSha()).isEqualTo("a6befb598a35c1c206e1bf7bbb3018f4403b9610");
		assertThat(status.getUrl()).isEqualTo("http://localhost/sign/pivotal?repositoryId=rwinch/176_test&pullRequestId=2");
		assertThat(status.isSuccess()).isTrue();
		assertThat(status.getGitHubUsername()).isEqualTo(user.getGitHubLogin());
	}

	@Test
	public void markCommitStatusSuccessIndividualAcceptingReviewComments() throws Exception {

		User user = WithSigningUserFactory.create();
		when(mockUserRepo.findOne(anyString())).thenReturn(user);
		when(mockTokenRepo.findOne("rwinch/176_test"))
			.thenReturn(new AccessToken("rwinch/176_test", "mock_access_token_value"));
		when(mockIndividualSignatureRepository.findSignaturesFor(any(), any(), anyString())).thenReturn(Arrays.asList(individualSignature));

		mockMvc.perform(hookRequest().header("X-GitHub-Event", GithubEvents.PULL_REQUEST_REVIEW_COMMENT).content(getPayload("pull_request_review_comment.json")))
			.andExpect(status().isOk());

		ArgumentCaptor<PullRequestStatus> statusCaptor = ArgumentCaptor.forClass(PullRequestStatus.class);
		verify(mockGitHub).save(statusCaptor.capture());

		PullRequestStatus status = statusCaptor.getValue();
		assertThat(status.getRepoId()).isEqualTo("rwinch/176_test");
		assertThat(status.getAccessToken()).isEqualTo("mock_access_token_value");
		assertThat(status.getPullRequestId()).isEqualTo(2);
		assertThat(status.getSha()).isEqualTo("a6befb598a35c1c206e1bf7bbb3018f4403b9610");
		assertThat(status.getUrl()).isEqualTo("http://localhost/sign/pivotal?repositoryId=rwinch/176_test&pullRequestId=2");
		assertThat(status.isSuccess()).isTrue();
		assertThat(status.getGitHubUsername()).isEqualTo(user.getGitHubLogin());
		assertThat(status.getPullRequestBody()).isNotEmpty();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void markCommitStatusSuccessCorporate() throws Exception {
		User user = WithSigningUserFactory.create();
		when(mockUserRepo.findOne(anyString())).thenReturn(user);
		when(mockTokenRepo.findOne("rwinch/176_test"))
			.thenReturn(new AccessToken("rwinch/176_test", "mock_access_token_value"));
		when(mockGitHub.getOrganizations(anyString())).thenReturn(Arrays.asList("organization"));
		when(mockCorporateSignatureRepository.findSignature(anyString(), anySet(), anyCollectionOf(String.class))).thenReturn(corporateSignature);

		mockMvc.perform(hookRequest().header("X-GitHub-Event", GithubEvents.PULL_REQUEST).content(getPayload("pull_request.json")))
			.andExpect(status().isOk());

		ArgumentCaptor<PullRequestStatus> statusCaptor = ArgumentCaptor.forClass(PullRequestStatus.class);
		verify(mockGitHub).save(statusCaptor.capture());

		PullRequestStatus status = statusCaptor.getValue();
		assertThat(status.getRepoId()).isEqualTo("rwinch/176_test");
		assertThat(status.getAccessToken()).isEqualTo("mock_access_token_value");
		assertThat(status.getPullRequestId()).isEqualTo(2);
		assertThat(status.getSha()).isEqualTo("a6befb598a35c1c206e1bf7bbb3018f4403b9610");
		assertThat(status.getUrl()).isEqualTo("http://localhost/sign/pivotal?repositoryId=rwinch/176_test&pullRequestId=2");
		assertThat(status.isSuccess()).isTrue();
		assertThat(status.getGitHubUsername()).isEqualTo(user.getGitHubLogin());
	}

	@Test
	public void markCommitStatusBadRequest() throws Exception {

		mockMvc.perform(hookRequest().content(""))
			.andExpect(status().isBadRequest());
	}

	@SneakyThrows
	private byte[] getPayload(String id) {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(getClass().getSimpleName() + "/" + id)) {
			return StreamUtils.copyToByteArray(is);
		}
	}

	private MockHttpServletRequestBuilder hookRequest() {
		MockHttpServletRequestBuilder post = post("/github/hooks/pull_request/pivotal").with(new RequestPostProcessor() {

			@Override
			public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
				if(accessToken != null) {
					try {
						String signature = getSignature(request);
						request.addHeader("X-Hub-Signature", signature);
					} catch(Exception e) {
						throw new RuntimeException(e);
					}
				}
				return request;
			}

			private String getSignature(MockHttpServletRequest request)
					throws IOException, UnsupportedEncodingException, Exception {
				String body = IOUtils.toString(request.getReader());
				String signature = oauth.create(body, accessToken.getToken());
				return signature;
			}
		});

		return post;
	}
}
