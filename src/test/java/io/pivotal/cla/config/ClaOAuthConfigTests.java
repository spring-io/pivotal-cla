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
package io.pivotal.cla.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Rob Winch
 *
 */
public class ClaOAuthConfigTests {
	ClaOAuthConfig config;

	@Before
	public void setup() {
		config = new ClaOAuthConfig();
	}

	@Test
	public void getGitHubApiBaseUrl() {
		assertThat(config.getGitHubApiBaseUrl()).isEqualTo("https://api.github.com/");
	}

	@Test
	public void getGitHubApiBaseUrlCustomSchemePort() {
		config.setScheme("http");
		config.setPort(1234);
		assertThat(config.getGitHubApiBaseUrl()).isEqualTo("http://api.github.com:1234/");
	}

	@Test
	public void getGitHubBaseUrl() {
		assertThat(config.getGitHubBaseUrl()).isEqualTo("https://github.com/");
	}

	@Test
	public void getGitHubBaseUrlCustomSchemePort() {
		config.setScheme("http");
		config.setPort(1234);
		assertThat(config.getGitHubBaseUrl()).isEqualTo("http://github.com:1234/");
	}
}
