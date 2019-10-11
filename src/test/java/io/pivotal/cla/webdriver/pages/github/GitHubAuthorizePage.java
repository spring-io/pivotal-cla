package io.pivotal.cla.webdriver.pages.github;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GitHubAuthorizePage {
	final WebDriver driver;

	public GitHubAuthorizePage(WebDriver driver) {
		this.driver = driver;
	}

	public void authorizeIfNecessary() {
		if(isAt()) {
			By authorizeButtonSelector = By.cssSelector("button[name=authorize]");
			WebElement authorize = driver.findElement(authorizeButtonSelector);
			waitToBeClickable(authorizeButtonSelector);
			authorize.click();
		}
	}

	private void waitToBeClickable(By selector) {
		WebDriverWait wait = new WebDriverWait(driver, 60);
		wait.until(ExpectedConditions.elementToBeClickable(selector));
	}

	private boolean isAt() {
		String title = driver.getTitle();
		String url = driver.getCurrentUrl();
		return url.startsWith("https://github.com/") && title.contains("Authorize");
	}
}
