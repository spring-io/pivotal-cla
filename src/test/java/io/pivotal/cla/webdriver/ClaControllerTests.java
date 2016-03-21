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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;

import io.pivotal.cla.security.WithSigningUser;
import io.pivotal.cla.webdriver.pages.SignIclaPage;
import io.pivotal.cla.webdriver.pages.SignIclaPage.Form;

@WithSigningUser
public class ClaControllerTests extends BaseWebDriverTests {

	@Test
	public void view() {
		when(mockClaRepository.findByName(cla.getName())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		assertThat(signPage.getIndividualCla()).isEqualTo(cla.getIndividualContent().getHtml());
	}

	@Test
	public void signNameRequired() {
		when(mockClaRepository.findByName(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.telephone("123.456.7890")
			.sign(SignIclaPage.class);

		signPage.assertAt();
		form = signPage.form();
		form.assertName().hasRequiredError();
		form.assertEmail().hasNoErrors();
		form.assertMailingAddress().hasNoErrors();
		form.assertCountry().hasNoErrors();
		form.assertTelephone().hasNoErrors();
	}

	@Test
	public void signEmailRequired() {
		when(mockClaRepository.findByName(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.name("Rob Winch")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.telephone("123.456.7890")
			.sign(SignIclaPage.class);

		signPage.assertAt();
		form = signPage.form();
		form.assertName().hasNoErrors();
		form.assertEmail().hasRequiredError();
		form.assertMailingAddress().hasNoErrors();
		form.assertCountry().hasNoErrors();
		form.assertTelephone().hasNoErrors();
	}

	@Test
	public void signMailingAddressRequired() {
		when(mockClaRepository.findByName(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.name("Rob Winch")
			.email("rob@gmail.com")
			.country("USA")
			.telephone("123.456.7890")
			.sign(SignIclaPage.class);

		signPage.assertAt();
		form = signPage.form();
		form.assertName().hasNoErrors();
		form.assertEmail().hasNoErrors();
		form.assertMailingAddress().hasRequiredError();
		form.assertCountry().hasNoErrors();
		form.assertTelephone().hasNoErrors();
	}

	@Test
	public void signCountryRequired() {
		when(mockClaRepository.findByName(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.name("Rob Winch")
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.telephone("123.456.7890")
			.sign(SignIclaPage.class);

		signPage.assertAt();
		form = signPage.form();
		form.assertName().hasNoErrors();
		form.assertEmail().hasNoErrors();
		form.assertMailingAddress().hasNoErrors();
		form.assertCountry().hasRequiredError();
		form.assertTelephone().hasNoErrors();
	}

	@Test
	public void signTelephoneRequired() {
		when(mockClaRepository.findByName(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		Form form = signPage.form();

		signPage = form
			.name("Rob Winch")
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.sign(SignIclaPage.class);

		signPage.assertAt();
		form = signPage.form();
		form.assertName().hasNoErrors();
		form.assertEmail().hasNoErrors();
		form.assertMailingAddress().hasNoErrors();
		form.assertCountry().hasNoErrors();
		form.assertTelephone().hasRequiredError();
	}

	@Test
	public void fieldsRepopulatedOnError() {
		when(mockClaRepository.findByName(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		signPage = signPage.form()
			.name("Rob Winch")
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.sign(SignIclaPage.class);

		signPage.assertAt();

		Form form = signPage.form();
		form.assertName().hasValue("Rob Winch");
		form.assertEmail().hasValue("rob@gmail.com");
		form.assertMailingAddress().hasValue("123 Seasame St");
		form.assertCountry().hasValue("USA");

		signPage = SignIclaPage.go(getDriver(), cla.getName());

		signPage = signPage.form()
			.telephone("123.456.7890")
			.sign(SignIclaPage.class);

		signPage.form()
			.assertTelephone().hasValue("123.456.7890");
	}

	@Test
	public void signNoRepositoryIdAndNoPullRequestId() {
		when(mockClaRepository.findByName(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName());

		signPage = signPage.form()
			.name("Rob Winch")
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.telephone("123.456.7890")
 			.sign(SignIclaPage.class);

		signPage.assertAt();
	}


	@Test
	public void signNoRepositoryIdWithPullRequestId() {
		when(mockClaRepository.findByName(cla.getName())).thenReturn(cla);
		when(mockClaRepository.findOne(cla.getId())).thenReturn(cla);

		SignIclaPage signPage = SignIclaPage.go(getDriver(), cla.getName(), "rwinch/176_test", 2);

		signPage = signPage.form()
			.name("Rob Winch")
			.email("rob@gmail.com")
			.mailingAddress("123 Seasame St")
			.country("USA")
			.telephone("123.456.7890")
 			.sign(SignIclaPage.class);

		signPage.assertAt();
	}
}
