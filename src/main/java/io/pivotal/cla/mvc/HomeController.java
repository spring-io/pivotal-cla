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
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.pivotal.cla.data.IndividualSignature;
import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.ContributorLicenseAgreementRepository;
import io.pivotal.cla.data.repository.IndividualSignatureRepository;

@Controller
public class HomeController {
	@Autowired
	IndividualSignatureRepository individual;
	@Autowired
	ContributorLicenseAgreementRepository clas;

	@RequestMapping("/")
	public String home(Map<String,Object> model) throws Exception {
		model.put("claName", "pivotal");
		return "index";
	}

	@RequestMapping(value="/",params="logout")
	public String logoutSuccess(Map<String,Object> model) throws Exception {
		model.put("claName", "pivotal");
		model.put("message", "You have been signed out.");
		return "index";
	}

	@RequestMapping("/dashboard")
	public String dashboard(@AuthenticationPrincipal User currentUser, Map<String,Object> model) throws Exception {
		List<IndividualSignature> signatures = individual.findByEmailIn(currentUser.getEmails());
		model.put("clas", signatures.stream().map(IndividualSignature::getCla).collect(Collectors.toList()));
		return "dashboard";
	}
}
