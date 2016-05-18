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

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import io.pivotal.cla.webdriver.pages.BasePage;

public class AdminCreateClaPage extends BasePage {

	public AdminCreateClaPage(WebDriver driver) {
		super(driver);
	}

	public Form form() {
		Form form = new Form();
		PageFactory.initElements(getDriver(), form);
		return form;
	}

	public class Form {
		@FindBy(id = "individualContent.markdown")
		WebElement individualContent;
		@FindBy(id = "corporateContent.markdown")
		WebElement corporateContent;
		WebElement name;
		@FindBy(id = "create-submit")
		WebElement createSubmit;
		WebElement primary1;
		WebElement description;
		WebElement supersedingCla;


		public InputAssert assertPrimary() {
			return assertInput(primary1);
		}

		public InputAssert assertIndividualContent() {
			return assertInput(individualContent);
		}

		public InputAssert assertName() {
			return assertInput(name);
		}

		public InputAssert assertCorporateContent() {
			return assertInput(corporateContent);
		}

		public Form name(String name) {
			this.name.sendKeys(name);
			return this;
		}

		public Form individual(String individual) {
			this.individualContent.sendKeys(individual);
			return this;
		}

		public Form corporate(String corporate) {
			this.corporateContent.sendKeys(corporate);
			return this;
		}

		public Form description(String description) {
			this.description.sendKeys(description);
			return this;
		}

		public Form primary() {
			this.primary1.click();
			return this;
		}

		public Form supersedingCla(long supersedingCla) {
			new Select(this.supersedingCla).selectByValue(String.valueOf(supersedingCla));
			return this;
		}

		public <T extends BasePage> T submit(Class<T> page) {
			this.createSubmit.click();
			return PageFactory.initElements(getDriver(), page);
		}
	}

	public void assertAt() {
		assertThat(getDriver().getTitle()).endsWith("Create CLA");
	}

	public static AdminCreateClaPage to(WebDriver driver) {
		get(driver, "/admin/cla/create");
		return PageFactory.initElements(driver, AdminCreateClaPage.class);
	}
}
