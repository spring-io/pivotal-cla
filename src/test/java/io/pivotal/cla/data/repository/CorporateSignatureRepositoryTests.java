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
package io.pivotal.cla.data.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.pivotal.cla.PivotalClaApplication;
import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.CorporateSignature;
import io.pivotal.cla.data.DataUtils;
import io.pivotal.cla.data.User;
import io.pivotal.cla.test.context.SystemDataActiveProfiles;

/**
 * @author Rob Winch
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(PivotalClaApplication.class)
@TestPropertySource(locations="/application-test.properties")
@Transactional
@SystemDataActiveProfiles
public class CorporateSignatureRepositoryTests {

	@Autowired
	ContributorLicenseAgreementRepository clas;

	@Autowired
	CorporateSignatureRepository signatures;

	ContributorLicenseAgreement cla;

	ContributorLicenseAgreement springCla;

	CorporateSignature signature;

	User user;

	@Before
	public void setup() {
		user = DataUtils.createUser();

		cla = clas.findByNameAndPrimaryTrue("pivotal");
		signature = createSignature(cla, user);

		signatures.save(signature);

		springCla = DataUtils.createSpringCla();
		springCla.setSupersedingCla(cla);

		springCla = clas.save(springCla);
	}

	@Test
	public void findSignatureNotFoundOrganization() {
		assertThat(signatures.findSignature(cla.getName(), Arrays.asList("notorganization"), user.getEmails())).isNull();
	}

	@Test
	public void findSignatureNotFoundCla() {
		assertThat(signatures.findSignature("notfound", Arrays.asList(signature.getGitHubOrganization()), user.getEmails())).isNull();
	}

	@Test
	public void findSignature() {
		assertThat(signatures.findSignature(cla.getName(), Arrays.asList("notorganization", signature.getGitHubOrganization()), user.getEmails())).isNotNull();
	}

	@Test
	public void findSignatureSupersedingCla() {
		assertThat(signatures.findSignature(springCla.getName(), Arrays.asList("notorganization", signature.getGitHubOrganization()), user.getEmails())).isNotNull();
	}

	@Test
	public void findSignatureForMultipleSigned() {
		CorporateSignature springSignature = createSignature(springCla, user);
		signatures.save(springSignature);

		assertThat(signatures.findSignature(springCla.getName(), Arrays.asList(signature.getGitHubOrganization(), springSignature.getGitHubOrganization()), user.getEmails())).isNotNull();
	}

	@Test
	public void findSignatureEmailDomain() {
		CorporateSignature emailSignature = createSignature(cla, user);
		emailSignature.setGitHubOrganization(null);
		emailSignature.setEmailDomain("gmail.com");

		emailSignature = signatures.save(emailSignature);

		assertThat(signatures.findSignature(springCla.getName(), Arrays.asList("notorganization"), user.getEmails())).isNotNull();
	}

	/**
	 * Happens in GitHubHooksController when user has not yet authenticated.
	 */
	@Test
	public void findSignatureNullEmails() {
		CorporateSignature emailSignature = createSignature(cla, user);
		emailSignature.setGitHubOrganization(null);
		emailSignature.setEmailDomain("gmail.com");

		emailSignature = signatures.save(emailSignature);

		assertThat(signatures.findSignature(springCla.getName(), Arrays.asList("notorganization"), null)).isNull();
	}

	private static CorporateSignature createSignature(ContributorLicenseAgreement cla, User user) {
		CorporateSignature signature = DataUtils.cclaSignature(cla);
		signature.setGithubLogin(user.getGithubLogin());
		signature.setEmail(user.getEmails().iterator().next());
		return signature;
	}
}
