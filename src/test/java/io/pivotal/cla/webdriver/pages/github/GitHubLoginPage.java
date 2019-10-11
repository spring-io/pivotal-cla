package io.pivotal.cla.webdriver.pages.github;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class GitHubLoginPage {
	final WebDriver driver;

	public GitHubLoginPage(WebDriver driver) {
		this.driver = driver;
	}

	public LoginForm loginForm() {
		return PageFactory.initElements(driver, LoginForm.class);
	}

	public static GitHubLoginPage to(WebDriver driver) {
		driver.get("https://github.com/login");
		return PageFactory.initElements(driver, GitHubLoginPage.class);
	}

	public static class LoginForm {
		final WebDriver driver;

		@FindBy(id="login_field")
		WebElement username;
		WebElement password;
		@FindBy(css="input[type=submit]")
		WebElement submit;

		public LoginForm(WebDriver driver) {
			this.driver = driver;
		}

		public LoginForm username(String username) {
			this.username.sendKeys(username);
			return this;
		}

		public LoginForm password(String password) {
			this.password.sendKeys(password);
			return this;
		}

		public <T> T submit(Class<T> to) {
			submit.click();
			new GitHubAuthorizePage(driver).authorizeIfNecessary();
			return PageFactory.initElements(driver, to);
		}
	}
}
