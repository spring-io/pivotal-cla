/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.pivotal.cla.data.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.pivotal.cla.PivotalClaApplication;
import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.DataUtils;
import io.pivotal.cla.data.IndividualSignature;
import io.pivotal.cla.data.User;
import io.pivotal.cla.junit.JpaTests;
import io.pivotal.cla.service.ClaService;
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
@Category(JpaTests.class)
public class IndividualSignatureRepositoryTests {

	@Autowired
	ContributorLicenseAgreementRepository clas;

	@Autowired
	IndividualSignatureRepository signatures;

	@Autowired
	ClaService claService;

	ContributorLicenseAgreement cla;

	ContributorLicenseAgreement springCla;

	IndividualSignature signature;

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
	public void findSignaturesForUserGitHubLoginAndEmails() {
		assertThat(claService.findIndividualSignaturesFor(user, cla.getName())).isNotNull();
	}

	@Test
	public void findSignaturesForUserEmailOnly() {
		user.setGitHubLogin("notfound" + user.getGitHubLogin());

		assertThat(claService.findIndividualSignaturesFor(user, cla.getName())).isNotNull();
	}

	@Test
	public void findSignaturesForUserGitHubLoginOnly() {
		user.setEmails(Collections.emptySet());

		assertThat(claService.findIndividualSignaturesFor(user, cla.getName())).isNotNull();
	}

	@Test
	public void findSignatureForSupersedingCla() {
		assertThat(claService.findIndividualSignaturesFor(user, springCla.getName())).isNotNull();
	}

	@Test
	public void findSignatureForMultipleSigned() {
		IndividualSignature springSignature = createSignature(springCla, user);
		signatures.save(springSignature);

		assertThat(claService.findIndividualSignaturesFor(user, springCla.getName())).isNotNull();
	}

	@Test
	public void findSignatureNotFoundCla() {
		assertThat(claService.findIndividualSignaturesFor(user, "notfound")).isNull();;
	}

	@Test
	public void findSignatureNotUser() {
		user.setGitHubLogin("notfound" + user.getGitHubLogin());
		user.setEmails(Collections.singleton("notfound@example.com"));

		assertThat(claService.findIndividualSignaturesFor(user, cla.getName())).isNull();;
	}

	@Test
	public void findAllSignaturesForUserGitHubLoginAndEmails() {
		assertThat(signatures.findSignaturesFor(new PageRequest(0, 1), user)).isNotNull();
	}

	@Test
	public void findAllSignaturesForUserEmailOnly() {
		user.setGitHubLogin("notfound" + user.getGitHubLogin());

		assertThat(signatures.findSignaturesFor(new PageRequest(0, 1), user)).isNotNull();
	}

	@Test
	public void findAllSignaturesForUserGitHubLoginOnly() {
		user.setEmails(Collections.emptySet());

		assertThat(signatures.findSignaturesFor(new PageRequest(0, 1), user)).isNotNull();
	}

	private static IndividualSignature createSignature(ContributorLicenseAgreement cla, User user) {
		IndividualSignature signature = DataUtils.iclaSignature(cla);
		signature.setGitHubLogin(user.getGitHubLogin());
		signature.setEmail(user.getEmails().iterator().next());
		return signature;
	}
}