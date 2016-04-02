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

import io.pivotal.cla.security.WithSigningUserFactory;

/**
 * @author Rob Winch
 *
 */
public class DataUtils {

	public static User createUser() {
		return WithSigningUserFactory.create();
	}

	public static ContributorLicenseAgreement createPivotalCla() {
		MarkdownContent corporate = new MarkdownContent();
		corporate.setMarkdown("Corporate");
		MarkdownContent individual = new MarkdownContent();
		individual.setMarkdown("Individual");

		ContributorLicenseAgreement cla = new ContributorLicenseAgreement();
		cla.setCorporateContent(corporate);
		cla.setIndividualContent(individual);
		cla.setName("pivotal");

		return cla;
	}

	public static ContributorLicenseAgreement createSpringCla() {
		MarkdownContent corporate = new MarkdownContent();
		corporate.setMarkdown("Corporate");
		MarkdownContent individual = new MarkdownContent();
		individual.setMarkdown("Individual");

		ContributorLicenseAgreement cla = new ContributorLicenseAgreement();
		cla.setCorporateContent(corporate);
		cla.setIndividualContent(individual);
		cla.setName("spring");
		cla.setSupersedingCla(createPivotalCla());

		return cla;
	}

	public static IndividualSignature iclaSignature(ContributorLicenseAgreement cla) {
		IndividualSignature signature = new IndividualSignature();
		signature.setCla(cla);
		signature.setCountry("USA");
		signature.setEmail("rwinch@pivotal.io");
		signature.setMailingAddress("123 Seasame Street");
		signature.setName("Rob Winch");
		signature.setTelephone("1234567890");
		return signature;
	}

	public static CorporateSignature cclaSignature(ContributorLicenseAgreement cla) {
		CorporateSignature signature = new CorporateSignature();
		signature.setCla(cla);
		signature.setGitHubOrganization(cla.getName());
		signature.setCountry("USA");
		signature.setEmail("rwinch@pivotal.io");
		signature.setMailingAddress("123 Seasame Street");
		signature.setName("Rob Winch");
		signature.setTelephone("1234567890");
		return signature;
	}
}
