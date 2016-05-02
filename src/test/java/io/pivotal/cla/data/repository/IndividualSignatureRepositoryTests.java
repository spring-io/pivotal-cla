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

import java.util.Collections;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.pivotal.cla.GithubClaApplication;
import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.DataUtils;
import io.pivotal.cla.data.IndividualSignature;
import io.pivotal.cla.data.User;

/**
 * @author Rob Winch
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GithubClaApplication.class)
@TestPropertySource(locations="/application-test.properties")
@Transactional
public class IndividualSignatureRepositoryTests {

	@Autowired
	ContributorLicenseAgreementRepository clas;

	@Autowired
	IndividualSignatureRepository signatures;

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
		assertThat(signatures.findSignaturesFor(user, cla.getName())).isNotNull();
	}

	@Test
	public void findSignaturesForUserEmailOnly() {
		user.setGithubLogin("notfound" + user.getGithubLogin());

		assertThat(signatures.findSignaturesFor(user, cla.getName())).isNotNull();
	}

	@Test
	public void findSignaturesForUserGithubLoginOnly() {
		user.setEmails(Collections.emptySet());

		assertThat(signatures.findSignaturesFor(user, cla.getName())).isNotNull();
	}

	@Test
	public void findSignatureForSupersedingCla() {
		assertThat(signatures.findSignaturesFor(user, springCla.getName())).isNotNull();
	}

	@Test
	public void findSignatureForMultipleSigned() {
		IndividualSignature springSignature = createSignature(springCla, user);
		signatures.save(springSignature);

		assertThat(signatures.findSignaturesFor(user, springCla.getName())).isNotNull();
	}

	@Test
	public void findSignatureNotFoundCla() {
		assertThat(signatures.findSignaturesFor(user, "notfound")).isNull();;
	}

	@Test
	public void findSignatureNotUser() {
		user.setGithubLogin("notfound" + user.getGithubLogin());
		user.setEmails(Collections.singleton("notfound@example.com"));

		assertThat(signatures.findSignaturesFor(user, cla.getName())).isNull();;
	}

	private static IndividualSignature createSignature(ContributorLicenseAgreement cla, User user) {
		IndividualSignature signature = DataUtils.iclaSignature(cla);
		signature.setGithubLogin(user.getGithubLogin());
		signature.setEmail(user.getEmails().iterator().next());
		return signature;
	}
}