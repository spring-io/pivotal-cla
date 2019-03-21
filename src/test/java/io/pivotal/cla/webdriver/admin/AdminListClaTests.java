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
package io.pivotal.cla.webdriver.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;

import io.pivotal.cla.security.WithClaAuthorUser;
import io.pivotal.cla.webdriver.BaseWebDriverTests;
import io.pivotal.cla.webdriver.pages.admin.AdminListClasPage;
import io.pivotal.cla.webdriver.pages.admin.AdminListClasPage.Row;

@WithClaAuthorUser
public class AdminListClaTests extends BaseWebDriverTests {

	@Test
	public void listClas() {
		cla.setDescription("this here");
		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla));
		AdminListClasPage listPage = AdminListClasPage.go(driver);
		listPage.assertAt();

		Row row = listPage.row(0);
		assertThat(row.getName()).isEqualTo(cla.getName());
		assertThat(row.getDescription()).isEqualTo(cla.getDescription());
	}

	@Test
	public void listClasDelete() {
		when(mockClaRepository.findAll()).thenReturn(Arrays.asList(cla));
		AdminListClasPage listPage = AdminListClasPage.go(driver);
		listPage.assertAt();

		Row row = listPage.row(0);
		AdminListClasPage deletePage = row.delete();
		deletePage.assertAt();

		verify(mockClaRepository).deleteById(cla.getId());
	}
}
