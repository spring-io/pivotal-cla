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
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import io.pivotal.cla.data.IndividualSignature;
import io.pivotal.cla.security.WithSigningUser;
import io.pivotal.cla.webdriver.pages.AboutPage;
import io.pivotal.cla.webdriver.pages.DashboardPage;
import io.pivotal.cla.webdriver.pages.DashboardPage.Signature;
import io.pivotal.cla.webdriver.pages.HomePage;
import io.pivotal.cla.webdriver.pages.SignCclaPage;
import io.pivotal.cla.webdriver.pages.SignIclaPage;
import io.pivotal.cla.webdriver.pages.SignedPage;

public class HomeControllerTests extends BaseWebDriverTests {

	@Test
	public void homePermitAll() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(status().isOk());
	}

	@Test
	public void home() throws Exception {
		HomePage home = HomePage.go(driver);
		home.assertAt();
	}

	@Test
	@WithSigningUser
	public void learnMoreLink() {
		HomePage home = HomePage.go(driver);
		AboutPage aboutPage = home.learnMore();
		aboutPage.assertAt();
	}

	@Test
	@WithSigningUser
	public void signIcla() {
		cla.setName("pivotal");
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);

		HomePage home = HomePage.go(driver);
		SignIclaPage sign = home.signIcla(SignIclaPage.class);
		sign.assertAt();
	}

	@Test
	@WithSigningUser
	public void signCcla() {
		cla.setName("pivotal");
		when(mockClaRepository.findByNameAndPrimaryTrue(cla.getName())).thenReturn(cla);

		HomePage home = HomePage.go(driver);
		SignCclaPage sign = home.signCcla(SignCclaPage.class);
		sign.assertAt();
	}

	@Test
	public void dashboardRequiresAuthentication() throws Exception {
		mockMvc.perform(get("/dashboard"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	@WithSigningUser
	@SuppressWarnings("unchecked")
	public void dashboardNavigation() throws Exception {
		when(mockIndividualSignatureRepository.findByEmailIn(anySet())).thenReturn(Collections.emptyList());

		HomePage home = HomePage.go(driver);
		DashboardPage dashboard = home.dashboard();
		dashboard.assertAt();
	}

	@Test
	@WithSigningUser
	@SuppressWarnings("unchecked")
	public void dashboardNoSignatures() {
		when(mockIndividualSignatureRepository.findByEmailIn(anySet())).thenReturn(Collections.emptyList());

		DashboardPage dashboard = DashboardPage.go(driver);
		dashboard.assertAt();
		dashboard.assertNoSignatures();
	}

	@Test
	@WithSigningUser
	@SuppressWarnings("unchecked")
	public void dashboardSignature() {
		IndividualSignature signature = new IndividualSignature();
		signature.setCla(cla);
		when(mockIndividualSignatureRepository.findByEmailIn(anySet())).thenReturn(Arrays.asList(individualSignature));
		when(mockIndividualSignatureRepository.findByClaNameAndEmailIn(eq(cla.getName()), anySet())).thenReturn(individualSignature);

		DashboardPage dashboard = DashboardPage.go(driver);
		dashboard.assertAt();

		List<Signature> signatures = dashboard.getSignatures();
		assertThat(signatures).extracting(Signature::getName).containsOnly(cla.getName());

		SignedPage view = signatures.get(0).view();
		view.assertAt();
		view.asserSigned();
	}

	@Test
	@WithSigningUser
	public void profile() {
		HomePage homePage = HomePage.go(driver);
		homePage.profile();
	}
}
