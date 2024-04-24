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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.Executors;

import io.pivotal.cla.security.GitHubSignature;
import io.pivotal.cla.security.ImportSecurity;
import io.pivotal.cla.service.ClaService;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.web.context.WebApplicationContext;

import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.CorporateSignature;
import io.pivotal.cla.data.IndividualSignature;
import io.pivotal.cla.data.MarkdownContent;
import io.pivotal.cla.data.repository.AccessTokenRepository;
import io.pivotal.cla.data.repository.ContributorLicenseAgreementRepository;
import io.pivotal.cla.data.repository.CorporateSignatureRepository;
import io.pivotal.cla.data.repository.IndividualSignatureRepository;
import io.pivotal.cla.data.repository.UserRepository;
import io.pivotal.cla.service.github.GitHubApi;

@WebMvcTest
@ImportSecurity
@Import(ClaService.class)
public abstract class BaseWebDriverTests {
	@Autowired
	protected WebApplicationContext wac;

	@MockBean
	protected GitHubApi mockGitHub;
	@MockBean
	protected ContributorLicenseAgreementRepository mockClaRepository;
	@MockBean
	protected IndividualSignatureRepository mockIndividualSignatureRepository;
	@MockBean
	protected CorporateSignatureRepository mockCorporateSignatureRepository;
	@MockBean
	protected AccessTokenRepository mockTokenRepo;
	@MockBean
	protected UserRepository mockUserRepo;

	protected WebDriver driver;

	@Autowired
	protected MockMvc mockMvc;

	protected ContributorLicenseAgreement cla;
	protected IndividualSignature individualSignature;
	protected CorporateSignature corporateSignature;

	@BeforeEach
	public void setup() {
		HtmlUnitDriver driver = MockMvcHtmlUnitDriverBuilder
				.mockMvcSetup(mockMvc)
				.useMockMvcForHosts("github.com")
				.build();
		driver.setExecutor(new DelegatingSecurityContextExecutor(Executors.newSingleThreadExecutor()));
		this.driver = driver;

		MarkdownContent corporate = new MarkdownContent();
		corporate.setMarkdown("# Corporate");
		corporate.setHtml("[h1]Corporate[/h1]");
		MarkdownContent individual = new MarkdownContent();
		individual.setMarkdown("# Individual");
		individual.setHtml("[h1]Individual[/h1]");

		cla = new ContributorLicenseAgreement();
		cla.setName("pivotal");
		cla.setCorporateContent(corporate);
		cla.setIndividualContent(individual);
		cla.setId(1L);

		individualSignature = new IndividualSignature();
		individualSignature.setCla(cla);
		individualSignature.setGitHubLogin("rwinch");
		individualSignature.setEmail("rob@gmail.com");
		individualSignature.setName("Rob Winch");
		individualSignature.setMailingAddress("123 Seasame Street");
		individualSignature.setTelephone("123.456.7890");

		corporateSignature = new CorporateSignature();
		corporateSignature.setCla(cla);
		corporateSignature.setGitHubLogin("rwinch");
		corporateSignature.setEmail("rob@gmail.com");
		corporateSignature.setName("Rob Winch");
		corporateSignature.setMailingAddress("123 Seasame Street");
		corporateSignature.setTelephone("123.456.7890");
		corporateSignature.setGitHubOrganization("organization");
	}

	protected WebDriver getDriver() {
		return driver;
	}

	protected static String urlDecode(String value) throws UnsupportedEncodingException {
		return URLDecoder.decode(value, "UTF-8");
	}
}