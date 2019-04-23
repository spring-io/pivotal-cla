/*
 * Copyright 2002-2018 the original author or authors.
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
package io.pivotal.cla.security;

import io.pivotal.cla.data.IndividualSignature;
import io.pivotal.cla.data.repository.CorporateSignatureRepository;
import io.pivotal.cla.data.repository.IndividualSignatureRepository;
import io.pivotal.cla.mvc.support.ImportedSignaturesSessionAttr;
import io.pivotal.cla.service.github.GitHubApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Joe Grandja
 */
public class GitHubAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private final AuthenticationSuccessHandler savedRequestSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();

	@Autowired
	private GitHubApi gitHubApi;

	@Autowired
	private IndividualSignatureRepository individual;

	@Autowired
	private CorporateSignatureRepository corporate;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) throws IOException, ServletException {

		GitHubOAuth2User user = (GitHubOAuth2User) authentication.getPrincipal();
		if (user.isNewUser()) {
			List<IndividualSignature> individualSignatures = this.individual.findSignaturesFor(PageRequest.of(0, 1), user);
			boolean signed = !individualSignatures.isEmpty();
			if (!signed) {
				List<String> organizations = this.gitHubApi.getOrganizations(user.getGitHubLogin());
				signed = !this.corporate.findSignatures(PageRequest.of(0, 1), organizations, user.getEmails()).isEmpty();
			}
			if (signed) {
				new ImportedSignaturesSessionAttr(new ServletWebRequest(request)).setValue(true);
			}
		}

		this.savedRequestSuccessHandler.onAuthenticationSuccess(request, response, authentication);
	}
}