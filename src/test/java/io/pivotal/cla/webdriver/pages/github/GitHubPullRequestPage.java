package io.pivotal.cla.webdriver.pages.github;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

import io.pivotal.cla.webdriver.pages.SignClaPage;

import java.time.Duration;

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
		String text = "@" + gitHubUsername + " Please sign the Contributor License Agreement!";

		waitForText(text);
		assertThat(getBodyText(driver)).contains(text);
	}

	public void assertCommentThankYouFor(String gitHubUsername) {
		String text = "@" + gitHubUsername + " Thank you for signing the Contributor License Agreement!";

		waitForText(text);
		assertThat(getBodyText(driver)).contains(text);
	}

	public void assertBuildStatusSign() {
		String text = "ci/pivotal-cla — Please sign the Contributor License Agreement!";

		waitForText(text);
		assertThat(getBodyText(driver)).contains(text);
	}

	public void assertBuildStatusSuccess() {
		String text = "ci/pivotal-cla — Thank you for signing the Contributor License Agreement!";

		waitForText(text);
		assertThat(getBodyText(driver)).contains(text);
	}

	public SignClaPage details() {
		WebElement details = driver.findElement(By.cssSelector("a.status-actions"));
		details.click();

		new GitHubAuthorizePage(driver).authorizeIfNecessary();

		return PageFactory.initElements(driver, SignClaPage.class);
	}

	private void waitForText(String text) {
		new WebDriverWait(driver, Duration.ofSeconds(60)).until(input -> getBodyText(input).contains(text));
	}

	private String getBodyText(WebDriver driver) {
		WebElement body = driver.findElement(By.tagName("body"));
		return body.getText().replaceAll("\n", " ");
	}

}
