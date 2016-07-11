package io.pivotal.cla.webdriver.pages.github;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

import io.pivotal.cla.webdriver.pages.SignClaPage;

/**
 * Represents a Pull Request Page. For example:
 *
 * https://github.com/pivotal-cla/cla-test/pull/1
 *
 * @author Rob Winch
 *
 */
public class GitHubPullRequestPage {
	final WebDriver driver;

	public GitHubPullRequestPage(WebDriver driver) {
		super();
		this.driver = driver;
	}

	public void assertCommentPleaseSignFor(String gitHubUsername) {
		String text = "@" + gitHubUsername + " Please sign the Contributor License Agreement! Click here to manually synchronize the status of this Pull Request. See the FAQ for frequently asked questions.";

		waitForText(text);
		assertThat(getBodyText(driver)).contains(text);
	}

	public void assertCommentThankYouFor(String gitHubUsername) {
		String text = "@" + gitHubUsername + " Thank you for signing the Contributor License Agreement!";

		waitForText(text);
		assertThat(getBodyText(driver)).contains(text);
	}

	public void assertBuildStatusSign() {
		String text = "Details ci/pivotal-cla — Please sign the Contributor License Agreement!";

		waitForText(text);
		assertThat(getBodyText(driver)).contains(text);
	}

	public void assertBuildStatusSuccess() {
		String text = "Details ci/pivotal-cla — Thank you for signing the Contributor License Agreement!";

		waitForText(text);
		assertThat(getBodyText(driver)).contains(text);
	}

	public SignClaPage details() {
		WebElement details = driver.findElement(By.cssSelector(".build-status-item a.build-status-details"));
		details.click();

		new GitHubAuthorizePage(driver).authorizeIfNecessary();

		return PageFactory.initElements(driver, SignClaPage.class);
	}

	private void waitForText(String text) {
		new WebDriverWait(driver, 60).until(new Predicate<WebDriver>() {
			@Override
			public boolean apply(WebDriver input) {
				String body = getBodyText(input);
				return body.contains(text);
			}
		});
	}

	private String getBodyText(WebDriver driver) {
		WebElement body = driver.findElement(By.tagName("body"));
		return body.getText().replaceAll("\n", " ");
	}

}
