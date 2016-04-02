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

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.IndividualSignature;
import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.ContributorLicenseAgreementRepository;
import io.pivotal.cla.data.repository.IndividualSignatureRepository;

@Controller
public class ClaController {

	@Autowired
	ContributorLicenseAgreementRepository clas;

	@Autowired
	IndividualSignatureRepository individual;

	@RequestMapping("/sign/{claName}")
	public String signIndex(@AuthenticationPrincipal User user, @PathVariable String claName,
			@RequestParam(required = false) String repositoryId, @RequestParam(required = false) Integer pullRequestId,
			Map<String, Object> model) {

		ContributorLicenseAgreement cla = clas.findByNameAndPrimaryTrue(claName);
		if(cla == null) {
			throw new ResourceNotFoundException();
		}

		IndividualSignature individualSignature = individual.findSignaturesFor(user, claName);

		model.put("repositoryId",repositoryId);
		model.put("pullRequestId", pullRequestId);
		model.put("signed", individualSignature != null);
		model.put("claName", claName);
		return "index";
	}


}
