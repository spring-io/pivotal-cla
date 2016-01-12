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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import io.pivotal.cla.data.ContributorLicenseAgreeement;
import io.pivotal.cla.data.User;
import io.pivotal.cla.security.WithAdminUserFactory;
import io.pivotal.cla.security.WithSigningUserFactory;
import io.pivotal.cla.webdriver.pages.HomePage;
import io.pivotal.cla.webdriver.pages.admin.AdminLinkClaPage;
import io.pivotal.cla.webdriver.pages.admin.AdminListClasPage;

public class SecurityTests extends BaseWebDriverTests {

	@Test
	public void requiresAuthenticationAndCreatesValidOAuthTokenRequest() throws Exception {
		String redirect = mockMvc.perform(get("/"))
				.andExpect(status().is3xxRedirection())
				.andReturn().getResponse().getRedirectedUrl();

		UriComponents redirectComponent = UriComponentsBuilder.fromHttpUrl(redirect).build();

		assertThat(redirectComponent.getScheme()).isEqualTo("https");
		assertThat(redirectComponent.getHost()).isEqualTo("github.com");
		MultiValueMap<String, String> params = redirectComponent.getQueryParams();
		assertThat(params.getFirst("client_id")).isNotNull();
		assertThat(urlDecode(params.getFirst("redirect_uri"))).isEqualTo("http://localhost/login/oauth2/github");
		assertThat(params.getFirst("state")).isNotNull();

		String[] scopes = urlDecode(params.getFirst("scope")).split(",");
		assertThat(scopes).containsOnly("user:email");
	}

	@Test
	public void adminRequiresAuthenticationAndCreatesValidOAuthTokenRequest() throws Exception {
		String redirect = mockMvc.perform(get("/admin/cla/link"))
				.andExpect(status().is3xxRedirection())
				.andReturn().getResponse().getRedirectedUrl();

		UriComponents redirectComponent = UriComponentsBuilder.fromHttpUrl(redirect).build();

		assertThat(redirectComponent.getScheme()).isEqualTo("https");
		assertThat(redirectComponent.getHost()).isEqualTo("github.com");
		MultiValueMap<String, String> params = redirectComponent.getQueryParams();
		assertThat(params.getFirst("client_id")).isNotNull();
		assertThat(urlDecode(params.getFirst("redirect_uri"))).isEqualTo("http://localhost/login/oauth2/github");
		assertThat(params.getFirst("state")).isNotNull();

		String[] scopes = urlDecode(params.getFirst("scope")).split(",");
		assertThat(scopes).containsOnly("user:email", "repo:status", "admin:repo_hook", "admin:org_hook", "read:org");
	}

	@Test
	public void savedRequestUsed() throws Exception {
		User user = WithAdminUserFactory.create();

		when(mockGithub.getCurrentAdmin(any(HttpServletRequest.class), anyString())).thenReturn(user);

		AdminListClasPage page = AdminListClasPage.go(getDriver());
		page.assertAt();
	}

	@Test
	public void authenticates() throws Exception {
		User user = WithSigningUserFactory.create();

		when(mockGithub.getCurrentUser(any(HttpServletRequest.class), anyString())).thenReturn(user);

		HomePage home = HomePage.go(driver);
		home.assertAt();
	}

	@Test
	public void userUrlWithAdminUserThenAdminUrl() throws Exception {
		User currentUser = WithAdminUserFactory.create();
		currentUser.setAdmin(false);

		when(mockGithub.getCurrentUser(any(HttpServletRequest.class), anyString())).thenReturn(currentUser, (User) null);

		HomePage home = HomePage.go(driver);
		home.assertAt();

		verify(mockGithub).getCurrentUser(any(HttpServletRequest.class), anyString());

		User currentAdmin = WithAdminUserFactory.create();
		when(mockGithub.getCurrentAdmin(any(HttpServletRequest.class), anyString())).thenReturn(currentAdmin);

		ContributorLicenseAgreeement cla = new ContributorLicenseAgreeement();
		cla.setName("apache");
		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla));
		when(mockGithub.findRepositoryNames(anyString())).thenReturn(Arrays.asList("test/this"));

		AdminLinkClaPage admin = AdminLinkClaPage.to(driver);
		admin.assertAt();

		verify(mockGithub).getCurrentAdmin(any(HttpServletRequest.class), anyString());
		verify(mockGithub).findRepositoryNames(anyString());

		verifyNoMoreInteractions(mockGithub);
	}

	@Test
	public void loginVerifiesSecretState() throws Exception {
		User currentUser = WithAdminUserFactory.create();
		when(mockGithub.getCurrentUser(any(HttpServletRequest.class), anyString())).thenReturn(currentUser, (User) null);
		MockHttpSession session = new MockHttpSession();
		String redirect = mockMvc.perform(get("/").session(session))
				.andExpect(status().is3xxRedirection())
				.andReturn().getResponse().getRedirectedUrl();

		redirect = mockMvc.perform(get(redirect))
			.andReturn().getResponse().getRedirectedUrl();

		// change the expected secret state
		session.setAttribute("state", "INVALID");
		mockMvc.perform(get(redirect).session(session))
			.andExpect(status().isBadRequest());
	}
}
