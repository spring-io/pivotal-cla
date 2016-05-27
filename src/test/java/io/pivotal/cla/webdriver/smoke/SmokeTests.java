package io.pivotal.cla.webdriver.smoke;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.PullRequestMarker;
import org.eclipse.egit.github.core.Reference;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.DataService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import io.pivotal.cla.webdriver.pages.SignClaPage;
import io.pivotal.cla.webdriver.pages.SignIclaPage;
import io.pivotal.cla.webdriver.pages.admin.AdminLinkClaPage;
import io.pivotal.cla.webdriver.pages.github.GitHubLoginPage;
import io.pivotal.cla.webdriver.pages.github.GitHubPullRequestPage;

@Category(io.pivotal.cla.junit.SmokeTests.class)
public class SmokeTests {
	static WebDriver driverForLinkUser;

	static WebDriver driverForSignUser;

	/**
	 * This must be a user that has permission to link a CLA (i.e. has
	 * a @pivotal.io email address associated to it and verified).
	 */
	static User linkUser;

	static User signUser;

	@BeforeClass
	public static void setup() throws Exception {
		linkUser = new User();
		linkUser.setGitHubUsername("pivotal-cla-link");
		linkUser.setGitHubPassword(getPasswordFor("linkUser"));
		linkUser.setGitHubAccessToken(getTokenFor("linkUser"));

		signUser = new User();
		signUser.setGitHubUsername("pivotal-cla-signer");
		signUser.setGitHubPassword(getPasswordFor("signUser"));
		signUser.setGitHubAccessToken(getTokenFor("signUser"));

		createTestRepository(linkUser);
		forkRepositoryFor(linkUser.getGitHubUsername() + "/cla-test", signUser);
		driverForLinkUser = new FirefoxDriver();
		driverForSignUser = new FirefoxDriver();
	}

	private static String getPasswordFor(String user) {
		return System.getProperty("smokeTest." + user + ".password");
	}

	private static String getTokenFor(String user) {
		return System.getProperty("smokeTest." + user + ".token");
	}

	private static void forkRepositoryFor(String toFork, User user) throws IOException {
		GitHubClient client = createClient(user.getGitHubAccessToken());
		RepositoryService repository = new RepositoryService(client);

		RestTemplate rest = new RestTemplate();
		try {
		rest.delete("https://api.github.com/repos/{owner}/{repo}?access_token={token}", user.getGitHubUsername(), "cla-test", user.getGitHubAccessToken());
		}catch(Throwable t) {
			t.printStackTrace();
		}

		repository.forkRepository(RepositoryId.createFromId(toFork));
	}

	private static void createTestRepository(User user) throws IOException {
		GitHubClient client = createClient(user.getGitHubAccessToken());
		RepositoryService repository = new RepositoryService(client);

		Repository toCreate = new Repository();
		toCreate.setName("cla-test");

		RestTemplate rest = new RestTemplate();
		try {
			rest.delete("https://api.github.com/repos/{owner}/{repo}?access_token={token}", user.getGitHubUsername(), toCreate.getName(), user.getGitHubAccessToken());
		}catch(Throwable t) {
			t.printStackTrace();
		}

		repository.createRepository(toCreate);

		// we need content to allow forking
		Map<String,String> content = new HashMap<>();
		content.put("message", "Initial");
		content.put("content", "bXkgbmV3IGZpbGUgY29udGVudHM=");
		rest.put("https://api.github.com/repos/{owner}/{repo}/contents/README.adoc?access_token={token}", content, user.getGitHubUsername(), toCreate.getName(), user.getGitHubAccessToken());
	}

