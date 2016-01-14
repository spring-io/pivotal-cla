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

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class DashboardPage extends BasePage {
	@FindBy(id="no-signatures")
	WebElement noSignatures;

	@FindBy(css = "table tbody tr")
	List<WebElement> rows;

	public DashboardPage(WebDriver driver) {
		super(driver);
	}

	public void assertNoSignatures() {
		assertThat(noSignatures.isDisplayed()).isTrue();
	}

	public List<Signature> getSignatures() {
		List<Signature> signatures = new ArrayList<>();
		for(WebElement r : rows) {
			List<WebElement> columns = r.findElements(By.tagName("td"));
			signatures.add(new Signature(columns.get(0).getText(), columns.get(1).findElement(By.tagName("a"))));
		}
		return signatures;
	}

	public void assertAt() {
		assertThat(getDriver().getTitle()).endsWith("- Dashboard");
	}

	public static DashboardPage go(WebDriver driver) {
		get(driver, "/dashboard");
		return PageFactory.initElements(driver, DashboardPage.class);
	}

	public class Signature {
		String name;

		WebElement view;

		private Signature(String name, WebElement view) {
			super();
			this.name = name;
			this.view = view;
		}

		public String getName() {
			return name;
		}

		/**
		 * @return the view
		 */
		public SignedPage view() {
			view.click();
			return PageFactory.initElements(getDriver(), SignedPage.class);
		}
	}
}