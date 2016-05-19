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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import io.pivotal.cla.data.AccessToken;
import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.DataUtils;
import io.pivotal.cla.data.IndividualSignature;
import io.pivotal.cla.security.WithSigningUser;
import io.pivotal.cla.security.WithSigningUserFactory;
import io.pivotal.cla.service.github.UpdatePullRequestStatusRequest;
import io.pivotal.cla.webdriver.pages.SignIclaPage;
import io.pivotal.cla.webdriver.pages.SignIclaPage.Form;

@WithSigningUser
public class IclaControllerTests extends BaseWebDriverTests {

	@Test
	public void view() {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		signPage.assertClaLink(cla.getName());
		assertThat(signPage.getIndividualCla()).isEqualTo(cla.getIndividualContent().getHtml());
		assertThat(signPage.isSigned()).isFalse();
	}

	@Test
	public void viewSupersedingCla() {
		ContributorLicenseAgreement springCla = DataUtils.createSpringCla();
		springCla.setSupersedingCla(cla);
		when(mockClaRepository.findByNameAndPrimaryTrue(springCla.getName())).thenReturn(springCla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), springCla.getName());

		signPage.assertClaLink(springCla.getName());
		assertThat(signPage.getIndividualCla()).isEqualTo(cla.getIndividualContent().getHtml());
		assertThat(signPage.isSigned()).isFalse();
	}

	@Test
	public void viewAlreadySigned() {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockIndividualSignatureRepository.findSignaturesFor(WithSigningUserFactory.create(), cla.getName())).thenReturn(individualSignature);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		assertThat(signPage.isSigned()).isTrue();
	}

	@Test
	public void signNameRequired() {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.telephone("123.456.7890")
			.confirm()
			.sign(SignIclaPage.class);

		signPage.assertAt();
		form = signPage.form();
		form.assertName().hasRequiredError();
		form.assertEmail().hasNoErrors();
		form.assertMailingAddress().hasNoErrors();
		form.assertCountry().hasNoErrors();
		form.assertTelephone().hasNoErrors();
		form.assertConfirm().hasNoErrors();
	}

	@Test
	public void signEmailRequired() {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.name("Rob Winch")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.telephone("123.456.7890")
			.confirm()
			.sign(SignIclaPage.class);

		signPage.assertAt();
		form = signPage.form();
		form.assertName().hasNoErrors();
		form.assertEmail().hasRequiredError();
		form.assertMailingAddress().hasNoErrors();
		form.assertCountry().hasNoErrors();
		form.assertTelephone().hasNoErrors();
		form.assertConfirm().hasNoErrors();
	}

	@Test
	public void signMailingAddressRequired() {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.name("Rob Winch")
			.email("rob@gmail.com")
			.country("USA")
			.telephone("123.456.7890")
			.confirm()
			.sign(SignIclaPage.class);

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
	public void signCountryRequired() {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.name("Rob Winch")
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.telephone("123.456.7890")
			.confirm()
			.sign(SignIclaPage.class);

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
	public void signTelephoneRequired() {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.name("Rob Winch")
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.confirm()
			.sign(SignIclaPage.class);

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
	public void signConfirmRequired() {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.name("Rob Winch")
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.telephone("123.456.7890")
			.sign(SignIclaPage.class);

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
	public void signSupersedingCla() {
		ContributorLicenseAgreement springCla = DataUtils.createSpringCla();
		springCla.setSupersedingCla(cla);
		when(mockClaRepository.findByNameAndPrimaryTrue(springCla.getName())).thenReturn(springCla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), springCla.getName());

		signPage = signPage.form()
			.sign(SignIclaPage.class);

		signPage.assertClaLink(springCla.getName());
		assertThat(signPage.getIndividualCla()).isEqualTo(cla.getIndividualContent().getHtml());
	}

	@Test
	public void fieldsRepopulatedOnError() {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		signPage = signPage.form()
			.name("Rob Winch")
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.telephone("123.456.7890")
			.sign(SignIclaPage.class);

		signPage.assertAt();

		Form form = signPage.form();
		form.assertName().hasValue("Rob Winch");
		form.assertEmail().hasValue("rob@gmail.com");
		form.assertMailingAddress().hasValue("123 Seasame St");
		form.assertCountry().hasValue("USA");
		form.assertTelephone().hasValue("123.456.7890");

		signPage = SignIclaPage.go(getDriver(), cla.getName());

		signPage = signPage.form()
			.confirm()
			.sign(SignIclaPage.class);

		signPage.form()
			.assertConfirm().assertSelected();
	}

	@Test
	public void signNoRepositoryIdAndNoPullRequestId() {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		signPage = signPage.form()
			.name("Rob Winch")
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.telephone("123.456.7890")
			.confirm()
			.sign(SignIclaPage.class);

		signPage.assertAt();
		verifyZeroInteractions(mockGitHub);
	}

	@Test
	public void sign() {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		String country = "USA";
		String name = "Rob Winch";
		String email = "rob@gmail.com";
		String address = "123 Seasame St";
		String telephone = "123.456.7890";
		signPage = signPage.form()
			.name(name)
			.email(email)
			.mailingAddress(address)
			.country(country)
			.telephone(telephone)
			.confirm()
			.sign(SignIclaPage.class);

		signPage.assertAt();

		ArgumentCaptor<IndividualSignature> signatureCaptor = ArgumentCaptor.forClass(IndividualSignature.class);
		verify(mockIndividualSignatureRepository).save(signatureCaptor.capture());

		IndividualSignature signature = signatureCaptor.getValue();

		assertThat(signature.getCla()).isEqualTo(cla);
		assertThat(signature.getCountry()).isEqualTo(country);
		assertThat(signature.getName()).isEqualTo(name);
		assertThat(signature.getEmail()).isEqualTo(email);
		assertThat(signature.getMailingAddress()).isEqualTo(address);
		assertThat(signature.getTelephone()).isEqualTo(telephone);
		assertThat(signature.getDateOfSignature()).isCloseTo(new Date(), TimeUnit.SECONDS.toMillis(5));

		verifyZeroInteractions(mockGitHub);
	}

	@Test
	public void signWithRepositoryIdWithPullRequestId() throws Exception {
		String repositoryId = "rwinch/176_test";
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockIndividualSignatureRepository.findSignaturesFor(WithSigningUserFactory.create(), cla.getName())).thenReturn(null, individualSignature);
		when(mockTokenRepo.findOne(repositoryId)).thenReturn(new AccessToken(repositoryId, "access-token-123"));

		int pullRequestId = 2;
		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName(), repositoryId, pullRequestId);

		signPage = signPage.form()
			.name("Rob Winch")
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.telephone("123.456.7890")
			.confirm()
			.sign(SignIclaPage.class);

		signPage.assertAt();
		signPage.assertPullRequestLink(repositoryId, pullRequestId);

		ArgumentCaptor<UpdatePullRequestStatusRequest> updatePullRequestCaptor = ArgumentCaptor.forClass(UpdatePullRequestStatusRequest.class);
		verify(mockGitHub).save(updatePullRequestCaptor.capture());
		UpdatePullRequestStatusRequest updatePr = updatePullRequestCaptor.getValue();
		String commitStatusUrl = "http://localhost/sign/"+cla.getName()+"?repositoryId="+repositoryId+"&pullRequestId="+pullRequestId;
		assertThat(updatePr.getCommitStatusUrl()).isEqualTo(commitStatusUrl);
		assertThat(updatePr.getCurrentUserGitHubLogin()).isEqualTo(WithSigningUserFactory.create().getGitHubLogin());
		assertThat(updatePr.getPullRequestId()).isEqualTo(pullRequestId);
		assertThat(updatePr.getRepositoryId()).isEqualTo(repositoryId);
	}

	@Test
	public void claNameNotFound() throws Exception {
		String url = SignIclaPage.url("missing");
		mockMvc
			.perform(get(url))
				.andExpect(status().isNotFound());
	}
}
