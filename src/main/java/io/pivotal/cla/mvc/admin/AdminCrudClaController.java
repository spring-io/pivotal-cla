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

import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.MarkdownContent;
import io.pivotal.cla.data.User;
import io.pivotal.cla.mvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Map;

/**
 * @author Rob Winch
 *
 */
@Controller
@PreAuthorize("hasRole('CLA_AUTHOR')")
public class AdminCrudClaController extends AdminClaController {

	@GetMapping("/admin/cla/")
	public String listClas(Map<String, Object> model) throws Exception {
		model.put("clas", findAllClas());
		return "admin/cla/index";
	}

	@GetMapping("/admin/cla/create")
	public String createClaForm(Map<String, Object> model) throws Exception {
		return claForm(new ClaForm(), model);
	}

	@GetMapping("/admin/cla/{claId}/edit")
	public String editClaForm(@PathVariable long claId, Map<String, Object> model) throws Exception {
		ContributorLicenseAgreement cla = claRepo.findOne(claId);
		return claForm(cla, model);
	}

	private String claForm(Object claForm, Map<String, Object> model) {
		if(claForm == null) {
			throw new ResourceNotFoundException();
		}
		Iterable<ContributorLicenseAgreement> clas = claRepo.findAll();
		model.put("licenses", clas);
		model.put("claForm", claForm);
		return "admin/cla/form";
	}

	@PostMapping("/admin/cla")
	public String saveCla(@AuthenticationPrincipal User user, @Valid ClaForm claForm, BindingResult result, Map<String, Object> model)
			throws Exception {
		boolean primary = claForm.isPrimary();
		if(primary) {
			ContributorLicenseAgreement existingPrimaryCla = claRepo.findByNameAndPrimaryTrue(claForm.getName());
			Long existingPrimaryClaId = existingPrimaryCla == null ? null : existingPrimaryCla.getId();
			if(existingPrimaryClaId != null && !existingPrimaryClaId.equals(claForm.getId())) {
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
		String individualHtml = gitHub.markdownToHtml(accessToken, individual.getMarkdown());
		individual.setHtml(individualHtml);

		MarkdownContent corporate = claForm.getCorporateContent();
		String corperateHtml = gitHub.markdownToHtml(accessToken, corporate.getMarkdown());
		corporate.setHtml(corperateHtml);

		boolean isCreateNew = claForm.getId() == null;
		ContributorLicenseAgreement cla = isCreateNew ? new ContributorLicenseAgreement() : claRepo.findOne(claForm.getId());
		cla.setCorporateContent(claForm.getCorporateContent());
		cla.setDescription(claForm.getDescription());
		cla.setIndividualContent(claForm.getIndividualContent());
		cla.setName(claForm.getName());
		cla.setPrimary(claForm.isPrimary());
		cla.setSupersedingCla(supersedingCla);

		claRepo.save(cla);
		return "redirect:/admin/cla/?success";
	}

	@DeleteMapping("/admin/cla/{claId}")
	public String delete(@AuthenticationPrincipal User user, @PathVariable long claId) {
		claRepo.delete(claId);
		return "redirect:/admin/cla/?success";
	}
}
