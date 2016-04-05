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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

import io.pivotal.cla.security.WithSigningUser;
import io.pivotal.cla.webdriver.pages.AboutPage;
import io.pivotal.cla.webdriver.pages.HomePage;
import io.pivotal.cla.webdriver.pages.SignCclaPage;
import io.pivotal.cla.webdriver.pages.SignClaPage;
import io.pivotal.cla.webdriver.pages.SignIclaPage;

public class HomeControllerTests extends BaseWebDriverTests {

	@Test
	public void homePermitAll() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(status().isOk());
	}

	@Test
	public void home() throws Exception {
		SignClaPage home = HomePage.go(driver);
		home.assertAt();
	}

	@Test
	@WithSigningUser
	public void learnMoreLink() {
		SignClaPage home = HomePage.go(driver);
		AboutPage aboutPage = home.learnMore();
		aboutPage.assertAt();
	}

	@Test
	@WithSigningUser
	public void signIcla() {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);

		SignClaPage home = HomePage.go(driver);
		SignIclaPage sign = home.signIcla(SignIclaPage.class);
		sign.assertAt();
	}

	@Test
	@WithSigningUser
	public void signCcla() {
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);

		SignClaPage home = HomePage.go(driver);
		SignCclaPage sign = home.signCcla(SignCclaPage.class);
		sign.assertAt();
	}

	@Test
	@WithSigningUser
	public void profile() {
		SignClaPage homePage = HomePage.go(driver);
		homePage.profile();
	}
}
