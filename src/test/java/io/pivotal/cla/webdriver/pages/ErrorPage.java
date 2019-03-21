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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

/**
 * @author Rob Winch
 *
 */
public class ErrorPage extends SignClaPage {
	private WebElement error;

	/**
	 * @param driver
	 */
	public ErrorPage(WebDriver driver) {
		super(driver);
	}

	public void assertAt() {
		assertThat(getDriver().getTitle()).endsWith("- Error");
	}

	public void assertErrorNotFound() {
		assertThat(getError()).isEqualTo("Not Found");
	}

	public void assertErrorForbidden() {
		assertThat(getError()).isEqualTo("Forbidden");
	}

	public String getError() {
		return error.getText();
	}

	public static ErrorPage at(WebDriver driver) {
		return PageFactory.initElements(driver, ErrorPage.class);
	}
}
