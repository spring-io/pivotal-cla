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

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.pivotal.cla.data.AccessToken;
import io.pivotal.cla.data.User;
import io.pivotal.cla.mvc.util.UrlBuilder;
import io.pivotal.cla.service.MigratePullRequestStatusRequest;
import io.pivotal.cla.service.github.ContributingUrlsResponse;
import io.pivotal.cla.service.github.CreatePullRequestHookRequest;

/**
 * @author Rob Winch
 *
 */
@Controller
public class AdminLinkClaController extends AdminClaController {
	private static final String ACCESS_TOKENS_URL = "https://github.com/settings/applications";

	@RequestMapping("/admin/cla/link")
	public String linkClaForm(@AuthenticationPrincipal User user, Map<String, Object> model) throws Exception {
		model.put("linkClaForm", new LinkClaForm());
		model.put("licenses", findPrimaryClas());
		model.put("accessTokensUrl", ACCESS_TOKENS_URL);
		return "admin/cla/link";
	}

	@ResponseBody
	@RequestMapping("/admin/cla/link/repositories.json")
	public List<String> repositories(@AuthenticationPrincipal User user) throws Exception {
		return gitHub.findRepositoryNamesWithAdminPermission(user.getAccessToken());
	}

	@RequestMapping(value = "/admin/cla/link", method = RequestMethod.POST)
	public String linkCla(@AuthenticationPrincipal User user, HttpServletRequest request, Map<String, Object> model, @Valid LinkClaForm linkClaForm,
			BindingResult result, RedirectAttributes attrs) throws Exception {
		if (result.hasErrors()) {
			model.put("licenses", findPrimaryClas());
			model.put("accessTokensUrl", ACCESS_TOKENS_URL);
			return "admin/cla/link";
		}

		AccessToken accessToken = tokenRepo.findOne(AccessToken.CLA_ACCESS_TOKEN_ID);

		UrlBuilder pullRequestUrlBldr = UrlBuilder.fromRequest(request);
		UrlBuilder signClaUrlBldr = UrlBuilder.fromRequest(request);

		String urlEncodedClaName = URLEncoder.encode(linkClaForm.getClaName(), "UTF-8");
		String pullRequestHookUrl = pullRequestUrlBldr
				.path(UrlBuilder.pullRequestHookCallbackPath(linkClaForm.getClaName()))
				.build();
		String signClaUrl = signClaUrlBldr.path("/sign/" + urlEncodedClaName).build();

		CreatePullRequestHookRequest createPullRequest = new CreatePullRequestHookRequest();
		createPullRequest.setAccessToken(user.getAccessToken());
		List<String> repositoryIds = linkClaForm.getRepositories();
		createPullRequest.setRepositoryIds(repositoryIds);
		createPullRequest.setGitHubEventUrl(pullRequestHookUrl);
		createPullRequest.setSecret(accessToken.getToken());

		ContributingUrlsResponse contributingUrls = gitHub.getContributingUrls(repositoryIds);
		List<String> hookUrls = gitHub.createPullRequestHooks(createPullRequest);
		for(String repositoryId : repositoryIds) {
			AccessToken token = new AccessToken();
			token.setId(repositoryId);
			token.setToken(user.getAccessToken());

			tokenRepo.save(token);
		}

		UpdatePullRequestStatusesForm updatePullRequestStatusesForm = new UpdatePullRequestStatusesForm();
		updatePullRequestStatusesForm.setClaName(linkClaForm.getClaName());
		updatePullRequestStatusesForm.setRepositories(repositoryIds);

		attrs.addFlashAttribute("signClaUrl", signClaUrl);
		attrs.addFlashAttribute("hookUrls", hookUrls);
		attrs.addFlashAttribute("success", true);
		attrs.addFlashAttribute("editContributingAdocUrls", contributingUrls.getAsciidoc());
		attrs.addFlashAttribute("editContributingMdUrls", contributingUrls.getMarkdown());
		attrs.addFlashAttribute("updatePullRequestStatusesForm", updatePullRequestStatusesForm);

		return "redirect:/admin/cla/link";
	}

	@RequestMapping(value = "/admin/cla/link/migrate", method = RequestMethod.POST)
	public String updatePullRequestStatuses(@AuthenticationPrincipal User user, @ModelAttribute UpdatePullRequestStatusesForm updatePullRequestStatusesForm, HttpServletRequest request) throws Exception {
		String claName = updatePullRequestStatusesForm.getClaName();
		String urlEncodedClaName = URLEncoder.encode(claName, "UTF-8");


		UrlBuilder signClaUrlBldr = UrlBuilder.fromRequest(request);
		String signClaUrl = signClaUrlBldr.path("/sign/" + urlEncodedClaName).build();

		UrlBuilder aboutUrlBldr = UrlBuilder.fromRequest(request);
		String aboutUrl = aboutUrlBldr.path("/about").build();

		UrlBuilder baseSyncUrlBldr = UrlBuilder.fromRequest(request);
		String baseSyncUrl = baseSyncUrlBldr.path("/sync/" + urlEncodedClaName).build();

		MigratePullRequestStatusRequest migratePullRequests = MigratePullRequestStatusRequest.builder()
							.accessToken(user.getAccessToken())
							.commitStatusUrl(signClaUrl)
							.repositoryIds(updatePullRequestStatusesForm.getRepositories())
							.faqUrl(aboutUrl)
							.baseSyncUrl(baseSyncUrl)
							.build();

		claService.migratePullRequestStatus(updatePullRequestStatusesForm.getClaName(), migratePullRequests);
		return "redirect:/admin/cla/link";
	}

}
