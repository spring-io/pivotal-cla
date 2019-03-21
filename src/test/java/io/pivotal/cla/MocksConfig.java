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
package io.pivotal.cla;

import static org.mockito.Mockito.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.pivotal.cla.data.repository.AccessTokenRepository;
import io.pivotal.cla.data.repository.ContributorLicenseAgreementRepository;
import io.pivotal.cla.data.repository.CorporateSignatureRepository;
import io.pivotal.cla.data.repository.IndividualSignatureRepository;
import io.pivotal.cla.data.repository.UserRepository;
import io.pivotal.cla.service.github.GitHubApi;

@Configuration
@Profile("test")
public class MocksConfig {

	@Bean
	@Primary
	public UserRepository mockUserRepo() {
		return mock(UserRepository.class);
	}

	@Bean
	@Primary
	public GitHubApi mockGitHubService() {
		return mock(GitHubApi.class);
	}

	@Bean
	@Primary
	public IndividualSignatureRepository mockIndividualSignature() {
		IndividualSignatureRepository individual = mock(IndividualSignatureRepository.class);
		return individual;
	}

	@Bean
	@Primary
	public CorporateSignatureRepository mockCorporateSignature() {
		return mock(CorporateSignatureRepository.class);
	}

	@Bean
	@Primary
	public ContributorLicenseAgreementRepository mockClaRepository() {
		return mock(ContributorLicenseAgreementRepository.class);
	}

	@Bean
	@Primary
	public AccessTokenRepository mockAccessTokenRepository() {
		return mock(AccessTokenRepository.class);
	}

	@Controller
	static class GitHubOAuthAuthorizeController {

		@RequestMapping("/login/oauth/authorize")
		public String response(@RequestParam String state) {
			return "redirect:https://localhost/login/oauth2/github?code=abc&state=" + state;
		}
	}
}
