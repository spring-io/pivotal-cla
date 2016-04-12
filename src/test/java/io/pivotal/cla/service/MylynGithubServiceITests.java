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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.pivotal.cla.config.ClaOAuthConfig;
import io.pivotal.cla.data.AccessToken;
import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.AccessTokenRepository;
import okhttp3.mockwebserver.EnqueueResourcesMockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * @author Rob Winch
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class MylynGithubServiceITests {
	@Rule
	public final EnqueueResourcesMockWebServer server = new EnqueueResourcesMockWebServer();

	@Mock
	AccessTokenRepository tokenRepository;
	@Mock
	AccessTokenService tokenService;

	ClaOAuthConfig oauthConfig;

	MylynGithubService service;

	@Before
	public void setup() throws IOException {
		oauthConfig = new ClaOAuthConfig();
		oauthConfig.setScheme("http");
		oauthConfig.setHost(server.getServer().getHostName());
		oauthConfig.setPort(server.getServer().getPort());
		oauthConfig.setPivotalClaAccessToken("pivotal-cla-accessToken");

		service = new MylynGithubService(tokenRepository, oauthConfig, tokenService);
	}

	@Test
	public void findRepositoryNames() throws Exception {
		String accessToken = "accessToken";

		List<String> respositoryNames = service.findRepositoryNames(accessToken);

		assertThat(respositoryNames).containsOnly("spring-projects/spring-security",
				"spring-projects/spring-framework");

		RecordedRequest request = server.getServer().takeRequest();

		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath()).isEqualTo("/api/v3/user/repos?per_page=100&page=1");
		assertThat(request.getHeader("Authorization")).isEqualTo("token " + accessToken);
	}

	@Test
	public void getCurrentUserAdminRequestedButNotAdmin() throws Exception {
		when(tokenService.getToken(any())).thenReturn("access-token-123");

		OAuthAccessTokenParams oauthParams = new OAuthAccessTokenParams();
		oauthParams.setCallbackUrl("https://example.com/oauth/callback");
		oauthParams.setCode("code-123");
		oauthParams.setState("state-456");
		CurrentUserRequest userRequest = new CurrentUserRequest();
		userRequest.setOauthParams(oauthParams);
		userRequest.setRequestAdminAccess(true);

		User user = service.getCurrentUser(userRequest);

		assertThat(user.getAccessToken()).isEqualTo("access-token-123");
		assertThat(user.getAvatarUrl()).isEqualTo("https://avatars.githubusercontent.com/u/362503?v=3");
		assertThat(user.getEmails()).containsOnly("rob@example.com");
		assertThat(user.getGithubLogin()).isEqualTo("rwinch");
		assertThat(user.getName()).isEqualTo("Rob Winch");
		assertThat(user.isAdminAccessRequested()).isTrue();
		assertThat(user.isAdmin()).isFalse();

		RecordedRequest request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/user/emails?per_page=100&page=1");
		assertThat(request.getHeader("Authorization")).isEqualTo("token " + user.getAccessToken());

		request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/user");
		assertThat(request.getHeader("Authorization")).isEqualTo("token " + user.getAccessToken());
	}


	@Test
	public void getCurrentUserSigning() throws Exception {
		when(tokenService.getToken(any())).thenReturn("access-token-123");

		OAuthAccessTokenParams oauthParams = new OAuthAccessTokenParams();
		oauthParams.setCallbackUrl("https://example.com/oauth/callback");
		oauthParams.setCode("code-123");
		oauthParams.setState("state-456");
		CurrentUserRequest userRequest = new CurrentUserRequest();
		userRequest.setOauthParams(oauthParams);
		userRequest.setRequestAdminAccess(false);

		User user = service.getCurrentUser(userRequest);

		assertThat(user.getAccessToken()).isEqualTo("access-token-123");
		assertThat(user.getAvatarUrl()).isEqualTo("https://avatars.githubusercontent.com/u/362503?v=3");
		assertThat(user.getEmails()).containsOnly("rob@example.com");
		assertThat(user.getGithubLogin()).isEqualTo("rwinch");
		assertThat(user.getName()).isEqualTo("Rob Winch");
		assertThat(user.isAdminAccessRequested()).isFalse();
		assertThat(user.isAdmin()).isFalse();

		RecordedRequest request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/user/emails?per_page=100&page=1");
		assertThat(request.getHeader("Authorization")).isEqualTo("token " + user.getAccessToken());

		request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/user");
		assertThat(request.getHeader("Authorization")).isEqualTo("token " + user.getAccessToken());
	}

	@Test
	public void getOrganizations() throws Exception {
		assertThat(service.getOrganizations("rwinch")).containsExactly("asciidoctor","cla-test","spring-projects");
	}

	@Test
	public void createPullRequestHooks() throws Exception {
		CreatePullRequestHookRequest hookRequest = new CreatePullRequestHookRequest();
		hookRequest.setAccessToken("access-token-123");
		hookRequest.setGithubEventUrl("https://example.com/github/hook");
		hookRequest.setRepositoryIds(Arrays.asList("spring-projects/spring-security","spring-projects/spring-session"));
		hookRequest.setSecret("do not guess me");

		List<String> hooks = service.createPullRequestHooks(hookRequest);

		assertThat(hooks).containsOnly("https://github.com/spring-projects/spring-security/settings/hooks/123",
				"https://github.com/spring-projects/spring-session/settings/hooks/456");

		RecordedRequest request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/repos/spring-projects/spring-security/hooks?per_page=100&page=1");

		request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("POST");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/repos/spring-projects/spring-security/hooks");
		assertThat(request.getHeader("Authorization")).isEqualTo("token " + hookRequest.getAccessToken());
		assertThat(request.getBody().readUtf8()).isEqualTo(
				"{\"events\":[\"pull_request\"],\"active\":true,\"created_at\":null,\"updated_at\":null,\"id\":0,\"last_response\":null,\"name\":\"web\",\"url\":null,\"config\":{\"content_type\":\"json\",\"secret\":\"do not guess me\",\"url\":\"https://example.com/github/hook\"}}");

		request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/repos/spring-projects/spring-session/hooks?per_page=100&page=1");

		request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("POST");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/repos/spring-projects/spring-session/hooks");
		assertThat(request.getHeader("Authorization")).isEqualTo("token " + hookRequest.getAccessToken());
		assertThat(request.getBody().readUtf8()).isEqualTo(
				"{\"events\":[\"pull_request\"],\"active\":true,\"created_at\":null,\"updated_at\":null,\"id\":0,\"last_response\":null,\"name\":\"web\",\"url\":null,\"config\":{\"content_type\":\"json\",\"secret\":\"do not guess me\",\"url\":\"https://example.com/github/hook\"}}");
	}

	@Test
	public void doNotCreatePullRequestHooksTwice() throws Exception {
		CreatePullRequestHookRequest hookRequest = new CreatePullRequestHookRequest();
		hookRequest.setAccessToken("access-token-123");
		hookRequest.setGithubEventUrl("https://example.com/github/hook");
		hookRequest.setRepositoryIds(Arrays.asList("spring-projects/spring-security"));
		hookRequest.setSecret("do not guess me");

		List<String> hooks = service.createPullRequestHooks(hookRequest);

		assertThat(hooks).containsOnly("https://github.com/spring-projects/spring-security/settings/hooks/123");

		RecordedRequest request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath()).isEqualTo("/api/v3/repos/spring-projects/spring-security/hooks?per_page=100&page=1");
		assertThat(request.getHeader("Authorization")).isEqualTo("token " + hookRequest.getAccessToken());

		assertThat(server.getServer().getRequestCount()).isEqualTo(1);
	}

	@Test
	public void enableInactivePullRequestHook() throws Exception {
		CreatePullRequestHookRequest hookRequest = new CreatePullRequestHookRequest();
		hookRequest.setAccessToken("access-token-123");
		hookRequest.setGithubEventUrl("https://example.com/github/hook");
		hookRequest.setRepositoryIds(Arrays.asList("spring-projects/spring-security"));
		hookRequest.setSecret("do not guess me");

		List<String> hooks = service.createPullRequestHooks(hookRequest);

		assertThat(hooks).containsOnly("https://github.com/spring-projects/spring-security/settings/hooks/123");

		RecordedRequest request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath()).isEqualTo("/api/v3/repos/spring-projects/spring-security/hooks?per_page=100&page=1");

		request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("POST");
		assertThat(request.getPath()).isEqualTo("/api/v3/repos/spring-projects/spring-security/hooks/123");
		assertThat(request.getHeader("Authorization")).isEqualTo("token " + hookRequest.getAccessToken());
		assertThat(request.getBody().readUtf8()).isEqualTo(
				"{\"active\":true,\"created_at\":\"2011-09-06T17:26:27Z\",\"updated_at\":\"2011-09-06T20:39:23Z\",\"id\":123,\"last_response\":null,\"name\":\"web\",\"url\":\"https://api.github.com/repos/spring-projects/spring-security/hooks/123\",\"config\":{\"url\":\"https://example.com/github/hook\",\"content_type\":\"json\"}}");
	}

	@Test
	public void saveSuccessAlreadyCommented() throws Exception {
		AccessToken token = new AccessToken();
		token.setToken("access-token-123");
		when(tokenRepository.findOne(anyString())).thenReturn(token);

		CommitStatus commitStatus = new CommitStatus();
		commitStatus.setGithubUsername("rwinch");
		commitStatus.setPullRequestId(1);
		commitStatus.setRepoId("spring-projects/spring-security");
		commitStatus.setSha("14f7eed929c0086d5d7b87d28bc4722f618a361f");
		commitStatus.setSuccess(true);
		commitStatus.setUrl("https://status.example.com/uri");

		service.save(commitStatus);

		assertThat(server.getServer().getRequestCount()).isEqualTo(3);

		RecordedRequest request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("POST");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/repos/spring-projects/spring-security/statuses/14f7eed929c0086d5d7b87d28bc4722f618a361f");
		assertThat(request.getHeader("Authorization")).isEqualTo("token " + token.getToken());
		assertThat(request.getBody().readUtf8()).isEqualTo(
				"{\"context\":\"ci/pivotal-cla\",\"description\":\"Thank you for signing the Contributor License Agreement!\",\"state\":\"success\",\"target_url\":\"https://status.example.com/uri\"}");

		request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/user");
		assertThat(request.getHeader("Authorization")).isEqualTo("token pivotal-cla-accessToken");

		request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/repos/spring-projects/spring-security/issues/1/comments?per_page=100&page=1");
		assertThat(request.getHeader("Authorization")).isEqualTo("token pivotal-cla-accessToken");
	}

	@Test
	public void saveSuccessNoComments() throws Exception {
		AccessToken token = new AccessToken();
		token.setToken("access-token-123");
		when(tokenRepository.findOne(anyString())).thenReturn(token);

		CommitStatus commitStatus = new CommitStatus();
		commitStatus.setGithubUsername("rwinch");
		commitStatus.setPullRequestId(1);
		commitStatus.setRepoId("spring-projects/spring-security");
		commitStatus.setSha("14f7eed929c0086d5d7b87d28bc4722f618a361f");
		commitStatus.setSuccess(true);
		commitStatus.setUrl("https://status.example.com/uri");

		service.save(commitStatus);

		assertThat(server.getServer().getRequestCount()).isEqualTo(4);

		RecordedRequest request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("POST");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/repos/spring-projects/spring-security/statuses/14f7eed929c0086d5d7b87d28bc4722f618a361f");
		assertThat(request.getHeader("Authorization")).isEqualTo("token " + token.getToken());
		assertThat(request.getBody().readUtf8()).isEqualTo(
				"{\"context\":\"ci/pivotal-cla\",\"description\":\"Thank you for signing the Contributor License Agreement!\",\"state\":\"success\",\"target_url\":\"https://status.example.com/uri\"}");

		request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/user");
		assertThat(request.getHeader("Authorization")).isEqualTo("token pivotal-cla-accessToken");

		request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/repos/spring-projects/spring-security/issues/1/comments?per_page=100&page=1");
		assertThat(request.getHeader("Authorization")).isEqualTo("token pivotal-cla-accessToken");

		request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("POST");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/repos/spring-projects/spring-security/issues/1/comments");
		assertThat(request.getHeader("Authorization")).isEqualTo("token pivotal-cla-accessToken");
		assertThat(request.getBody().readUtf8()).isEqualTo(
				"{\"body\":\"@rwinch Thank you for signing the [Contributor License Agreement](https://status.example.com/uri)!\"}");
	}

	@Test
	public void saveFailureAlreadyCommented() throws Exception {
		AccessToken token = new AccessToken();
		token.setToken("access-token-123");
		when(tokenRepository.findOne(anyString())).thenReturn(token);

		CommitStatus commitStatus = new CommitStatus();
		commitStatus.setGithubUsername("rwinch");
		commitStatus.setPullRequestId(1);
		commitStatus.setRepoId("spring-projects/spring-security");
		commitStatus.setSha("14f7eed929c0086d5d7b87d28bc4722f618a361f");
		commitStatus.setSuccess(false);
		commitStatus.setUrl("https://status.example.com/uri");

		service.save(commitStatus);

		assertThat(server.getServer().getRequestCount()).isEqualTo(3);

		RecordedRequest request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("POST");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/repos/spring-projects/spring-security/statuses/14f7eed929c0086d5d7b87d28bc4722f618a361f");
		assertThat(request.getHeader("Authorization")).isEqualTo("token " + token.getToken());
		assertThat(request.getBody().readUtf8()).isEqualTo(
				"{\"context\":\"ci/pivotal-cla\",\"description\":\"Please sign the Contributor Licenese Agreement!\",\"state\":\"failure\",\"target_url\":\"https://status.example.com/uri\"}");

		request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/user");
		assertThat(request.getHeader("Authorization")).isEqualTo("token pivotal-cla-accessToken");

		request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/repos/spring-projects/spring-security/issues/1/comments?per_page=100&page=1");
		assertThat(request.getHeader("Authorization")).isEqualTo("token pivotal-cla-accessToken");
	}

	@Test
	public void saveFailureNoComments() throws Exception {
		AccessToken token = new AccessToken();
		token.setToken("access-token-123");
		when(tokenRepository.findOne(anyString())).thenReturn(token);

		CommitStatus commitStatus = new CommitStatus();
		commitStatus.setGithubUsername("rwinch");
		commitStatus.setPullRequestId(1);
		commitStatus.setRepoId("spring-projects/spring-security");
		commitStatus.setSha("14f7eed929c0086d5d7b87d28bc4722f618a361f");
		commitStatus.setSuccess(false);
		commitStatus.setUrl("https://status.example.com/uri");

		service.save(commitStatus);

		assertThat(server.getServer().getRequestCount()).isEqualTo(4);

		RecordedRequest request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("POST");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/repos/spring-projects/spring-security/statuses/14f7eed929c0086d5d7b87d28bc4722f618a361f");
		assertThat(request.getHeader("Authorization")).isEqualTo("token " + token.getToken());
		assertThat(request.getBody().readUtf8()).isEqualTo(
				"{\"context\":\"ci/pivotal-cla\",\"description\":\"Please sign the Contributor Licenese Agreement!\",\"state\":\"failure\",\"target_url\":\"https://status.example.com/uri\"}");

		request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/user");
		assertThat(request.getHeader("Authorization")).isEqualTo("token pivotal-cla-accessToken");

		request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/repos/spring-projects/spring-security/issues/1/comments?per_page=100&page=1");
		assertThat(request.getHeader("Authorization")).isEqualTo("token pivotal-cla-accessToken");

		request = server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("POST");
		assertThat(request.getPath())
				.isEqualTo("/api/v3/repos/spring-projects/spring-security/issues/1/comments");
		assertThat(request.getHeader("Authorization")).isEqualTo("token pivotal-cla-accessToken");
		assertThat(request.getBody().readUtf8()).isEqualTo(
				"{\"body\":\"@rwinch Please sign the [Contributor License Agreement](https://status.example.com/uri)!\"}");
	}

	@Test
	public void getContributingUrls() {
		List<String> repositoryIds = Arrays.asList("spring-projects/has-md", "spring-projects/has-adoc", "spring-projects/no-contributor");

		ContributingUrlsResponse urls = service.getContributingUrls(repositoryIds);

		assertThat(urls.getAsciidoc()).containsExactly(
				server.getServerUrl() + "/spring-projects/has-adoc/edit/master/CONTRIBUTING.adoc",
				server.getServerUrl() + "/spring-projects/no-contributor/new/master?filename=CONTRIBUTING.adoc");
		assertThat(urls.getMarkdown()).containsOnly(server.getServerUrl() + "/spring-projects/has-md/edit/master/CONTRIBUTING.md");
	}

	@Test
	public void markdownToHtml() {
		String accessToken = "access-token-123";
		String markdown = "Hello world github/linguist#1 **cool**, and #1!";

		String html = service.markdownToHtml(accessToken, markdown);

		assertThat(html).isEqualTo("<p>Hello world <a href=\"http://github.com/github/linguist/issues/1\" class=\"issue-link\" title=\"This is a simple issue\">github/linguist#1</a> <strong>cool</strong>, and <a href=\"http://github.com/github/gollum/issues/1\" class=\"issue-link\" title=\"This is another issue\">#1</a>!</p>");
	}
}
