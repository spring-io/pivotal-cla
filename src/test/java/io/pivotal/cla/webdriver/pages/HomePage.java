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

public class HomePage extends BasePage {
	@FindBy(id="pivotal-cla-link")
	WebElement pivotalClaLink;

	public HomePage(WebDriver driver) {
		super(driver);
	}


	public <T extends BasePage> T pivotalCla(Class<T> page) {
		pivotalClaLink.click();
		return PageFactory.initElements(getDriver(), page);
	}

	public void assertAt() {
		assertThat(getDriver().getTitle()).endsWith("- Home");
	}

	public static HomePage go(WebDriver driver) {
		get(driver, "/");
		return PageFactory.initElements(driver, HomePage.class);
	}
}