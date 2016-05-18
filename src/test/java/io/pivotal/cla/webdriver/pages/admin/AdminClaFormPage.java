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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.springframework.util.StringUtils;

import io.pivotal.cla.webdriver.pages.BasePage;

public class AdminClaFormPage extends BasePage {

	public AdminClaFormPage(WebDriver driver) {
		super(driver);
	}

	public ClaForm form() {
		ClaForm form = new ClaForm();
		PageFactory.initElements(getDriver(), form);
		return form;
	}

	public class ClaForm {
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

		public ClaForm name(String name) {
			this.name.clear();
			this.name.sendKeys(name);
			return this;
		}

		public ClaForm individual(String individual) {
			this.individualContent.clear();
			this.individualContent.sendKeys(individual);
			return this;
		}

		public ClaForm corporate(String corporate) {
			this.corporateContent.clear();
			this.corporateContent.sendKeys(corporate);
			return this;
		}

		public ClaForm description(String description) {
			this.description.clear();
			this.description.sendKeys(description);
			return this;
		}

		public ClaForm primary() {
			this.primary1.click();
			return this;
		}

		public ClaForm supersedingCla(long supersedingCla) {
			new Select(this.supersedingCla).selectByValue(String.valueOf(supersedingCla));
			return this;
		}

		public <T extends BasePage> T submit(Class<T> page) {
			this.createSubmit.click();
			return PageFactory.initElements(getDriver(), page);
		}
	}
}
