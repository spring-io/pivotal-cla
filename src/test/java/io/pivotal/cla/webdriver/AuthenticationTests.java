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
package io.pivotal.cla.webdriver;

import io.pivotal.cla.config.ClaOAuthConfig;
import io.pivotal.cla.data.User;
import io.pivotal.cla.security.WithAdminUserFactory;
import io.pivotal.cla.security.WithSigningUser;
import io.pivotal.cla.security.WithSigningUserFactory;
import io.pivotal.cla.webdriver.pages.SignClaPage;
import io.pivotal.cla.webdriver.pages.admin.AdminLinkClaPage;
import okhttp3.mockwebserver.EnqueueRequests;
import okhttp3.mockwebserver.EnqueueResourcesMockWebServer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.test.context.TestSecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationTests extends BaseWebDriverTests {

	@Rule
	public EnqueueResourcesMockWebServer server = new EnqueueResourcesMockWebServer();

	@SpyBean
	ClaOAuthConfig config;

	private ClientRegistration claAdminRegistration = ClientRegistration.withRegistrationId("cla-admin")
			.clientId("client-main-id-123")
			.clientSecret("client-main-secret-abc")
			.clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.redirectUriTemplate("http://localhost/login/oauth2/github")
			.scope("user:email", "repo:status", "admin:repo_hook", "admin:org_hook", "read:org")
			.authorizationUri("https://github.com/login/oauth/authorize")
			.tokenUri(server.getServer().url("/login/oauth/access_token").toString())
			.userInfoUri(server.getServer().url("/user").toString())
			.userNameAttributeName("id")
			.clientName("GitHub Admin")
			.build();

	private ClientRegistration claUserRegistration = ClientRegistration.withRegistrationId("cla-user")
			.clientId("client-main-id-123")
			.clientSecret("client-main-secret-abc")
			.clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.redirectUriTemplate("http://localhost/login/oauth2/github")
			.scope("user:email")
			.authorizationUri("https://github.com/login/oauth/authorize")
			.tokenUri(server.getServer().url("/login/oauth/access_token").toString())
			.userInfoUri(server.getServer().url("/user").toString())
			.userNameAttributeName("id")
			.clientName("GitHub")
			.build();


	@Before
	public void setup() {
		super.setup();
		when(clientRegistrationRepository.findByRegistrationId(anyString())).thenAnswer(invocation -> {
			String registrationId = invocation.getArgument(0);
			return "cla-admin".equals(registrationId) ? this.claAdminRegistration : this.claUserRegistration;
		});
		when(config.getGitHubApiBaseUrl()).thenReturn(server.getServer().url("/").toString());
	}

	@Test
	public void webjarsPublic() throws Exception {
		mockMvc.perform(get("/webjars/bootstrap/css/bootstrap.min.css"))
			.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void requiresAuthenticationAndRedirectsForAuthorization() throws Exception {
		mockMvc.perform(get("/sign/pivotal"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://localhost/oauth2/authorization/cla-user"));
	}

	@Test
	public void adminRequiresAuthenticationAndRedirectsForAuthorization() throws Exception {
		mockMvc.perform(get("/admin/cla/link"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://localhost/oauth2/authorization/cla-admin"));
	}

	@Test
	@EnqueueRequests({"getAccessToken", "getUserInfo", "notAuthor"})
	public void savedRequestUsed() throws Exception {
		User user = WithAdminUserFactory.create();

		when(mockGitHub.getVerifiedEmails(any())).thenReturn(user.getEmails());
		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla));

		AdminLinkClaPage page = AdminLinkClaPage.to(getDriver());
		page.assertAt();
	}

	@Test
	@EnqueueRequests({"getAccessToken", "getUserInfo"})
	public void authenticateUser() throws Exception {
		User user = WithSigningUserFactory.create();
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockGitHub.getVerifiedEmails(any())).thenReturn(user.getEmails());

		SignClaPage claPage = SignClaPage.go(driver, cla.getName());
		claPage.assertAt();
	}

	@Test
	@EnqueueRequests({"getAccessToken", "getUserInfo", "notAuthor"})
	public void authenticateAdmin() throws Exception {
		User user = WithAdminUserFactory.create();

		when(mockGitHub.getVerifiedEmails(any())).thenReturn(user.getEmails());

		AdminLinkClaPage admin = AdminLinkClaPage.to(driver);
		admin.assertAt();
	}

	@SuppressWarnings("unchecked")
	@Test
	@EnqueueRequests({"getAccessToken", "getUserInfo", "notAuthor"})
	public void userUrlWithAdminUserThenAdminUrl() throws Exception {
		// Add scope 'repo:status' to enable admin user
		ClientRegistration claUserRegistration = ClientRegistration.withRegistrationId("cla-user")
				.clientId("client-main-id-123")
				.clientSecret("client-main-secret-abc")
				.clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.redirectUriTemplate("http://localhost/login/oauth2/github")
				.scope("user:email", "repo:status")
				.authorizationUri("https://github.com/login/oauth/authorize")
				.tokenUri(server.getServer().url("/login/oauth/access_token").toString())
				.userInfoUri(server.getServer().url("/user").toString())
				.userNameAttributeName("id")
				.clientName("GitHub")
				.build();
		when(clientRegistrationRepository.findByRegistrationId(anyString())).thenReturn(claUserRegistration);

		User currentUser = WithAdminUserFactory.create();

		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockGitHub.getVerifiedEmails(any())).thenReturn(currentUser.getEmails());

		when(mockIndividualSignatureRepository.findByEmailIn(anySet())).thenReturn(Collections.emptyList());

		SignClaPage signClaPage = SignClaPage.go(driver, cla.getName());
		signClaPage.assertAt();

		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla));
		when(mockGitHub.findRepositoryNamesWithAdminPermission(anyString())).thenReturn(Arrays.asList("test/this"));

		AdminLinkClaPage admin = AdminLinkClaPage.to(driver);
		admin.assertAt();
	}

	@Test
	@WithSigningUser
	@EnqueueRequests({"getAccessToken", "getUserInfo"})
	@SuppressWarnings("unchecked")
	public void signOut() throws Exception {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockIndividualSignatureRepository.findByEmailIn(anySet())).thenReturn(Arrays.asList(individualSignature));

		SignClaPage signClaPage = SignClaPage.go(driver, cla.getName());
		signClaPage.assertAt();

		TestSecurityContextHolder.clearContext();

		SignClaPage signOut = signClaPage.signOut();
		signOut.assertAt();

		signOut.assertLogoutSuccess();
	}
}
