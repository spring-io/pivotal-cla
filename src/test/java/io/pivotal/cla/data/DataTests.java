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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.pivotal.cla.GithubClaApplication;
import io.pivotal.cla.data.repository.ContributorLicenseAgreementRepository;
import io.pivotal.cla.data.repository.IndividualSignatureRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(GithubClaApplication.class)
public class DataTests {
	@Autowired
	ContributorLicenseAgreementRepository clas;

	@Autowired
	IndividualSignatureRepository signatures;

	@Test
	public void workflow() {
		ContributorLicenseAgreeement cla = new ContributorLicenseAgreeement();
		cla.setCorporateContent("Corporate");
		cla.setIndividualContent("Individual");
		cla.setName("spring");

		ContributorLicenseAgreeement springCla = clas.save(cla);

		IndividualSignature signature = new IndividualSignature();
		signature.setCla(springCla);
		signature.setCountry("USA");
		signature.setEmail("rwinch@pivotal.io");
		signature.setMailingAddress("123 Seasame Street");
		signature.setName("Rob Winch");
		signature.setTelephone("1234567890");

		IndividualSignature robsSignature = signatures.save(signature);

		IndividualSignature didRobSign = signatures.findByClaNameAndEmailIn(springCla.getName(),
				Sets.newSet(robsSignature.getEmail(), "other@example.com"));

		assertThat(didRobSign).isNotNull();
	}
}
