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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

public class RequiresChannelTests extends BaseWebDriverTests {

	@Test
	public void xforwardedPortRequriesHttps() throws Exception {
		String redirect = mockMvc.perform(get("/").header("x-forwarded-port", "pivotal.io"))
			.andReturn().getResponse().getRedirectedUrl();

		assertThat(redirect).startsWith("https://");
	}

	@Test
	public void noxforwardedPortAllowsHttp() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(status().isOk());
	}
}
