/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.pivotal.cla.webdriver.pages;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.pivotal.cla.webdriver.pages.admin.AdminHelpPage;
import io.pivotal.cla.webdriver.pages.admin.AdminLinkClaPage;
import io.pivotal.cla.webdriver.pages.admin.AdminListClasPage;

public abstract class BasePage {
	public static final String WEBDRIVER_BASE_URL = "webdriver.baseUrl";

	WebDriver driver;

	WebElement manage;

	@FindBy(id = "admin-help")
	WebElement adminHelp;

	WebElement link;

	@FindBy(id = "sign-out")
	WebElement signout;

	WebElement profile;

	@FindBy(id = "user-menu")
	WebElement userMenu;

	@FindBy(id = "admin-menu")
	WebElement adminMenu;

	@FindBy(id = "successMessage")
	WebElement successMessage;

	protected BasePage(WebDriver driver) {
		this.driver = driver;
	}

	protected WebDriver getDriver() {
		return driver;
	}

	public static void get(WebDriver driver, String get) {
		String baseUrl = System.getProperty(WEBDRIVER_BASE_URL, "http://localhost");
		driver.get(baseUrl + get);
	}

	protected SelectAssert assertSelect(WebElement input) {
		return new SelectAssert(input);
	}

	protected InputAssert assertInput(WebElement input) {
		return new InputAssert(input);
	}

	protected CheckboxAssert assertCheckbox(WebElement input) {
		return new CheckboxAssert(input);
	}

	public class InputAssert {
		protected WebElement input;

		public InputAssert(WebElement input) {
			this.input = input;
		}

		public void hasValue(String value) {
			assertThat(input.getAttribute("value")).isEqualTo(value);
		}

		public InputAssert hasNoErrors() {
			try {
				hasError("None");
				fail("Expected that " + getInputName() + " had no errors");
			} catch (NoSuchElementException success) {
			}
			return this;
		}

		public void hasRequiredError() {
			hasError("This is required");
		}

		public void hasError(String error) {
			assertThat(driver.findElement(By.id("err-" + getInputName())).getText()).contains(error);
		}

		protected String getInputName() {
			return input.getAttribute("name");
		}

	}

	public class SelectAssert extends InputAssert {

		public SelectAssert(WebElement input) {
			super(input);
		}

		public void hasOptionTexts(String... texts) {
			assertThat(select().getOptions()).extracting("text").contains((Object[])texts);
		}

		@Override
		public void hasValue(String value) {
			Select select = new Select(input);
			assertThat(select.getFirstSelectedOption().getAttribute("value")).isEqualTo(value);
		}

		private Select select() {
			return new Select(input);
		}
	}

	public class CheckboxAssert extends InputAssert {
		public CheckboxAssert(WebElement input) {
			super(input);
		}

		public void assertSelected() {
			assertThat(input.isSelected()).isTrue();
		}

		public void assertNotSelected() {
			assertThat(input.isSelected()).isFalse();
		}
	}

	public void waitUntilAt(Runnable assertAt) {
		new WebDriverWait(getDriver(), 6).until(input -> at(assertAt));
	}

	private boolean at(Runnable assertAt) {
		try {
			assertAt.run();
			return true;
		} catch(AssertionError notAt) {
			return false;
		}
	}

	public void assertManageLink(boolean displayed) {
		assertThat(getDriver().findElements( By.id("manage") ).isEmpty()).isEqualTo(!displayed);
	}

	public AdminListClasPage manage() {
		adminMenu();
		manage.click();
		return PageFactory.initElements(driver, AdminListClasPage.class);
	}

	protected void userMenu() {
		userMenu.click();
		WebDriverWait wait = new WebDriverWait(driver, 5);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(signout.getAttribute("id"))));
	}

	protected void adminMenu() {
		adminMenu.click();
		WebDriverWait wait = new WebDriverWait(driver, 5);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(link.getAttribute("id"))));
	}

	public AdminLinkClaPage link() {
		adminMenu();
		link.click();
		return PageFactory.initElements(driver, AdminLinkClaPage.class);
	}

	public AdminHelpPage adminHelp() {
		adminMenu();
		adminHelp.click();
		return PageFactory.initElements(driver, AdminHelpPage.class);
	}

	public SignClaPage signOut() {
		userMenu();
		signout.click();
		return PageFactory.initElements(driver, SignClaPage.class);
	}

	public ProfilePage profile() {
		userMenu();
		profile.click();
		return PageFactory.initElements(driver, ProfilePage.class);
	}

	protected String getSuccessMessage() {
		return successMessage.getText();
	}
}
