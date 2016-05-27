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

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import io.pivotal.cla.webdriver.pages.github.GitHubPullRequestPage;

public class SignIclaPage extends BasePage {
	@FindBy(id = "individual-cla")
	WebElement individualCla;

	@FindBy(id = "success")
	WebElement success;

	@FindBy(id = "breadcrumb-cla-link")
	WebElement claLink;
	@FindBy(id = "pull-request")
	WebElement pullRequest;

	Form form;

	public SignIclaPage(WebDriver driver) {
		super(driver);
	}

	public void assertClaLink(String claName) {
		assertThat(claLink.getAttribute("href")).endsWith("/sign/"+claName);
	}

	public void assertPullRequestLink(String repositoryId, int pullRequestId) {
		String url = "https://github.com/" + repositoryId + "/pull/" + pullRequestId;
		assertThat(pullRequest.getAttribute("href")).isEqualTo(url);
	}

	public GitHubPullRequestPage pullRequest() {
		pullRequest.click();
		return PageFactory.initElements(getDriver(), GitHubPullRequestPage.class);
	}

	public boolean isSigned() {
		try {
			return success.getText() != null;
		} catch(NoSuchElementException missing) {
			return false;
		}
	}

	public String getIndividualCla() {
		return individualCla.getText();
	}

	public Form form() {
		if(form != null) {
			return form;
		}
		form = new Form();
		PageFactory.initElements(getDriver(), form);
		return form;
	}

	public void assertAt() {
		assertThat(getDriver().getTitle()).endsWith("- Sign ICLA");
	}

	public static String url(String cla) {
		return "/sign/"+ cla + "/icla";
	}

	public static SignIclaPage go(WebDriver driver, String cla) {
		get(driver, url(cla));
		return PageFactory.initElements(driver, SignIclaPage.class);
	}

	public static SignIclaPage go(WebDriver driver, String cla, String repositoryId, long pullRequestId) {
		get(driver, url(cla) + "?repositoryId="+repositoryId+"&pullRequestId="+pullRequestId);
		return PageFactory.initElements(driver, SignIclaPage.class);
	}

	public class Form {

		WebElement sign;

		WebElement name;

		WebElement email;

		WebElement mailingAddress;

		WebElement country;

		WebElement telephone;

		WebElement confirm;

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

		public Form email(int index) {
			new Select(this.email).selectByIndex(index);
			return this;
		}

		public InputAssert assertMailingAddress() {
			return assertInput(mailingAddress);
		}

		public Form mailingAddress(String address) {
			this.mailingAddress.sendKeys(address);
			return this;
		}

		public InputAssert assertCountry() {
			return assertInput(country);
		}

		public Form country(String country) {
			this.country.sendKeys(country);
			return this;
		}

		public InputAssert assertTelephone() {
			return assertInput(telephone);
		}

		public Form telephone(String telephone) {
			this.telephone.sendKeys(telephone);
			return this;
		}

		public Form confirm() {
			this.confirm.click();
			return this;
		}

		public CheckboxAssert assertConfirm() {
			return assertCheckbox(confirm);
		}
	}
}