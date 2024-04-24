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
package io.pivotal.cla.webdriver.admin;

import io.pivotal.cla.security.WithAdminUser;
import io.pivotal.cla.service.ClaService;
import io.pivotal.cla.webdriver.BaseWebDriverTests;
import io.pivotal.cla.webdriver.pages.admin.AdminLookupSignaturePage;
import io.pivotal.cla.webdriver.pages.admin.AdminLookupSignaturePage.FindSignatureForm;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WithAdminUser
public class AdminLookupSignatureTests extends BaseWebDriverTests {
	@MockBean
	ClaService claService;

	@Test
	public void navigationWorks() {
		AdminLookupSignaturePage find = AdminLookupSignaturePage.to(getDriver());
		find.assertAt();

		find = find.lookup();
		find.assertAt();
	}

	@Test
	public void fieldsRequired() {
		AdminLookupSignaturePage find = AdminLookupSignaturePage.to(getDriver());
		find = find.findForm()
				.claName("")
				.gitHubLogin("")
				.submit();

		find.assertAt();

		FindSignatureForm form = find.findForm();
		form.assertClaName().hasRequiredError();
		form.assertGitHubLogin().hasRequiredError();
	}

	@Test
	public void claNameDefaulted() {
		when(this.claService.hasSigned("rwinch", "pivotal")).thenReturn(true);
		AdminLookupSignaturePage find = AdminLookupSignaturePage.to(getDriver());
		FindSignatureForm form = find.findForm();
		form.assertClaName().hasValue("pivotal");

		find = form.gitHubLogin("rwinch").submit();

		assertThat(find.isSignatureFound()).isTrue();
	}
}