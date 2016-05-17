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

import java.util.Map;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.MarkdownContent;
import io.pivotal.cla.data.User;

/**
 * @author Rob Winch
 *
 */
@Controller
@PreAuthorize("hasRole('CLA_AUTHOR')")
public class AdminCrudClaController extends AdminClaController {

	@RequestMapping("/admin/cla/")
	public String listClas(Map<String, Object> model) throws Exception {
		model.put("clas", findAllClas());
		return "admin/cla/index";
	}

	@RequestMapping("/admin/cla/create")
	public String createClaForm(Map<String, Object> model) throws Exception {
		Iterable<ContributorLicenseAgreement> clas = claRepo.findAll();
		model.put("licenses", clas);
		model.put("claForm", new ClaForm());
		return "admin/cla/form";
	}

	@RequestMapping(value = "/admin/cla", method = RequestMethod.POST)
	public String createCla(@AuthenticationPrincipal User user, @Valid ClaForm claForm, BindingResult result, Map<String, Object> model)
			throws Exception {
		boolean primary = claForm.isPrimary();
		if(primary) {
			ContributorLicenseAgreement existingPrimaryCla = claRepo.findByNameAndPrimaryTrue(claForm.getName());
			if(existingPrimaryCla != null) {
				result.rejectValue("primary","errors.primary.exists", "A primary CLA with this name already exists");
			}
		}
		if (result.hasErrors()) {
			Iterable<ContributorLicenseAgreement> clas = claRepo.findAll();
			model.put("licenses", clas);
			return "admin/cla/form";
		}

		ContributorLicenseAgreement supersedingCla = null;
		if(claForm.getSupersedingCla() != null) {
			supersedingCla = claRepo.findOne(claForm.getSupersedingCla());
		}
		String accessToken = user.getAccessToken();

		MarkdownContent individual = claForm.getIndividualContent();
		String individualHtml = github.markdownToHtml(accessToken, individual.getMarkdown());
		individual.setHtml(individualHtml);

		MarkdownContent corporate = claForm.getCorporateContent();
		String corperateHtml = github.markdownToHtml(accessToken, corporate.getMarkdown());
		corporate.setHtml(corperateHtml);

		ContributorLicenseAgreement cla = new ContributorLicenseAgreement();
		cla.setCorporateContent(claForm.getCorporateContent());
		cla.setDescription(claForm.getDescription());
		cla.setIndividualContent(claForm.getIndividualContent());
		cla.setName(claForm.getName());
		cla.setPrimary(claForm.isPrimary());
		cla.setSupersedingCla(supersedingCla);

		claRepo.save(cla);
		return "redirect:/admin/cla/?success";
	}

	@RequestMapping(value = "/admin/cla/{claId}", method = RequestMethod.DELETE)
	public String delete(@AuthenticationPrincipal User user, @PathVariable long claId) {
		claRepo.delete(claId);
		return "redirect:/admin/cla/?success";
	}
}
