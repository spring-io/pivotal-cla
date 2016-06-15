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
package io.pivotal.cla.webdriver.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.security.WithAdminUserFactory;
import io.pivotal.cla.security.WithClaAuthorUser;
import io.pivotal.cla.webdriver.BaseWebDriverTests;
import io.pivotal.cla.webdriver.pages.HomePage;
import io.pivotal.cla.webdriver.pages.admin.AdminClaFormPage.ClaForm;
import io.pivotal.cla.webdriver.pages.admin.AdminCreateClaPage;
import io.pivotal.cla.webdriver.pages.admin.AdminListClasPage;

@WithClaAuthorUser
public class AdminCreateClaTests extends BaseWebDriverTests {

	@Test
	public void navigateToCreateCla() {
		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla));

		HomePage homePage = HomePage.go(driver);
		AdminListClasPage manage = homePage.manage();
		manage.assertAt();
		AdminCreateClaPage create = manage.createCla();
		create.assertAt();
	}

	@Test
	public void createClaRequiredFieldsAll() throws Exception {

		AdminCreateClaPage create = AdminCreateClaPage.to(getDriver());

		create = create.form()
				.submit(AdminCreateClaPage.class);

		ClaForm form = create.form();
		form.assertName().hasRequiredError();
		form.assertIndividualContent().hasRequiredError();
		form.assertCorporateContent().hasRequiredError();
	}

	@Test
	public void createClaRequiredFieldsCorporateRequired() throws Exception {
		AdminCreateClaPage create = AdminCreateClaPage.to(getDriver());

		create = create.form()
				.name("Name")
				.individual("Individual")
				.submit(AdminCreateClaPage.class);

		ClaForm form = create.form();
		form.assertName().hasNoErrors();
		form.assertIndividualContent().hasNoErrors();
		form.assertCorporateContent().hasRequiredError();
	}

	@Test
	public void createClaRequiredFieldsIndividualRequired() throws Exception {
		AdminCreateClaPage create = AdminCreateClaPage.to(getDriver());


		create = create.form()
				.name("Name")
				.corporate("Corporate")
				.submit(AdminCreateClaPage.class);

		ClaForm form = create.form();
		form.assertName().hasNoErrors().hasValue("Name");
		form.assertCorporateContent().hasNoErrors();
		form.assertIndividualContent().hasRequiredError();
	}

	@Test
	public void createClaRequiredFieldsNameRequired() throws Exception {

		AdminCreateClaPage create = AdminCreateClaPage.to(getDriver());

		create = create.form()
				.individual("Individual")
				.corporate("Corporate")
				.submit(AdminCreateClaPage.class);

		ClaForm form = create.form();
		form.assertName().hasRequiredError();
		form.assertIndividualContent().hasNoErrors().hasValue("Individual");
		form.assertCorporateContent().hasNoErrors().hasValue("Corporate");
	}

	@Test
	public void createClaInvalidPrimary() throws Exception {
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);

		AdminCreateClaPage create = AdminCreateClaPage.to(getDriver());

		create = create.form()
				.name(cla.getName())
				.individual("Individual")
				.corporate("Corporate")
				.primary()
				.submit(AdminCreateClaPage.class);

		ClaForm form = create.form();
		form.assertPrimary().hasError("A primary CLA with this name already exists");
	}

	@Test
	public void createClaValidPrimary() throws Exception {
		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla));

		AdminCreateClaPage create = AdminCreateClaPage.to(getDriver());

		AdminListClasPage success = create.form()
				.name(cla.getName())
				.individual("Individual")
				.corporate("Corporate")
				.primary()
				.submit(AdminListClasPage.class);

		success.assertAt();
	}

	@Test
	public void createClaSuccess() {
		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla));
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);


		String individualMd = cla.getIndividualContent().getMarkdown();
		String individualHtml = cla.getIndividualContent().getHtml();
		String corporateMd = cla.getCorporateContent().getMarkdown();
		String corporateHtml = cla.getCorporateContent().getHtml();

		String accessToken = WithAdminUserFactory.create().getAccessToken();
		when(mockGitHub.markdownToHtml(accessToken, individualMd)).thenReturn(individualHtml);
		when(mockGitHub.markdownToHtml(accessToken, corporateMd)).thenReturn(corporateHtml);

		AdminCreateClaPage create = AdminCreateClaPage.to(getDriver());

		AdminListClasPage successPage = create.form()
				.name("Eclipse")
				.individual(individualMd)
				.corporate(corporateMd)
				.submit(AdminListClasPage.class);
		successPage.assertAt();

		ArgumentCaptor<ContributorLicenseAgreement> captor = ArgumentCaptor
				.forClass(ContributorLicenseAgreement.class);
		verify(mockClaRepository).save(captor.capture());

		ContributorLicenseAgreement cla = captor.getValue();
		assertThat(cla.getName()).isEqualTo("Eclipse");
		assertThat(cla.isPrimary()).isFalse();
		assertThat(cla.getDescription()).isNull();
		assertThat(cla.getSupersedingCla()).isNull();
		assertThat(cla.getIndividualContent().getHtml()).isEqualTo(individualHtml);
		assertThat(cla.getIndividualContent().getMarkdown()).isEqualTo(individualMd);
		assertThat(cla.getCorporateContent().getHtml()).isEqualTo(corporateHtml);
		assertThat(cla.getCorporateContent().getMarkdown()).isEqualTo(corporateMd);
	}

	@Test
	public void createClaSuccessPrimary() {
		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla));
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);


		String individualMd = cla.getIndividualContent().getMarkdown();
		String individualHtml = cla.getIndividualContent().getHtml();
		String corporateMd = cla.getCorporateContent().getMarkdown();
		String corporateHtml = cla.getCorporateContent().getHtml();

		String accessToken = WithAdminUserFactory.create().getAccessToken();
		when(mockGitHub.markdownToHtml(accessToken, individualMd)).thenReturn(individualHtml);
		when(mockGitHub.markdownToHtml(accessToken, corporateMd)).thenReturn(corporateHtml);

		AdminCreateClaPage create = AdminCreateClaPage.to(getDriver());

		AdminListClasPage successPage = create.form()
				.name("Eclipse")
				.individual(individualMd)
				.corporate(corporateMd)
				.primary()
				.submit(AdminListClasPage.class);
		successPage.assertAt();

		ArgumentCaptor<ContributorLicenseAgreement> captor = ArgumentCaptor
				.forClass(ContributorLicenseAgreement.class);
		verify(mockClaRepository).save(captor.capture());

		ContributorLicenseAgreement cla = captor.getValue();
		assertThat(cla.getName()).isEqualTo("Eclipse");
		assertThat(cla.isPrimary()).isTrue();
		assertThat(cla.getIndividualContent().getHtml()).isEqualTo(individualHtml);
		assertThat(cla.getIndividualContent().getMarkdown()).isEqualTo(individualMd);
		assertThat(cla.getCorporateContent().getHtml()).isEqualTo(corporateHtml);
		assertThat(cla.getCorporateContent().getMarkdown()).isEqualTo(corporateMd);
	}

	@Test
	public void createClaSuccessDescription() {
		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla));
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		String individualMd = cla.getIndividualContent().getMarkdown();
		String individualHtml = cla.getIndividualContent().getHtml();
		String corporateMd = cla.getCorporateContent().getMarkdown();
		String corporateHtml = cla.getCorporateContent().getHtml();

		String accessToken = WithAdminUserFactory.create().getAccessToken();
		when(mockGitHub.markdownToHtml(accessToken, individualMd)).thenReturn(individualHtml);
		when(mockGitHub.markdownToHtml(accessToken, corporateMd)).thenReturn(corporateHtml);

		AdminCreateClaPage create = AdminCreateClaPage.to(getDriver());

		String description = "ICLA FINAL 072310";
		AdminListClasPage successPage = create.form()
				.name("Eclipse")
				.individual(individualMd)
				.corporate(corporateMd)
				.description(description)
				.submit(AdminListClasPage.class);
		successPage.assertAt();
		successPage.assertAt();

		ArgumentCaptor<ContributorLicenseAgreement> captor = ArgumentCaptor
				.forClass(ContributorLicenseAgreement.class);
		verify(mockClaRepository).save(captor.capture());

		ContributorLicenseAgreement cla = captor.getValue();
		assertThat(cla.getName()).isEqualTo("Eclipse");
		assertThat(cla.isPrimary()).isFalse();
		assertThat(cla.getDescription()).isEqualTo(description);
		assertThat(cla.getIndividualContent().getHtml()).isEqualTo(individualHtml);
		assertThat(cla.getIndividualContent().getMarkdown()).isEqualTo(individualMd);
		assertThat(cla.getCorporateContent().getHtml()).isEqualTo(corporateHtml);
		assertThat(cla.getCorporateContent().getMarkdown()).isEqualTo(corporateMd);
	}

	@Test
	public void createClaSuccessSuperseding() {
		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla));
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		String individualMd = cla.getIndividualContent().getMarkdown();
		String individualHtml = cla.getIndividualContent().getHtml();
		String corporateMd = cla.getCorporateContent().getMarkdown();
		String corporateHtml = cla.getCorporateContent().getHtml();

		String accessToken = WithAdminUserFactory.create().getAccessToken();
		when(mockGitHub.markdownToHtml(accessToken, individualMd)).thenReturn(individualHtml);
		when(mockGitHub.markdownToHtml(accessToken, corporateMd)).thenReturn(corporateHtml);

		AdminCreateClaPage create = AdminCreateClaPage.to(getDriver());

		AdminListClasPage successPage = create.form()
				.name("Eclipse")
				.individual(individualMd)
				.corporate(corporateMd)
				.supersedingCla(cla.getId())
				.submit(AdminListClasPage.class);
		successPage.assertAt();

		ArgumentCaptor<ContributorLicenseAgreement> captor = ArgumentCaptor
				.forClass(ContributorLicenseAgreement.class);
		verify(mockClaRepository).save(captor.capture());

		ContributorLicenseAgreement cla = captor.getValue();
		assertThat(cla.getName()).isEqualTo("Eclipse");
		assertThat(cla.isPrimary()).isFalse();
		assertThat(cla.getSupersedingCla()).isSameAs(this.cla);
		assertThat(cla.getIndividualContent().getHtml()).isEqualTo(individualHtml);
		assertThat(cla.getIndividualContent().getMarkdown()).isEqualTo(individualMd);
		assertThat(cla.getCorporateContent().getHtml()).isEqualTo(corporateHtml);
		assertThat(cla.getCorporateContent().getMarkdown()).isEqualTo(corporateMd);
	}
}
