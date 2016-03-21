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

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.validation.Valid;

import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.pivotal.cla.data.AccessToken;
import io.pivotal.cla.data.ContributorLicenseAgreeement;
import io.pivotal.cla.data.IndividualSignature;
import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.AccessTokenRepository;
import io.pivotal.cla.data.repository.ContributorLicenseAgreementRepository;
import io.pivotal.cla.data.repository.IndividualSignatureRepository;
import io.pivotal.cla.service.CommitStatus;
import io.pivotal.cla.service.GitHubService;

@Controller
public class IclaController {
	@Autowired
	ContributorLicenseAgreementRepository clas;
	@Autowired
	IndividualSignatureRepository individual;
	@Autowired
	GitHubService github;
	@Autowired
	AccessTokenRepository tokenRepo;

	@RequestMapping("/sign/{claName}/icla")
	public String claForm(@AuthenticationPrincipal User user, @PathVariable String claName,
			@RequestParam(required = false) String repositoryId, @RequestParam(required = false) Integer pullRequestId,
			Map<String, Object> model) {
		IndividualSignature signed = individual.findByClaNameAndEmailIn(claName, user.getEmails());
		ContributorLicenseAgreeement cla = signed == null ? clas.findByName(claName) : signed.getCla();
		SignClaForm form = new SignClaForm();
		form.setSigned(signed != null);
		form.setName(user.getName());
		form.setClaId(cla.getId());
		form.setRepositoryId(repositoryId);
		form.setPullRequestId(pullRequestId);

		model.put("signClaForm", form);
		model.put("cla", cla);

		return "cla/icla/sign";
	}

	@RequestMapping(value = "/sign/{claName}/icla", method = RequestMethod.POST)
	public String signCla(@AuthenticationPrincipal User user, @Valid SignClaForm signClaForm, BindingResult result, Map<String, Object> model) throws IOException {
		if(result.hasErrors()) {
			ContributorLicenseAgreeement cla = clas.findOne(signClaForm.getClaId());
			model.put("cla", cla);
			return "cla/icla/sign";
		}
		ContributorLicenseAgreeement cla = clas.findOne(signClaForm.getClaId());
		IndividualSignature signature = new IndividualSignature();
		signature.setCla(cla);
		signature.setCountry(signClaForm.getCountry());
		signature.setEmail(signClaForm.getEmail());
		signature.setMailingAddress(signClaForm.getMailingAddress());
		signature.setDateOfSignature(new Date());
		signature.setTelephone(signClaForm.getTelephone());
		signature.setGithubLogin(user.getGithubLogin());
		individual.save(signature);

		// update github

		String repositoryId = signClaForm.getRepositoryId();
		Integer pullRequestId = signClaForm.getPullRequestId();
		if(repositoryId == null || pullRequestId == null) {
			return "redirect:/sign/" +cla.getName() +"/icla?success";
		}

		AccessToken token = tokenRepo.findOne(repositoryId);
		if (token != null) {
			GitHubClient client = new GitHubClient();
			client.setOAuth2Token(token.getToken());
			RepositoryId id = RepositoryId.createFromId(repositoryId);

			PullRequestService service = new PullRequestService(client);
			PullRequest pullRequest = service.getPullRequest(id, pullRequestId);
			if (pullRequest.getUser().getLogin().equals(user.getGithubLogin())) {
				CommitStatus status = new CommitStatus();
				status.setCla(cla.getName());
				status.setPullRequestId(pullRequest.getNumber());
				status.setRepoId(repositoryId);
				status.setSha(pullRequest.getHead().getSha());
				status.setSuccess(true);
				status.setGithubUsername(user.getGithubLogin());
				github.save(status);
			}
		}

		return "redirect:/sign/" + cla.getName() + "/icla?success&repositoryId=" + repositoryId + "&pullRequestId="
				+ pullRequestId;
	}
}
