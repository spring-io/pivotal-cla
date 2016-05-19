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
package io.pivotal.cla.mvc.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.repository.AccessTokenRepository;
import io.pivotal.cla.data.repository.ContributorLicenseAgreementRepository;
import io.pivotal.cla.service.ClaService;
import io.pivotal.cla.service.github.GitHubApi;

@PreAuthorize("hasRole('ADMIN')")
public class AdminClaController {

	@Autowired
	GitHubApi gitHub;

	@Autowired
	ContributorLicenseAgreementRepository claRepo;

	@Autowired
	AccessTokenRepository tokenRepo;

	@Autowired
	ClaService claService;

	protected List<ContributorLicenseAgreement> findPrimaryClas() {
		List<ContributorLicenseAgreement> result = claRepo.findByPrimaryTrue();
		return sort(result);
	}

	protected List<ContributorLicenseAgreement> findAllClas() {
		List<ContributorLicenseAgreement> result = new ArrayList<>();
		claRepo.findAll().forEach(result::add);
		return sort(result);
	}

	protected List<ContributorLicenseAgreement> sort(List<ContributorLicenseAgreement> result) {
		Collections.sort(result, (lhs, rhs) -> lhs.getName().compareTo(rhs.getName()));
		return result;
	}
}