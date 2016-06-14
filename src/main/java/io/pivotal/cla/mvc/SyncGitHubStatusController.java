package io.pivotal.cla.mvc;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.UserRepository;
import io.pivotal.cla.service.ClaPullRequestStatusRequest;
import io.pivotal.cla.service.ClaService;
import io.pivotal.cla.service.github.GitHubApi;

@Controller
public class SyncGitHubStatusController {
	@Autowired
	ClaService claService;
	@Autowired
	GitHubApi github;
	@Autowired
	UserRepository users;

	@RequestMapping(value = "/sync/{claName}", method = RequestMethod.GET)
	public String synchForm(@ModelAttribute ClaRequest claRequest) throws Exception {
		return "sync/form";
	}

	@RequestMapping(value = "/sync/{claName}", method = RequestMethod.POST)
	public String synch(@AuthenticationPrincipal User currentUser, @ModelAttribute ClaRequest claRequest, RedirectAttributes redirect) throws Exception {

		Set<String> verifiedEmails = github.getVerifiedEmails(currentUser.getAccessToken());
		if(!currentUser.getEmails().containsAll(verifiedEmails)) {
			currentUser.setEmails(verifiedEmails);
			users.save(currentUser);
		}

		ClaPullRequestStatusRequest updatePullRequest = claRequest.createUpdatePullRequestStatus(currentUser.getGitHubLogin());
		if(updatePullRequest != null) {
			claService.savePullRequestStatus(updatePullRequest);
		}
		String[] repositoryParts = claRequest.getRepositoryId().split("/");
		String repositoryOwner = repositoryParts[0];
		String repositoryName = repositoryParts[1];
		redirect.addAttribute("repositoryOwner", repositoryOwner);
		redirect.addAttribute("repositoryName", repositoryName);
		redirect.addAttribute("pullRequestId", claRequest.getPullRequestId());
		return "redirect:https://github.com/{repositoryOwner}/{repositoryName}/pull/{pullRequestId}";
	}
}
