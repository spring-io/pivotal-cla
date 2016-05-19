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
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import io.pivotal.cla.data.AccessToken;
import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.CorporateSignature;
import io.pivotal.cla.data.DataUtils;
import io.pivotal.cla.data.User;
import io.pivotal.cla.security.WithSigningUser;
import io.pivotal.cla.security.WithSigningUserFactory;
import io.pivotal.cla.service.github.UpdatePullRequestStatusRequest;
import io.pivotal.cla.webdriver.pages.SignCclaPage;
import io.pivotal.cla.webdriver.pages.SignCclaPage.Form;

@WithSigningUser
public class CclaControllerTests extends BaseWebDriverTests {

	@Test
	public void view() {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);

		SignCclaPage signPage = SignCclaPage.go(getDriver(), cla.getName());

		signPage.assertClaLink(cla.getName());
		assertThat(signPage.getCorporate()).isEqualTo(cla.getCorporateContent().getHtml());
	}

	@Test
	public void viewSupersedingCla() {
		ContributorLicenseAgreement springCla = DataUtils.createSpringCla();
		springCla.setSupersedingCla(cla);
		when(mockClaRepository.findByNameAndPrimaryTrue(springCla.getName())).thenReturn(springCla);

		SignCclaPage signPage = SignCclaPage.go(getDriver(), springCla.getName());

		signPage.assertClaLink(springCla.getName());
		assertThat(signPage.getCorporate()).isEqualTo(cla.getCorporateContent().getHtml());
		assertThat(signPage.isSigned()).isFalse();
	}

	@Test
	public void viewSigned() throws Exception {
		List<String> organizations = Arrays.asList(corporateSignature.getGitHubOrganization());
		User user = WithSigningUserFactory.create();
		when(mockGithub.getOrganizations(user.getGithubLogin())).thenReturn(organizations);
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockCorporateSignatureRepository.findSignature(cla.getName(), organizations, user.getEmails())).thenReturn(corporateSignature);

		SignCclaPage signPage = SignCclaPage.go(getDriver(), cla.getName());

		assertThat(signPage.isSigned()).isTrue();
	}

	@Test
	public void signNameRequired() throws Exception {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockGithub.getOrganizations(anyString())).thenReturn(Arrays.asList("spring","pivotal"));

		SignCclaPage signPage = SignCclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.telephone("123.456.7890")
			.companyName("Pivotal")
			.gitHubOrganization("pivotal")
			.title("Director")
			.confirm()
			.sign(SignCclaPage.class);

		signPage.assertAt();
		form = signPage.form();
		form.assertName().hasRequiredError();
		form.assertEmail().hasNoErrors();
		form.assertCompanyName().hasNoErrors();
		form.assertGitHubOrganization().hasNoErrors();
		form.assertConfirm().hasNoErrors();
	}

	@Test
	public void signEmailRequired() throws Exception {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockGithub.getOrganizations(anyString())).thenReturn(Arrays.asList("spring","pivotal"));

		SignCclaPage signPage = SignCclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.name("Rob Winch")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.telephone("123.456.7890")
			.companyName("Pivotal")
			.gitHubOrganization("pivotal")
			.title("Director")
			.confirm()
			.sign(SignCclaPage.class);

		signPage.assertAt();
		form = signPage.form();
		form.assertName().hasNoErrors();
		form.assertEmail().hasRequiredError();
		form.assertCompanyName().hasNoErrors();
		form.assertGitHubOrganization().hasNoErrors();
		form.assertConfirm().hasNoErrors();
	}


	@Test
	public void signMailingAddressRequired() throws Exception {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockGithub.getOrganizations(anyString())).thenReturn(Arrays.asList("spring","pivotal"));

		SignCclaPage signPage = SignCclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.name("Rob Winch")
			.email("rob@gmail.com")
			.country("USA")
			.telephone("123.456.7890")
			.companyName("Pivotal")
			.gitHubOrganization("pivotal")
			.title("Director")
			.confirm()
			.sign(SignCclaPage.class);

		signPage.assertAt();
		form = signPage.form();
		form.assertName().hasNoErrors();
		form.assertEmail().hasNoErrors();
		form.assertMailingAddress().hasRequiredError();
		form.assertCountry().hasNoErrors();
		form.assertTelephone().hasNoErrors();
		form.assertConfirm().hasNoErrors();
	}

	@Test
	public void signCountryRequired() throws Exception {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockGithub.getOrganizations(anyString())).thenReturn(Arrays.asList("spring","pivotal"));

		SignCclaPage signPage = SignCclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.name("Rob Winch")
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.telephone("123.456.7890")
			.companyName("Pivotal")
			.gitHubOrganization("pivotal")
			.title("Director")
			.confirm()
			.sign(SignCclaPage.class);

		signPage.assertAt();
		form = signPage.form();
		form.assertName().hasNoErrors();
		form.assertEmail().hasNoErrors();
		form.assertMailingAddress().hasNoErrors();
		form.assertCountry().hasRequiredError();
		form.assertTelephone().hasNoErrors();
		form.assertConfirm().hasNoErrors();
	}

	@Test
	public void signTelephoneRequired() throws Exception {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockGithub.getOrganizations(anyString())).thenReturn(Arrays.asList("spring","pivotal"));

		SignCclaPage signPage = SignCclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.name("Rob Winch")
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.companyName("Pivotal")
			.gitHubOrganization("pivotal")
			.title("Director")
			.confirm()
			.sign(SignCclaPage.class);

		signPage.assertAt();
		form = signPage.form();
		form.assertName().hasNoErrors();
		form.assertEmail().hasNoErrors();
		form.assertMailingAddress().hasNoErrors();
		form.assertCountry().hasNoErrors();
		form.assertTelephone().hasRequiredError();
		form.assertConfirm().hasNoErrors();
	}

	@Test
	public void signCompanyNameRequired() throws Exception {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockGithub.getOrganizations(anyString())).thenReturn(Arrays.asList("spring","pivotal"));

		SignCclaPage signPage = SignCclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.name("Rob Winch")
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.telephone("123.456.7890")
			.gitHubOrganization("pivotal")
			.title("Director")
			.confirm()
			.sign(SignCclaPage.class);

		signPage.assertAt();
		form = signPage.form();
		form.assertName().hasNoErrors();
		form.assertEmail().hasNoErrors();
		form.assertCompanyName().hasRequiredError();
		form.assertGitHubOrganization().hasNoErrors();
		form.assertConfirm().hasNoErrors();
	}

	@Test
	public void signOrganizationRequired() throws Exception {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockGithub.getOrganizations(anyString())).thenReturn(Arrays.asList("spring","pivotal"));

		SignCclaPage signPage = SignCclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.name("Rob Winch")
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.telephone("123.456.7890")
			.companyName("Pivotal")
			.title("Director")
			.confirm()
			.sign(SignCclaPage.class);

		signPage.assertAt();
		form = signPage.form();
		form.assertName().hasNoErrors();
		form.assertEmail().hasNoErrors();
		form.assertCompanyName().hasNoErrors();
		form.assertGitHubOrganization().hasRequiredError();
		form.assertConfirm().hasNoErrors();
	}

	@Test
	public void signConfirmRequired() throws Exception {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockGithub.getOrganizations(anyString())).thenReturn(Arrays.asList("spring","pivotal"));

		SignCclaPage signPage = SignCclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.name("Rob Winch")
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.telephone("123.456.7890")
			.companyName("Pivotal")
			.gitHubOrganization("pivotal")
			.title("Director")
			.sign(SignCclaPage.class);

		signPage.assertAt();
		form = signPage.form();
		form.assertName().hasNoErrors();
		form.assertEmail().hasNoErrors();
		form.assertMailingAddress().hasNoErrors();
		form.assertCountry().hasNoErrors();
		form.assertTelephone().hasNoErrors();
		form.assertConfirm().hasRequiredError();
	}

	@Test
	public void fieldsRepopulatedOnError() throws Exception {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockGithub.getOrganizations(anyString())).thenReturn(Arrays.asList("spring","pivotal"));

		SignCclaPage signPage = SignCclaPage.go(getDriver(), cla.getName());

		signPage = signPage.form()
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.telephone("123.456.7890")
			.companyName("Pivotal")
			.gitHubOrganization("pivotal")
			.title("Director")
			.confirm()
			.sign(SignCclaPage.class);

		signPage.assertAt();

		Form form = signPage.form();
		form.assertEmail().hasValue("rob@gmail.com");
		form.assertMailingAddress().hasValue("123 Seasame St");
		form.assertCountry().hasValue("USA");
		form.assertTelephone().hasValue("123.456.7890");
		form.assertCompanyName().hasValue("Pivotal");
		form.assertGitHubOrganization().hasValue("pivotal");
		form.assertTitle().hasValue("Director");
		form.assertConfirm().assertSelected();

		signPage = SignCclaPage.go(getDriver(), cla.getName());

		signPage = signPage.form()
			.name("Rob Winch")
			.sign(SignCclaPage.class);

		signPage.form()
			.assertName().hasValue("Rob Winch");
	}

	@Test
	public void signSupersedingCla() {
		ContributorLicenseAgreement springCla = DataUtils.createSpringCla();
		springCla.setSupersedingCla(cla);
		when(mockClaRepository.findByNameAndPrimaryTrue(springCla.getName())).thenReturn(springCla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignCclaPage signPage = SignCclaPage.go(getDriver(), springCla.getName());

		signPage = signPage.form()
			.sign(SignCclaPage.class);

		signPage.assertClaLink(springCla.getName());
		assertThat(signPage.getCorporate()).isEqualTo(cla.getCorporateContent().getHtml());
	}

	@Test
	public void sign() throws Exception {
		String organization = "pivotal";
		String name = "Rob Winch";
		String email = "rob@gmail.com";
		String address = "123 Seasame St";
		String country = "USA";
		String telephone = "123.456.7890";
		String companyName = "Pivotal";
		String title = "Director";

		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockGithub.getOrganizations(anyString())).thenReturn(Arrays.asList("spring",organization));

		SignCclaPage signPage = SignCclaPage.go(getDriver(), cla.getName());

		signPage = signPage.form()
				.name(name)
				.email(email)
				.mailingAddress(address)
				.country(country)
				.telephone(telephone)
				.companyName(companyName)
				.gitHubOrganization(organization)
				.title(title)
				.confirm()
				.sign(SignCclaPage.class);

		signPage.assertAt();

		ArgumentCaptor<CorporateSignature> signatureCaptor = ArgumentCaptor.forClass(CorporateSignature.class);
		verify(mockCorporateSignatureRepository).save(signatureCaptor.capture());

		CorporateSignature signature = signatureCaptor.getValue();

		assertThat(signature.getCla()).isEqualTo(cla);
		assertThat(signature.getCountry()).isEqualTo(country);
		assertThat(signature.getName()).isEqualTo(name);
		assertThat(signature.getEmail()).isEqualTo(email);
		assertThat(signature.getMailingAddress()).isEqualTo(address);
		assertThat(signature.getTelephone()).isEqualTo(telephone);
		assertThat(signature.getCompanyName()).isEqualTo(companyName);
		assertThat(signature.getGitHubOrganization()).isEqualTo(organization);
		assertThat(signature.getTitle()).isEqualTo(title);
		assertThat(signature.getDateOfSignature()).isCloseTo(new Date(), TimeUnit.SECONDS.toMillis(5));

		verify(mockGithub, never()).save(any(UpdatePullRequestStatusRequest.class));
	}

	@Test
	public void signWithRepositoryIdWithPullRequestId() throws Exception {
		String repositoryId = "rwinch/176_test";

		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockGithub.getOrganizations(anyString())).thenReturn(Arrays.asList("spring","pivotal"));
		when(mockCorporateSignatureRepository.findSignature(anyString(), anyCollectionOf(String.class), anyCollectionOf(String.class))).thenReturn(null,corporateSignature);
		when(mockTokenRepo.findOne(repositoryId)).thenReturn(new AccessToken(repositoryId, "access-token-123"));

		int pullRequestId = 2;
		SignCclaPage signPage = SignCclaPage.go(getDriver(), cla.getName(), repositoryId, pullRequestId);

		signPage = signPage.form()
				.name("Rob Winch")
				.email("rob@gmail.com")
				.mailingAddress("123 Seasame St")
				.country("USA")
				.telephone("123.456.7890")
				.companyName("Pivotal")
				.gitHubOrganization("pivotal")
				.title("Director")
				.confirm()
				.sign(SignCclaPage.class);

		signPage.assertAt();
		signPage.assertPullRequestLink(repositoryId, pullRequestId);

		ArgumentCaptor<UpdatePullRequestStatusRequest> updatePullRequestCaptor = ArgumentCaptor.forClass(UpdatePullRequestStatusRequest.class);
		verify(mockGithub).save(updatePullRequestCaptor.capture());
		UpdatePullRequestStatusRequest updatePr = updatePullRequestCaptor.getValue();
		String commitStatusUrl = "http://localhost/sign/"+cla.getName()+"?repositoryId="+repositoryId+"&pullRequestId="+pullRequestId;
		assertThat(updatePr.getCommitStatusUrl()).isEqualTo(commitStatusUrl);
		assertThat(updatePr.getCurrentUserGithubLogin()).isEqualTo(WithSigningUserFactory.create().getGithubLogin());
		assertThat(updatePr.getPullRequestId()).isEqualTo(pullRequestId);
		assertThat(updatePr.getRepositoryId()).isEqualTo(repositoryId);
	}

	@Test
	public void signNoRepositoryIdAndNoPullRequestId() throws Exception {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockGithub.getOrganizations(anyString())).thenReturn(Arrays.asList("spring","pivotal"));

		SignCclaPage signPage = SignCclaPage.go(getDriver(), cla.getName());

		signPage = signPage.form()
			.name("Rob Winch")
			.email("rob@gmail.com")
			.sign(SignCclaPage.class);

		signPage.assertAt();
	}

	@Test
	public void signNoRepositoryIdWithPullRequestId() {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignCclaPage signPage = SignCclaPage.go(getDriver(), cla.getName(), "rwinch/176_test", 2);

		signPage = signPage.form()
			.name("Rob Winch")
			.email("rob@gmail.com")
			.sign(SignCclaPage.class);

		signPage.assertAt();
	}

	@Test
	public void claNameNotFound() throws Exception {
		String url = SignCclaPage.url("missing");
		mockMvc
			.perform(get(url))
				.andExpect(status().isNotFound());
	}

}
