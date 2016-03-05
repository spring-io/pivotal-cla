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
package io.pivotal.cla.webdriver.pages;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class CorporateSignClaPage extends BasePage {
	@FindBy(id = "corporate-cla")
	WebElement corporateCla;


	public CorporateSignClaPage(WebDriver driver) {
		super(driver);
	}

	public String getCorporate() {
		return corporateCla.getText();
	}

	public Form form() {
		Form form = new Form();
		PageFactory.initElements(getDriver(), form);
		return form;
	}

	public void assertAt() {
		assertThat(getDriver().getTitle()).endsWith("- Sign");
	}

	public static CorporateSignClaPage go(WebDriver driver, String cla) {
		get(driver, "/cla/corporate/sign/" + cla);
		return PageFactory.initElements(driver, CorporateSignClaPage.class);
	}

	public static CorporateSignClaPage go(WebDriver driver, String cla, String repositoryId, long pullRequestId) {
		get(driver, "/cla/corporate/sign/" + cla + "?repositoryId="+repositoryId+"&pullRequestId="+pullRequestId);
		return PageFactory.initElements(driver, CorporateSignClaPage.class);
	}

	public class Form {

		WebElement sign;

		WebElement name;

		WebElement email;

		WebElement companyName;

		WebElement organization;

		public <T extends BasePage> T sign(Class<T> page) {
			sign.click();
			return PageFactory.initElements(getDriver(), page);
		}

		public InputAssert assertName() {
			return assertInput(name);
		}

		public Form name(String name) {
			this.name.sendKeys(name);
			return this;
		}

		public SelectAssert assertEmail() {
			return assertSelect(email);
		}

		public Form email(String email) {
			new Select(this.email).selectByValue(email);
			return this;
		}

		public InputAssert assertCompanyName() {
			return assertInput(companyName);
		}

		public Form companyName(String name) {
			this.companyName.sendKeys(name);
			return this;
		}

		public SelectAssert assertOrganization() {
			return assertSelect(organization);
		}

		public Form organization(String organization) {
			new Select(this.organization).selectByValue(organization);
			return this;
		}
	}
}