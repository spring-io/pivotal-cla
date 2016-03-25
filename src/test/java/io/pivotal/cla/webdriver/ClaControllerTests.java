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

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import io.pivotal.cla.security.WithSigningUser;
import io.pivotal.cla.security.WithSigningUserFactory;
import io.pivotal.cla.webdriver.pages.AboutPage;
import io.pivotal.cla.webdriver.pages.SignCclaPage;
import io.pivotal.cla.webdriver.pages.SignClaPage;
import io.pivotal.cla.webdriver.pages.SignIclaPage;

@WithSigningUser
public class ClaControllerTests extends BaseWebDriverTests {

	@Before
	public void setup() {
		super.setup();
		cla.setName("pivotal");
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);
	}

	@Test
	public void claPivotal() throws Exception {
		SignClaPage home = SignClaPage.go(driver, cla.getName());
		home.assertAt();
		home.assertClaLinks(cla.getName(), null);
	}

	@Test
	public void claPivotaLegacySpring() throws Exception {
		SignClaPage home = SignClaPage.go(driver, cla.getName(), "spring");
		home.assertAt();
		home.assertClaLinks(cla.getName(), "spring");
	}

	@Test
	public void claPivotalIndividualSigned() {
		when(mockIndividualSignatureRepository.getSignature(WithSigningUserFactory.create(),cla.getName(), null)).thenReturn(individualSignature);

		SignClaPage home = SignClaPage.go(driver, cla.getName());
		home.assertAt();
		home.assertSigned();
	}

	@Test
	public void claPivotalIndividualLegacySigned() {
		String claName = cla.getName() + "-notsigned";
		when(mockIndividualSignatureRepository.getSignature(WithSigningUserFactory.create(),claName, cla.getName())).thenReturn(individualSignature);

		SignClaPage home = SignClaPage.go(driver, claName, cla.getName());
		home.assertAt();
		home.assertSigned();
	}

	@Test
	@WithSigningUser
	public void learnMoreLink() {
		SignClaPage home = SignClaPage.go(driver, cla.getName());
		AboutPage aboutPage = home.learnMore();
		aboutPage.assertAt();
	}

	@Test
	@WithSigningUser
	public void signIcla() {
		SignClaPage home = SignClaPage.go(driver, cla.getName());
		SignIclaPage sign = home.signIcla(SignIclaPage.class);
		sign.assertAt();
	}

	@Test
	@WithSigningUser
	public void signCcla() {
		SignClaPage home = SignClaPage.go(driver, cla.getName());
		SignCclaPage sign = home.signCcla(SignCclaPage.class);
		sign.assertAt();
	}
}
