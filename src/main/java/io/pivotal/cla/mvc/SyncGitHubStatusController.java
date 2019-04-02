package io.pivotal.cla.mvc;

import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.UserRepository;
import io.pivotal.cla.service.ClaPullRequestStatusRequest;
import io.pivotal.cla.service.ClaService;
import io.pivotal.cla.service.github.GitHubApi;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.egit.github.core.PullRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.Set;

@Controller
@Slf4j
public class SyncGitHubStatusController {
	@Autowired
	ClaService claService;
	@Autowired
	GitHubApi github;
	@Autowired
	UserRepository users;

	@GetMapping("/sync/{claName}")
	public String syncForm(@ModelAttribute ClaRequest claRequest) throws Exception {
		return "sync/form";
	}

	@PostMapping("/sync/{claName}")
	public String sync(@AuthenticationPrincipal User currentUser, @ModelAttribute ClaRequest claRequest, RedirectAttributes redirect) throws Exception {
		log.debug("Sync for request {}", claRequest);
		Assert.hasText(claRequest.getRepositoryId(), "RepositoryId must not be empty");
		Assert.isTrue(claRequest.getPullRequestId() != null && claRequest.getPullRequestId() > 0,
				"PullRequest Id must be greater 0");

		String[] repositoryParts = claRequest.getRepositoryId().split("/");
		if (repositoryParts.length != 2 || !StringUtils.hasText(repositoryParts[0])
				|| !StringUtils.hasText(repositoryParts[1])) {
			throw new IllegalArgumentException("RepositoryId must be in format 'username/repository'");
		}

		Set<String> verifiedEmails = github.getVerifiedEmails(currentUser.getAccessToken());
		if(!currentUser.getEmails().containsAll(verifiedEmails)) {
			currentUser.setEmails(verifiedEmails);
			users.save(currentUser);
		}

		Optional<PullRequest> optionalPullRequest = claService.findPullRequest(claRequest.getRepositoryId(),
				claRequest.getPullRequestId());

		PullRequest pullRequest = optionalPullRequest
				.orElseThrow(() -> new IllegalArgumentException(String.format(
						"Pull-request %s#%s does not exist",
						claRequest.getRepositoryId(), claRequest.getPullRequestId())));

		log.debug("Got pull request {}", pullRequest);
		ClaPullRequestStatusRequest updatePullRequest = claRequest
				.createUpdatePullRequestStatus(pullRequest.getUser().getLogin());
		updatePullRequest.getCommitStatus().setPullRequestState(pullRequest.getState());

		Set<String> associatedClaNames = claService.findAssociatedClaNames(claRequest.getRepositoryId());

		if (!associatedClaNames.contains(claRequest.getClaName())) {
			throw new IllegalArgumentException(
					String.format("Requested CLA '%s' is not linked to the repository", claRequest.getClaName()));
		}

		if (updatePullRequest != null) {
			updatePullRequest.getCommitStatus().setAdmin(currentUser.isAdmin());
			claService.savePullRequestStatus(updatePullRequest);
			log.debug("Updating Pull Request ");
		}

		String repositoryOwner = repositoryParts[0];
		String repositoryName = repositoryParts[1];

		redirect.addAttribute("repositoryOwner", repositoryOwner);
		redirect.addAttribute("repositoryName", repositoryName);
		redirect.addAttribute("pullRequestId", claRequest.getPullRequestId());
		return "redirect:https://github.com/{repositoryOwner}/{repositoryName}/pull/{pullRequestId}";
	}
}
