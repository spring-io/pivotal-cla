/*
 * Copyright 2002-2017 the original author or authors.
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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class ViewCclaPage extends BasePage {
	@FindBy(id = "corporate-cla")
	WebElement corporateCla;

	public ViewCclaPage(WebDriver driver) {
		super(driver);
	}

	public String getCorporate() {
		return this.corporateCla.getText();
	}

	public void assertAt() {
		assertThat(getDriver().getTitle()).endsWith("- View CCLA");
	}

	public static String url(String cla) {
		return "/view/"+ cla + "/ccla";
	}

	public static ViewCclaPage go(WebDriver driver, String cla) {
		get(driver, url(cla));
		return PageFactory.initElements(driver, ViewCclaPage.class);
	}
}