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

public class SignClaPage extends BasePage {
	@FindBy(id = "learn-more")
	WebElement learnMoreLink;
	@FindBy(id = "icla-link")
	WebElement iclaLink;
	@FindBy(id = "ccla-link")
	WebElement cclaLink;

	WebElement signed;

	public SignClaPage(WebDriver driver) {
		super(driver);
	}

	public void assertSigned() {
		assertThat(signed.getText()).isNotEmpty();
	}

	public AboutPage learnMore() {
		learnMoreLink.click();
		return PageFactory.initElements(getDriver(), AboutPage.class);
	}

	public <T extends BasePage> T signIcla(Class<T> page) {
		iclaLink.click();
		return (T) PageFactory.initElements(getDriver(), page);
	}

	public void assertClaLinks(String claName, String legacy) {

		if (legacy == null) {
			assertThat(getIclaHref()).isEqualTo("http://localhost/sign/" + claName + "/icla");
			assertThat(getCclaHref()).isEqualTo("http://localhost/sign/" + claName + "/ccla");
		} else {
			assertThat(getIclaHref()).isEqualTo("http://localhost/sign/" + claName + "/icla?legacy=" + legacy);
			assertThat(getCclaHref()).isEqualTo("http://localhost/sign/" + claName + "/ccla?legacy=" + legacy);
		}
	}

	private String getCclaHref() {
		return cclaLink.getAttribute("href");
	}

	private String getIclaHref() {
		return iclaLink.getAttribute("href");
	}

	public <T extends BasePage> T signCcla(Class<T> page) {
		cclaLink.click();
		return (T) PageFactory.initElements(getDriver(), page);
	}

	public void assertAt() {
		assertThat(getDriver().getTitle()).endsWith("- Home");
	}

	public void assertLogoutSuccess() {
		assertThat(getMessage()).isEqualTo("You have been signed out.");
	}

	public static SignClaPage go(WebDriver driver, String claName) {
		get(driver, "/sign/" + claName);
		return PageFactory.initElements(driver, SignClaPage.class);
	}

	public static SignClaPage go(WebDriver driver, String claName, String legacy) {
		get(driver, "/sign/" + claName + "?legacy=" + legacy);
		return PageFactory.initElements(driver, SignClaPage.class);
	}
}