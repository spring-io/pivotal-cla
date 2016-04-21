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

	public AdminCreateClaPage(WebDriver driver) {
		super(driver);
	}

	public <T extends BasePage> T create(String name, String individual, String corporate, String description, Class<T> page) {
		return create(name, individual, corporate, description, false, page);
	}

	public <T extends BasePage> T create(String name, String individual, String corporate, Class<T> page) {
		return create(name, individual, corporate, null, false, null, page);
	}

	public <T extends BasePage> T create(String name, String individual, String corporate, boolean primary, Class<T> page) {
		return create(name, individual, corporate, null, primary, page);
	}
	public <T extends BasePage> T create(String name, String individual, String corporate, Long supersedingCla, Class<T> page) {
		return create(name, individual, corporate, null, false, supersedingCla, page);
	}

	public <T extends BasePage> T create(String name, String individual, String corporate, String description, boolean primary, Class<T> page) {
		return create(name, individual, corporate, description, primary, null, page);
	}

	public <T extends BasePage> T create(String name, String individual, String corporate, String description, boolean primary, Long supersedingCla, Class<T> page) {
		this.name.sendKeys(name);
		this.individualContent.sendKeys(individual);
		this.corporateContent.sendKeys(corporate);
		if(primary) {
			this.primary1.click();
		}
		if(description != null) {
			this.description.sendKeys(description);
		}
		if(supersedingCla != null) {
			new Select(this.supersedingCla).selectByValue(String.valueOf(supersedingCla));
		}
		this.createSubmit.click();
		return PageFactory.initElements(getDriver(), page);
	}

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

	public void assertAt() {
		assertThat(getDriver().getTitle()).endsWith("Create CLA");
	}

	public static AdminCreateClaPage to(WebDriver driver) {
		get(driver, "/admin/cla/create");
		return PageFactory.initElements(driver, AdminCreateClaPage.class);
	}
}
