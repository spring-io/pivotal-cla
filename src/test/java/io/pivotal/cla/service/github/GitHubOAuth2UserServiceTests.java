/*
 * Copyright 2002-2018 the original author or authors.
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

import io.pivotal.cla.config.ClaOAuthConfig;
import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.UserRepository;
import io.pivotal.cla.security.GitHubOAuth2User;
import io.pivotal.cla.security.WithAdminUserFactory;
import io.pivotal.cla.security.WithSigningUserFactory;
import okhttp3.mockwebserver.EnqueueRequests;
import okhttp3.mockwebserver.EnqueueResourcesMockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Joe Grandja
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class GitHubOAuth2UserServiceTests {
	private final String[] userAuthorities = {"ROLE_USER"};
	private final String[] adminAuthorities = {"ROLE_USER", "ROLE_ADMIN"};
	private final String[] authorAuthorities = {"ROLE_USER", "ROLE_ADMIN", "ROLE_CLA_AUTHOR", "ACTUATOR"};

	@Rule
	public final EnqueueResourcesMockWebServer server = new EnqueueResourcesMockWebServer();

	@MockBean
	private GitHubApi gitHubApi;

	@MockBean
	private ClaOAuthConfig oauthConfig;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private ClientRegistrationRepository clientRegistrationRepository;

	@Autowired
	private GitHubOAuth2UserService userService;

	private ClientRegistration claUserRegistration = ClientRegistration.withRegistrationId("cla-user")
			.clientId("client-main-id-123")
			.clientSecret("client-main-secret-abc")
			.clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.redirectUriTemplate("http://localhost/login/oauth2/github")
			.scope("user:email")
			.authorizationUri("https://github.com/login/oauth/authorize")
			.tokenUri("https://github.com/login/oauth/access_token")
			.userInfoUri(this.server.getServer().url("/user").toString())
			.userNameAttributeName("id")
			.clientName("GitHub")
			.build();

	private ClientRegistration claAdminRegistration = ClientRegistration.withRegistrationId("cla-admin")
			.clientId("client-main-id-123")
			.clientSecret("client-main-secret-abc")
			.clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.redirectUriTemplate("http://localhost/login/oauth2/github")
			.scope("user:email", "repo:status", "admin:repo_hook", "admin:org_hook", "read:org")
			.authorizationUri("https://github.com/login/oauth/authorize")
			.tokenUri("https://github.com/login/oauth/access_token")
			.userInfoUri(this.server.getServer().url("/user").toString())
			.userNameAttributeName("id")
			.clientName("GitHub Admin")
			.build();

	@Before
	public void setup() {
		when(this.oauthConfig.getGitHubApiBaseUrl()).thenReturn(this.server.getServer().url("/").toString());
	}

	@Test
	@EnqueueRequests("getUserInfo")
	public void loadUserSigning() throws Exception {
		OAuth2AccessToken accessToken = this.createAccessToken(this.claUserRegistration.getScopes().toArray(new String[0]));
		OAuth2UserRequest userRequest = new OAuth2UserRequest(this.claUserRegistration, accessToken);
		this.loadVerifyUser(userRequest);
	}

	@Test
	@EnqueueRequests("getUserInfo")
	public void loadUserAdminRequestedButNotAdmin() throws Exception {
		OAuth2AccessToken accessToken = this.createAccessToken("repo:status");		// Admin scope - triggers adminAccessRequested
		OAuth2UserRequest userRequest = new OAuth2UserRequest(this.claUserRegistration, accessToken);
		this.loadVerifyUser(userRequest);
	}

	@Test
	@EnqueueRequests({"getUserInfo", "isAuthor"})
	public void loadUserAdminAndClaAuthor() throws Exception {
		OAuth2AccessToken accessToken = this.createAccessToken(this.claAdminRegistration.getScopes().toArray(new String[0]));
		OAuth2UserRequest userRequest = new OAuth2UserRequest(this.claAdminRegistration, accessToken);
		this.loadVerifyAdminUser(userRequest, true);
	}

	@Test
	@EnqueueRequests({"getUserInfo", "notAuthor"})
	public void loadUserAdminAndNotClaAuthor() throws Exception {
		OAuth2AccessToken accessToken = this.createAccessToken(this.claAdminRegistration.getScopes().toArray(new String[0]));
		OAuth2UserRequest userRequest = new OAuth2UserRequest(this.claAdminRegistration, accessToken);
		this.loadVerifyAdminUser(userRequest, false);
	}

	private void loadVerifyUser(OAuth2UserRequest userRequest) throws Exception {
		User regularUser = WithSigningUserFactory.create();
		when(this.gitHubApi.getVerifiedEmails(any())).thenReturn(regularUser.getEmails());

		GitHubOAuth2User user = (GitHubOAuth2User) this.userService.loadUser(userRequest);

		OAuth2AccessToken accessToken = userRequest.getAccessToken();
		boolean isAdminAccessRequested = accessToken.getScopes().stream().anyMatch(s -> s.equals("repo:status"));

		assertThat(user.isAdminAccessRequested()).isEqualTo(isAdminAccessRequested);
		assertThat(user.isAdmin()).isFalse();
		assertThat(user.isClaAuthor()).isFalse();
		assertThat(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
				.containsOnly(this.userAuthorities);
		assertThat(user.getGitHubLogin()).isEqualTo("rwinch");
		assertThat(user.getFullName()).isEqualTo("Rob Winch");
		assertThat(user.getEmails()).containsOnly("rob@gmail.com");
		assertThat(user.getAvatarUrl()).isEqualTo("https://avatars.githubusercontent.com/u/362503?v=3");
		assertThat(user.getAccessToken()).isEqualTo(accessToken.getTokenValue());

		RecordedRequest request = this.server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath()).isEqualTo("/user");
		assertThat(request.getHeader("Authorization")).isEqualTo("Bearer " + user.getAccessToken());
	}

	private void loadVerifyAdminUser(OAuth2UserRequest userRequest, boolean isAuthor) throws Exception {
		User adminUser = WithAdminUserFactory.create();
		when(this.gitHubApi.getVerifiedEmails(any())).thenReturn(adminUser.getEmails());

		GitHubOAuth2User user = (GitHubOAuth2User) this.userService.loadUser(userRequest);

		OAuth2AccessToken accessToken = userRequest.getAccessToken();

		assertThat(user.isAdminAccessRequested()).isTrue();
		assertThat(user.isAdmin()).isTrue();
		assertThat(user.isClaAuthor()).isEqualTo(isAuthor);
		if (isAuthor) {
			assertThat(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
					.containsOnly(this.authorAuthorities);
		} else {
			assertThat(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
					.containsOnly(this.adminAuthorities);
		}
		assertThat(user.getGitHubLogin()).isEqualTo("rwinch");
		assertThat(user.getFullName()).isEqualTo("Rob Winch");
		assertThat(user.getEmails()).containsOnly("rob@pivotal.io");
		assertThat(user.getAvatarUrl()).isEqualTo("https://avatars.githubusercontent.com/u/362503?v=3");
		assertThat(user.getAccessToken()).isEqualTo(accessToken.getTokenValue());

		RecordedRequest request = this.server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath()).isEqualTo("/user");
		assertThat(request.getHeader("Authorization")).isEqualTo("Bearer " + user.getAccessToken());

		request = this.server.getServer().takeRequest();
		assertThat(request.getMethod()).isEqualTo("GET");
		assertThat(request.getPath()).isEqualTo("/teams/2006839/memberships/rwinch?access_token=access-token-123");
	}

	private OAuth2AccessToken createAccessToken(String... scope) {
		Instant issuedAt = Instant.now();
		Instant expiresAt = issuedAt.plusSeconds(300);
		Set<String> scopes = Arrays.stream(scope).collect(Collectors.toSet());
		return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "access-token-123", issuedAt, expiresAt, scopes);
	}
}