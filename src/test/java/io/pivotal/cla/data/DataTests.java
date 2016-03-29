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
package io.pivotal.cla.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.pivotal.cla.GithubClaApplication;
import io.pivotal.cla.data.repository.ContributorLicenseAgreementRepository;
import io.pivotal.cla.data.repository.CorporateSignatureRepository;
import io.pivotal.cla.data.repository.IndividualSignatureRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GithubClaApplication.class)
@TestPropertySource("/application-test.properties")
public class DataTests {
	@Autowired
	ContributorLicenseAgreementRepository clas;

	@Autowired
	IndividualSignatureRepository signatures;

	@Autowired
	CorporateSignatureRepository cclaSignatures;

	ContributorLicenseAgreement cla;

	IndividualSignature signature;

	@Before
	public void setup() {
		MarkdownContent corporate = new MarkdownContent();
		corporate.setMarkdown("Corporate");
		MarkdownContent individual = new MarkdownContent();
		individual.setMarkdown("Individual");
		cla = new ContributorLicenseAgreement();
		cla.setCorporateContent(corporate);
		cla.setIndividualContent(individual);
		cla.setName("pivotal");
		cla = clas.save(cla);

		signature = new IndividualSignature();
		signature.setCla(cla);
		signature.setCountry("USA");
		signature.setEmail("rwinch@pivotal.io");
		signature.setMailingAddress("123 Seasame Street");
		signature.setName("Rob Winch");
		signature.setTelephone("1234567890");

	}

	@Test
	public void workflow() {

		IndividualSignature robsSignature = signatures.save(signature);

		IndividualSignature didRobSign = signatures.findFirstByClaNameAndEmailInOrderByDateOfSignature(cla.getName(),
				Sets.newSet(robsSignature.getEmail(), "other@example.com"));

		assertThat(didRobSign).isNotNull();
	}

	@Test
	public void legacy() {
		ContributorLicenseAgreement legacy = new ContributorLicenseAgreement();
		legacy.setCorporateContent(cla.getCorporateContent());
		legacy.setIndividualContent(cla.getIndividualContent());
		legacy.setName("pivotal");

		legacy = clas.save(legacy);

		IndividualSignature legacySignature = new IndividualSignature();
		legacySignature.setCla(legacy);
		legacySignature.setCountry("USA");
		legacySignature.setEmail("rwinch@pivotal.io");
		legacySignature.setMailingAddress("123 Seasame Street");
		legacySignature.setName("Rob Winch");
		legacySignature.setTelephone("1234567890");

		signature  = signatures.save(signature);
		legacySignature = signatures.save(legacySignature);

		assertThat(signatures.findFirstByClaNameAndEmailInOrderByDateOfSignature(cla.getName(),
				Sets.newSet(signature.getEmail(), legacySignature.getEmail()))).isNotNull();
	}

	@Test
	public void cclaSignatureNotFoundOnIcla() {
		CorporateSignature signature = new CorporateSignature();
		signature.setCla(cla);
		signature.setCountry("USA");
		signature.setEmail("rwinch@pivotal.io");
		signature.setMailingAddress("123 Seasame Street");
		signature.setName("Rob Winch");
		signature.setTelephone("1234567890");
		signature.setCompanyName("Pivotal");
		signature.setGitHubOrganization("spring-projects");

		cclaSignatures.save(signature);

		assertThat(signatures.findFirstByClaNameAndEmailInOrderByDateOfSignature(cla.getName(),
				Sets.newSet(signature.getEmail()))).isNull();
	}
}
