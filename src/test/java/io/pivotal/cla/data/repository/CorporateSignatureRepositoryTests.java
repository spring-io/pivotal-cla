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

import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.CorporateSignature;
import io.pivotal.cla.data.DataUtils;
import io.pivotal.cla.data.User;
import io.pivotal.cla.junit.JpaTests;
import io.pivotal.cla.test.context.SystemDataActiveProfiles;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Rob Winch
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DataJpaTest
@TestPropertySource(locations="/application-test.properties")
@SystemDataActiveProfiles
@Category(JpaTests.class)
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

	@Test
	public void findSignatureEmptyEmails() {
		CorporateSignature emailSignature = createSignature(cla, user);
		emailSignature.setGitHubOrganization(null);
		emailSignature.setEmailDomain("gmail.com");

		emailSignature = signatures.save(emailSignature);

		assertThat(signatures.findSignature(springCla.getName(), Arrays.asList("notorganization"), Collections.emptyList())).isNull();
	}

	@Test
	public void findSignatureByEmptyGitHubOrganizations() {
		CorporateSignature emailSignature = createSignature(cla, user);
		emailSignature.setGitHubOrganization(null);
		emailSignature.setEmailDomain("gmail.com");

		emailSignature = signatures.save(emailSignature);

		assertThat(signatures.findSignature(springCla.getName(), Arrays.asList(), null)).isNull();
	}

	@Test
	public void findSignaturesNotFoundOrganization() {
		assertThat(signatures.findSignatures(PageRequest.of(0, 1), Arrays.asList("notorganization"), user.getEmails())).isEmpty();
	}

	@Test
	public void findSignatures() {
		assertThat(signatures.findSignatures(PageRequest.of(0, 1), Arrays.asList("notorganization", signature.getGitHubOrganization()), user.getEmails())).isNotNull();
	}

	@Test
	public void findSignaturesSupersedingCla() {
		assertThat(signatures.findSignatures(PageRequest.of(0, 1), Arrays.asList("notorganization", signature.getGitHubOrganization()), user.getEmails())).isNotNull();
	}

	@Test
	public void findSignaturesForMultipleSigned() {
		CorporateSignature springSignature = createSignature(springCla, user);
		signatures.save(springSignature);

		assertThat(signatures.findSignatures(PageRequest.of(0, 4), Arrays.asList(signature.getGitHubOrganization(), springSignature.getGitHubOrganization()), user.getEmails())).hasSize(2);
	}

	@Test
	public void findSignaturesEmailDomain() {
		CorporateSignature emailSignature = createSignature(cla, user);
		emailSignature.setGitHubOrganization(null);
		emailSignature.setEmailDomain("gmail.com");

		emailSignature = signatures.save(emailSignature);

		assertThat(signatures.findSignatures(PageRequest.of(0, 1), Arrays.asList("notorganization"), user.getEmails())).hasSize(1);
	}

	/**
	 * Happens in GitHubHooksController when user has not yet authenticated.
	 */
	@Test
	public void findSignaturesNullEmails() {
		CorporateSignature emailSignature = createSignature(cla, user);
		emailSignature.setGitHubOrganization(null);
		emailSignature.setEmailDomain("gmail.com");

		emailSignature = signatures.save(emailSignature);

		assertThat(signatures.findSignatures(PageRequest.of(0, 1), Arrays.asList("notorganization"), null)).isEmpty();
	}

	@Test
	public void findSignaturesEmptyEmails() {
		CorporateSignature emailSignature = createSignature(cla, user);
		emailSignature.setGitHubOrganization(null);
		emailSignature.setEmailDomain("gmail.com");

		emailSignature = signatures.save(emailSignature);

		assertThat(signatures.findSignatures(PageRequest.of(0, 1), Arrays.asList("notorganization"), Collections.emptyList())).isEmpty();
	}

	@Test
	public void findSignaturesByEmptyGitHubOrganizations() {
		CorporateSignature emailSignature = createSignature(cla, user);
		emailSignature.setGitHubOrganization(null);
		emailSignature.setEmailDomain("gmail.com");

		emailSignature = signatures.save(emailSignature);

		assertThat(signatures.findSignatures(PageRequest.of(0, 1), Arrays.asList(), null)).isEmpty();
	}

	private static CorporateSignature createSignature(ContributorLicenseAgreement cla, User user) {
		CorporateSignature signature = DataUtils.cclaSignature(cla);
		signature.setGitHubLogin(user.getGitHubLogin());
		signature.setEmail(user.getEmails().iterator().next());
		return signature;
	}
}
