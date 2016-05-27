package io.pivotal.cla.webdriver.pages.github;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GitHubAuthorizePage {
	final WebDriver driver;

	public void authorizeIfNecessary() {
		if(isAt()) {
			WebElement authorize = driver.findElement(By.cssSelector("button[name=authorize]"));
			authorize.click();
		}
	}

	private boolean isAt() {
		String title = driver.getTitle();
		String url = driver.getCurrentUrl();
		return url.startsWith("https://github.com/") && title.contains("Authorize");
	}
}
