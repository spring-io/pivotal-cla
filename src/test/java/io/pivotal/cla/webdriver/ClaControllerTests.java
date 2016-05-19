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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.test.context.support.WithAnonymousUser;

import io.pivotal.cla.data.AccessToken;
import io.pivotal.cla.data.User;
import io.pivotal.cla.security.WithSigningUser;
import io.pivotal.cla.security.WithSigningUserFactory;
import io.pivotal.cla.service.github.UpdatePullRequestStatusRequest;
import io.pivotal.cla.webdriver.pages.AboutPage;
import io.pivotal.cla.webdriver.pages.SignCclaPage;
import io.pivotal.cla.webdriver.pages.SignClaPage;
import io.pivotal.cla.webdriver.pages.SignIclaPage;

@WithSigningUser
public class ClaControllerTests extends BaseWebDriverTests {

	@Before
	public void setup() {
		super.setup();
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
	}

	@Test
	@WithAnonymousUser
	public void viewSignedWithRepositoryIdAndPullRequestIdNewUser() throws Exception {
		String repositoryId = "spring-projects/spring-security";
		User signingUser = WithSigningUserFactory.create();
		when(mockGitHub.getCurrentUser(any())).thenReturn(signingUser);
		when(mockIndividualSignatureRepository.findSignaturesFor(signingUser,cla.getName())).thenReturn(individualSignature);
		when(mockIndividualSignatureRepository.findSignaturesFor(any(),eq(signingUser))).thenReturn(Arrays.asList(individualSignature));
		when(mockTokenRepo.findOne(repositoryId)).thenReturn(new AccessToken(repositoryId, "access-token-123"));

		int pullRequestId = 123;
		SignClaPage home = SignClaPage.go(driver, cla.getName(), repositoryId, pullRequestId);
		home.assertAt();
		home.assertClaLinksWithPullRequest(cla.getName(), repositoryId, pullRequestId);
		home.assertPullRequestLink(repositoryId, pullRequestId);
		home.assertImported();

		ArgumentCaptor<UpdatePullRequestStatusRequest> updatePullRequestCaptor = ArgumentCaptor.forClass(UpdatePullRequestStatusRequest.class);
		verify(mockGitHub).save(updatePullRequestCaptor.capture());
		UpdatePullRequestStatusRequest updatePr = updatePullRequestCaptor.getValue();
		String commitStatusUrl = "http://localhost/sign/"+cla.getName()+"?repositoryId="+repositoryId+"&pullRequestId="+pullRequestId;
		assertThat(updatePr.getCommitStatusUrl()).isEqualTo(commitStatusUrl);
		assertThat(updatePr.getCurrentUserGitHubLogin()).isEqualTo(signingUser.getGitHubLogin());
		assertThat(updatePr.getPullRequestId()).isEqualTo(pullRequestId);
		assertThat(updatePr.getRepositoryId()).isEqualTo(repositoryId);
	}

	@Test
	public void claPivotal() throws Exception {
		SignClaPage home = SignClaPage.go(driver, cla.getName());
		home.assertAt();
		home.assertClaLinks(cla.getName());
	}

	@Test
	public void claPivotalWithPullRequest() throws Exception {
		String repositoryId = "spring-projects/spring-security";
		int pullRequestId = 123;
		SignClaPage home = SignClaPage.go(driver, cla.getName(), repositoryId, pullRequestId);
		home.assertAt();
		home.assertClaLinksWithPullRequest(cla.getName(), repositoryId, pullRequestId);

		verify(mockGitHub, never()).save(any(UpdatePullRequestStatusRequest.class));
	}

	@Test
	public void claPivotalSignedWithPullRequest() throws Exception {
		when(mockIndividualSignatureRepository.findSignaturesFor(WithSigningUserFactory.create(),cla.getName())).thenReturn(individualSignature);

		String repositoryId = "spring-projects/spring-security";
		int pullRequestId = 123;
		SignClaPage home = SignClaPage.go(driver, cla.getName(), repositoryId, pullRequestId);
		home.assertAt();
		home.assertPullRequestLink(repositoryId, pullRequestId);
	}

	@Test
	public void claPivotalIndividualSigned() {
		when(mockIndividualSignatureRepository.findSignaturesFor(WithSigningUserFactory.create(),cla.getName())).thenReturn(individualSignature);

		SignClaPage home = SignClaPage.go(driver, cla.getName());
		home.assertAt();
		home.assertSigned();
	}

	@Test
	public void claPivotalCorporateSigned() throws Exception {
		List<String> organizations = Arrays.asList(corporateSignature.getGitHubOrganization());
		User user = WithSigningUserFactory.create();
		when(mockGitHub.getOrganizations(user.getGitHubLogin())).thenReturn(organizations);
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockCorporateSignatureRepository.findSignature(cla.getName(), organizations, user.getEmails())).thenReturn(corporateSignature);

		SignClaPage claPage = SignClaPage.go(driver, cla.getName());

		claPage.assertSigned();
	}

	@Test
	public void claNameNotFound() throws Exception {
		String url = SignClaPage.url("missing");
		mockMvc.perform(get(url))
			.andExpect(status().isNotFound());
	}

	@Test
	@WithSigningUser
	public void learnMoreLink() {
		SignClaPage home = SignClaPage.go(driver, cla.getName());
		AboutPage aboutPage = home.learnMore();
		aboutPage.assertAt();
	}

	@Test
	@WithSigningUser
	public void signIcla() {
		SignClaPage home = SignClaPage.go(driver, cla.getName());
		SignIclaPage sign = home.signIcla(SignIclaPage.class);
		sign.assertAt();
	}

	@Test
	@WithSigningUser
	public void signCcla() {
		SignClaPage home = SignClaPage.go(driver, cla.getName());
		SignCclaPage sign = home.signCcla(SignCclaPage.class);
		sign.assertAt();
	}
}
