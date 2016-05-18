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
package io.pivotal.cla.mvc;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.IndividualSignature;
import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.ContributorLicenseAgreementRepository;
import io.pivotal.cla.data.repository.CorporateSignatureRepository;
import io.pivotal.cla.data.repository.IndividualSignatureRepository;
import io.pivotal.cla.service.GitHubService;

@Controller
public class ClaController {

	@Autowired
	ContributorLicenseAgreementRepository clas;

	@Autowired
	GitHubService github;
	@Autowired
	IndividualSignatureRepository individual;
	@Autowired
	CorporateSignatureRepository corporate;

	@RequestMapping("/sign/{claName}")
	public String signIndex(@AuthenticationPrincipal User user, @ModelAttribute ClaRequest claRequest,
			Map<String, Object> model) throws Exception {
		String claName = claRequest.getClaName();
		Integer pullRequestId = claRequest.getPullRequestId();
		String repositoryId = claRequest.getRepositoryId();
		ContributorLicenseAgreement cla = clas.findByNameAndPrimaryTrue(claName);
		if(cla == null) {
			throw new ResourceNotFoundException();
		}

		IndividualSignature individualSignature = individual.findSignaturesFor(user, claName);
		boolean signed = individualSignature != null;
		if(!signed) {
			List<String> organizations = github.getOrganizations(user.getGithubLogin());
			signed = corporate.findSignature(claName, organizations, user.getEmails()) != null;
		}

		model.put("repositoryId",repositoryId);
		model.put("pullRequestId", pullRequestId);
		model.put("signed", signed);
		model.put("claName", claName);
		return "index";
	}


}
