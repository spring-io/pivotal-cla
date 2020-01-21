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
package io.pivotal.cla.webdriver.pages.admin;

import io.pivotal.cla.webdriver.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class AdminLookupSignaturePage extends BasePage {

	public AdminLookupSignaturePage(WebDriver driver) {
		super(driver);
	}

	public boolean isSignatureFound() {
		try {
			getDriver().findElement(By.className("alert-success"));
			return true;
		} catch (NoSuchElementException fail) {}
		return false;
	}

	public FindSignatureForm findForm() {
		FindSignatureForm form = new FindSignatureForm();
		PageFactory.initElements(getDriver(), form);
		return form;
	}

	public AdminLookupSignaturePage assertAt() {
		assertThat(getDriver().getTitle()).endsWith("Lookup Signature");
		return this;
	}

	public class FindSignatureForm {
		@FindBy(id = "find-submit")
		WebElement submit;

		WebElement claName;

		WebElement gitHubLogin;

		public FindSignatureForm claName(String claName) {
			this.claName.clear();
			this.claName.sendKeys(claName);
			return this;
		}

		public InputAssert assertClaName() {
			return new InputAssert(this.claName);
		}

		public InputAssert assertGitHubLogin() {
			return new InputAssert(this.gitHubLogin);
		}

		public FindSignatureForm gitHubLogin(String gitHubLogin) {
			this.gitHubLogin.clear();
			this.gitHubLogin.sendKeys(gitHubLogin);
			return this;
		}

		public AdminLookupSignaturePage submit() {
			this.submit.click();
			return PageFactory.initElements(getDriver(), AdminLookupSignaturePage.class);
		}
	}

	public static String url() {
		return "/admin/lookup/";
	}

	public static AdminLookupSignaturePage to(WebDriver driver) {
		get(driver, url());
		return PageFactory.initElements(driver, AdminLookupSignaturePage.class);
	}
}
