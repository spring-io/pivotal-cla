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
package io.pivotal.cla.mvc.github;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import io.pivotal.cla.data.CorporateSignature;
import io.pivotal.cla.data.IndividualSignature;
import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.AccessTokenRepository;
import io.pivotal.cla.data.repository.CorporateSignatureRepository;
import io.pivotal.cla.data.repository.IndividualSignatureRepository;
import io.pivotal.cla.data.repository.UserRepository;
import io.pivotal.cla.egit.github.core.event.RepositoryPullRequestPayload;
import io.pivotal.cla.mvc.util.UrlBuilder;
import io.pivotal.cla.service.CommitStatus;
import io.pivotal.cla.service.GitHubService;

@RestController
public class GithubHooksController {

	@Autowired
	AccessTokenRepository tokenRepo;

	@Autowired
	UserRepository userRepo;

	@Autowired
	IndividualSignatureRepository individualRepo;

	@Autowired
	CorporateSignatureRepository corporate;

	@Autowired
	GitHubService github;

	@RequestMapping(value = "/github/hooks/pull_request/{cla}", headers = "X-GitHub-Event=ping")
	public String pullRequestPing() {
		return "SUCCESS";
	}

	@RequestMapping("/github/hooks/pull_request/{cla}")
	public String pullRequest(HttpServletRequest request, @RequestBody String body, @PathVariable String cla) throws Exception {
		Gson gson = GsonUtils.createGson();
		RepositoryPullRequestPayload pullRequestPayload = gson.fromJson(body, RepositoryPullRequestPayload.class);

		Repository repository = pullRequestPayload.getRepository();
		RepositoryId repoId = RepositoryId.createFromId(repository.getOwner().getLogin() + "/" + repository.getName());

		PullRequest pullRequest = pullRequestPayload.getPullRequest();
		String login = pullRequest.getUser().getLogin();

		User user = userRepo.findOne(login);

		IndividualSignature signedIndividual = user == null ? null
				: individualRepo.findByClaNameAndEmailIn(cla, user.getEmails());

		boolean success = signedIndividual != null;
		if(!success) {
			List<String> organizations = github.getOrganizations(login);
			CorporateSignature corporateSignature = corporate.findByClaNameAndOrganizationIn(cla, organizations);
			success = corporateSignature != null;
		}

		CommitStatus status = new CommitStatus();
		status.setGithubUsername(login);
		status.setPullRequestId(pullRequest.getNumber());
		status.setCla(cla);
		status.setRepoId(repoId.generateId());
		status.setSha(pullRequest.getHead().getSha());
		status.setSuccess(success);
		String url = UrlBuilder
			.fromRequest(request)
			.path("/sign/"+cla)
			.param("repositoryId", status.getRepoId())
			.param("pullRequestId", String.valueOf(status.getPullRequestId()))
			.build();
		status.setUrl(url);

		github.save(status);

		return "FAIL";
	}
}