	/**
	 * @return the HTML link of the Pull Request
	 * @throws InterruptedException
	 */
	private static String createPullRequest(User user, int count) throws IOException, InterruptedException {
		GitHubClient client = createClient(user.getGitHubAccessToken());
		PullRequestService pulls = new PullRequestService(client);

		RestTemplate rest = new RestTemplate();

		// get sha for master
		DataService references = new DataService(client);
		RepositoryId forkRepositoryId = RepositoryId.create(user.getGitHubUsername(), "cla-test");
		Reference forked = references.getReference(forkRepositoryId, "heads/master");

		// create a branch for our Pull Request

		Reference createPullRequestBranch = new Reference();
		createPullRequestBranch.setRef("refs/heads/pull-" + count);
		createPullRequestBranch.setObject(forked.getObject());
		references.createReference(forkRepositoryId, createPullRequestBranch);

		// create a file for our Pull Request
		Map<String,String> content = new HashMap<>();
		content.put("message", "We added some content for "+ count);
		content.put("content", "bXkgbmV3IGZpbGUgY29udGVudHM=");
		content.put("branch", "pull-"+count);
		rest.put("https://api.github.com/repos/{owner}/{repo}/contents/forPullRequest?access_token={token}", content, user.getGitHubUsername(), "cla-test", user.getGitHubAccessToken());

		PullRequest request = new PullRequest();
		request.setTitle("Please merge");
		request.setBody("Please merge");
		PullRequestMarker head = new PullRequestMarker();
		head.setLabel(signUser.getGitHubUsername() + ":pull-" + count);
		request.setHead(head);
		PullRequestMarker base = new PullRequestMarker();
		base.setLabel("master");
		request.setBase(base);

		Thread.sleep(TimeUnit.SECONDS.toMillis(1));
		PullRequest newPull = pulls.createPullRequest(RepositoryId.createFromId(linkUser.getGitHubUsername() + "/" + "cla-test"), request );
		Thread.sleep(TimeUnit.SECONDS.toMillis(1));
		return newPull.getHtmlUrl();
	}

	private static GitHubClient createClient(String token) {
		GitHubClient client = new GitHubClient();
		client.setOAuth2Token(token);
		return client;
	}

	@AfterClass
	public static void cleanup() {
		if(driverForLinkUser != null) {
			driverForLinkUser.close();
		}
		if(driverForSignUser != null) {
			driverForSignUser.close();
		}
	}

	@Test
	public void all() throws Exception {

		AdminLinkClaPage link = AdminLinkClaPage.to(driverForLinkUser);
		GitHubLoginPage login = new GitHubLoginPage(driverForLinkUser);
		link = login.loginForm()
			.username(linkUser.getGitHubUsername())
			.password(linkUser.getGitHubPassword())
			.submit(AdminLinkClaPage.class);
		link.assertAt();

		takeScreenShot(driverForLinkUser, "cla-link");

		link = link.link(linkUser.getGitHubUsername() + "/cla-test", "pivotal", AdminLinkClaPage.class);
		link.assertAt();

		takeScreenShot(driverForLinkUser, "cla-link-success");

		String pullHtmlUrl = createPullRequest(signUser, 1);

		GitHubLoginPage signLogin = GitHubLoginPage.to(driverForSignUser);
		signLogin.loginForm()
			.username(signUser.getGitHubUsername())
			.password(signUser.getGitHubPassword())
			.submit(PullRequestService.class);

		driverForSignUser.get(pullHtmlUrl);

		GitHubPullRequestPage pull = new GitHubPullRequestPage(driverForSignUser);
		pull.assertCommentPleaseSignFor(signUser.getGitHubUsername());
		pull.assertBuildStatusSign();

		takeScreenShot(driverForSignUser, "gh-pull-request-please-sign");

		SignClaPage sign = pull.details();
		sign.assertAt();

		takeScreenShot(driverForSignUser, "cla-signclapage-please-sign");

		SignIclaPage signIcla = sign.signIcla(SignIclaPage.class);
		signIcla.assertAt();

		takeScreenShot(driverForSignUser, "cla-signicla-please-sign");

		signIcla = signIcla.form()
			.name("Big Bird")
			.email(1)
			.mailingAddress("123 Seasame St")
			.telephone("123.456.7890")
			.country("USA")
			.confirm()
			.sign(SignIclaPage.class);

		pull = signIcla.pullRequest();
		pull.assertCommentThankYouFor(signUser.getGitHubUsername());

		takeScreenShot(driverForSignUser, "gh-pull-request-thanks");

		pullHtmlUrl = createPullRequest(signUser, 2);
		driverForSignUser.get(pullHtmlUrl);
		pull = new GitHubPullRequestPage(driverForSignUser);
		pull.assertBuildStatusSuccess();

		takeScreenShot(driverForSignUser, "gh-pull-request-already-signed");
	}

	int screenShotCount = 0;

	private void takeScreenShot(WebDriver driver, String name) throws IOException {
		TakesScreenshot shot = (TakesScreenshot) driver;
		byte[] screenshotAs = shot.getScreenshotAs(OutputType.BYTES);
		String prefix = String.format("%03d", screenShotCount++) + "_";
		File file = new File("build/" + prefix + name + ".png");
		file.getParentFile().mkdirs();
		file.createNewFile();
		FileCopyUtils.copy(screenshotAs, file);

	}
}
