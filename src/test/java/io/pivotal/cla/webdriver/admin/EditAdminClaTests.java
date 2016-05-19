package io.pivotal.cla.webdriver.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.DataUtils;
import io.pivotal.cla.security.WithAdminUserFactory;
import io.pivotal.cla.security.WithClaAuthorUser;
import io.pivotal.cla.webdriver.BaseWebDriverTests;
import io.pivotal.cla.webdriver.pages.HomePage;
import io.pivotal.cla.webdriver.pages.admin.AdminClaFormPage.ClaForm;
import io.pivotal.cla.webdriver.pages.admin.AdminEditClaPage;
import io.pivotal.cla.webdriver.pages.admin.AdminListClasPage;

@WithClaAuthorUser
public class EditAdminClaTests extends BaseWebDriverTests {


	@Test
	public void navigateToEditCla() {
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla));

		HomePage homePage = HomePage.go(driver);
		AdminListClasPage manage = homePage.manage();
		manage.assertAt();
		AdminEditClaPage edit = manage.row(0).edit();
		edit.assertAt();
	}

	@Test
	public void editClaRequiredFieldsAll() throws Exception {
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		AdminEditClaPage edit = AdminEditClaPage.to(getDriver(), cla.getId());

		edit = edit.form()
				.name("")
				.individual("")
				.corporate("")
				.submit(AdminEditClaPage.class);

		ClaForm form = edit.form();
		form.assertName().hasRequiredError();
		form.assertIndividualContent().hasRequiredError();
		form.assertCorporateContent().hasRequiredError();
	}

	@Test
	public void editClaRequiredFieldsCorporateRequired() throws Exception {
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		AdminEditClaPage create = AdminEditClaPage.to(getDriver(), cla.getId());

		create = create.form()
				.corporate("")
				.submit(AdminEditClaPage.class);

		ClaForm form = create.form();
		form.assertName().hasNoErrors();
		form.assertIndividualContent().hasNoErrors();
		form.assertCorporateContent().hasRequiredError();
	}

	@Test
	public void editClaRequiredFieldsIndividualRequired() throws Exception {
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		AdminEditClaPage create = AdminEditClaPage.to(getDriver(), cla.getId());

		create = create.form()
				.individual("")
				.submit(AdminEditClaPage.class);

		ClaForm form = create.form();
		form.assertName().hasNoErrors().hasValue(cla.getName());
		form.assertCorporateContent().hasNoErrors();
		form.assertIndividualContent().hasRequiredError();
	}

	@Test
	public void editClaRequiredFieldsNameRequired() throws Exception {
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		AdminEditClaPage create = AdminEditClaPage.to(getDriver(), cla.getId());

		create = create.form()
				.name("")
				.submit(AdminEditClaPage.class);

		ClaForm form = create.form();
		form.assertName().hasRequiredError();
		form.assertIndividualContent().hasNoErrors().hasValue(cla.getIndividualContent().getMarkdown());
		form.assertCorporateContent().hasNoErrors().hasValue(cla.getCorporateContent().getMarkdown());
	}

	@Test
	public void editClaInvalidPrimary() throws Exception {
		ContributorLicenseAgreement springCla = DataUtils.createSpringCla();
		springCla.setId(54321L);
		springCla.setSupersedingCla(cla);

		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla, springCla));
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockClaRepository.findOne(springCla.getId())).thenReturn(springCla);
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);

		AdminEditClaPage create = AdminEditClaPage.to(getDriver(), springCla.getId());

		create = create.form()
				.name(cla.getName())
				.primary()
				.submit(AdminEditClaPage.class);

		ClaForm form = create.form();
		form.assertPrimary().hasError("A primary CLA with this name already exists");
	}

	@Test
	public void editClaSuccess() {
		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla));
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);


		String individualMd = cla.getIndividualContent().getMarkdown();
		String individualHtml = cla.getIndividualContent().getHtml();
		String corporateMd = cla.getCorporateContent().getMarkdown();
		String corporateHtml = cla.getCorporateContent().getHtml();

		String accessToken = WithAdminUserFactory.create().getAccessToken();
		when(mockGitHub.markdownToHtml(accessToken, individualMd)).thenReturn(individualHtml);
		when(mockGitHub.markdownToHtml(accessToken, corporateMd)).thenReturn(corporateHtml);

		AdminEditClaPage create = AdminEditClaPage.to(getDriver(), cla.getId());

		AdminListClasPage successPage = create.form()
				.name("Eclipse")
				.individual(individualMd)
				.corporate(corporateMd)
				.submit(AdminListClasPage.class);
		successPage.assertAt();

		ArgumentCaptor<ContributorLicenseAgreement> captor = ArgumentCaptor
				.forClass(ContributorLicenseAgreement.class);
		verify(mockClaRepository).save(captor.capture());

		ContributorLicenseAgreement saved = captor.getValue();
		assertThat(saved.getName()).isEqualTo("Eclipse");
		assertThat(saved.isPrimary()).isFalse();
		assertThat(saved.getDescription()).isNull();
		assertThat(saved.getSupersedingCla()).isNull();
		assertThat(saved.getIndividualContent().getHtml()).isEqualTo(individualHtml);
		assertThat(saved.getIndividualContent().getMarkdown()).isEqualTo(individualMd);
		assertThat(saved.getCorporateContent().getHtml()).isEqualTo(corporateHtml);
		assertThat(saved.getCorporateContent().getMarkdown()).isEqualTo(corporateMd);
	}

	@Test
	public void editClaSuccessPrimary() {
		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla));
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		String individualMd = cla.getIndividualContent().getMarkdown();
		String individualHtml = cla.getIndividualContent().getHtml();
		String corporateMd = cla.getCorporateContent().getMarkdown();
		String corporateHtml = cla.getCorporateContent().getHtml();

		String accessToken = WithAdminUserFactory.create().getAccessToken();
		when(mockGitHub.markdownToHtml(accessToken, individualMd)).thenReturn(individualHtml);
		when(mockGitHub.markdownToHtml(accessToken, corporateMd)).thenReturn(corporateHtml);

		AdminEditClaPage create = AdminEditClaPage.to(getDriver(), cla.getId());

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

		ContributorLicenseAgreement saved = captor.getValue();
		assertThat(saved.getName()).isEqualTo("Eclipse");
		assertThat(saved.isPrimary()).isTrue();
		assertThat(saved.getIndividualContent().getHtml()).isEqualTo(individualHtml);
		assertThat(saved.getIndividualContent().getMarkdown()).isEqualTo(individualMd);
		assertThat(saved.getCorporateContent().getHtml()).isEqualTo(corporateHtml);
		assertThat(saved.getCorporateContent().getMarkdown()).isEqualTo(corporateMd);
	}

	@Test
	public void editClaSuccessPrimaryExistingButSameId() {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla));
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		String individualMd = cla.getIndividualContent().getMarkdown();
		String individualHtml = cla.getIndividualContent().getHtml();
		String corporateMd = cla.getCorporateContent().getMarkdown();
		String corporateHtml = cla.getCorporateContent().getHtml();

		String accessToken = WithAdminUserFactory.create().getAccessToken();
		when(mockGitHub.markdownToHtml(accessToken, individualMd)).thenReturn(individualHtml);
		when(mockGitHub.markdownToHtml(accessToken, corporateMd)).thenReturn(corporateHtml);

		AdminEditClaPage create = AdminEditClaPage.to(getDriver(), cla.getId());

		AdminListClasPage successPage = create.form()
				.corporate(corporateMd)
				.primary()
				.submit(AdminListClasPage.class);
		successPage.assertAt();

		ArgumentCaptor<ContributorLicenseAgreement> captor = ArgumentCaptor
				.forClass(ContributorLicenseAgreement.class);
		verify(mockClaRepository).save(captor.capture());

		ContributorLicenseAgreement saved = captor.getValue();
		assertThat(saved.getName()).isEqualTo(cla.getName());
		assertThat(saved.isPrimary()).isTrue();
		assertThat(saved.getIndividualContent().getHtml()).isEqualTo(individualHtml);
		assertThat(saved.getIndividualContent().getMarkdown()).isEqualTo(individualMd);
		assertThat(saved.getCorporateContent().getHtml()).isEqualTo(corporateHtml);
		assertThat(saved.getCorporateContent().getMarkdown()).isEqualTo(corporateMd);
	}

	@Test
	public void editClaSuccessDescription() {
		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla));
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		String individualMd = cla.getIndividualContent().getMarkdown();
		String individualHtml = cla.getIndividualContent().getHtml();
		String corporateMd = cla.getCorporateContent().getMarkdown();
		String corporateHtml = cla.getCorporateContent().getHtml();

		String accessToken = WithAdminUserFactory.create().getAccessToken();
		when(mockGitHub.markdownToHtml(accessToken, individualMd)).thenReturn(individualHtml);
		when(mockGitHub.markdownToHtml(accessToken, corporateMd)).thenReturn(corporateHtml);

		AdminEditClaPage create = AdminEditClaPage.to(getDriver(), cla.getId());

		String description = "ICLA FINAL 072310";
		AdminListClasPage successPage = create.form()
				.name("Eclipse")
				.individual(individualMd)
				.corporate(corporateMd)
				.description(description)
				.submit(AdminListClasPage.class);
		successPage.assertAt();

		ArgumentCaptor<ContributorLicenseAgreement> captor = ArgumentCaptor
				.forClass(ContributorLicenseAgreement.class);
		verify(mockClaRepository).save(captor.capture());

		ContributorLicenseAgreement saved = captor.getValue();
		assertThat(saved.getId()).isEqualTo(cla.getId());
		assertThat(saved.getName()).isEqualTo("Eclipse");
		assertThat(saved.isPrimary()).isFalse();
		assertThat(saved.getDescription()).isEqualTo(description);
		assertThat(saved.getIndividualContent().getHtml()).isEqualTo(individualHtml);
		assertThat(saved.getIndividualContent().getMarkdown()).isEqualTo(individualMd);
		assertThat(saved.getCorporateContent().getHtml()).isEqualTo(corporateHtml);
		assertThat(saved.getCorporateContent().getMarkdown()).isEqualTo(corporateMd);
	}

	@Test
	public void editClaSuccessSuperseding() {
		ContributorLicenseAgreement springCla = DataUtils.createSpringCla();
		springCla.setId(54321L);
		springCla.setSupersedingCla(cla);

		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla, springCla));
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);
		when(mockClaRepository.findOne(springCla.getId())).thenReturn(springCla);

		String individualMd = cla.getIndividualContent().getMarkdown();
		String individualHtml = cla.getIndividualContent().getHtml();
		String corporateMd = cla.getCorporateContent().getMarkdown();
		String corporateHtml = cla.getCorporateContent().getHtml();

		String accessToken = WithAdminUserFactory.create().getAccessToken();
		when(mockGitHub.markdownToHtml(accessToken, individualMd)).thenReturn(individualHtml);
		when(mockGitHub.markdownToHtml(accessToken, corporateMd)).thenReturn(corporateHtml);

		AdminEditClaPage create = AdminEditClaPage.to(getDriver(), springCla.getId());

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

		ContributorLicenseAgreement saved = captor.getValue();
		assertThat(saved.getId()).isEqualTo(springCla.getId());
		assertThat(saved.getName()).isEqualTo("Eclipse");
		assertThat(saved.isPrimary()).isFalse();
		assertThat(saved.getSupersedingCla()).isSameAs(this.cla);
		assertThat(saved.getIndividualContent().getHtml()).isEqualTo(individualHtml);
		assertThat(saved.getIndividualContent().getMarkdown()).isEqualTo(individualMd);
		assertThat(saved.getCorporateContent().getHtml()).isEqualTo(corporateHtml);
		assertThat(saved.getCorporateContent().getMarkdown()).isEqualTo(corporateMd);
	}

}
