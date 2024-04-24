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

public class SignCclaPage extends BasePage {
	@FindBy(id = "corporate-cla")
	WebElement corporateCla;

	@FindBy(id = "breadcrumb-cla-link")
	WebElement claLink;

	@FindBy(id = "success")
	WebElement success;
	@FindBy(id = "pull-request")
	WebElement pullRequest;

	public SignCclaPage(WebDriver driver) {
		super(driver);
	}

	public String getCorporate() {
		return corporateCla.getText();
	}

	public void assertClaLink(String claName) {
		assertThat(claLink.getAttribute("href")).endsWith("/sign/"+claName);
	}

	public void assertPullRequestLink(String repositoryId, int pullRequestId) {
		String url = "https://github.com/" + repositoryId + "/pull/" + pullRequestId;
		assertThat(pullRequest.getAttribute("href")).isEqualTo(url);
	}

	public boolean isSigned() {
		try {
			return success.getText() != null;
		} catch(NoSuchElementException missing) {
			return false;
		}
	}

	public Form form() {
		Form form = new Form();
		PageFactory.initElements(getDriver(), form);
		return form;
	}

	public void assertAt() {
		assertThat(getDriver().getTitle()).endsWith("- Sign CCLA");
	}

	public static String url(String cla) {
		return "/sign/"+ cla + "/ccla";
	}

	public static SignCclaPage go(WebDriver driver, String cla) {
		get(driver, url(cla));
		return PageFactory.initElements(driver, SignCclaPage.class);
	}

	public static SignCclaPage go(WebDriver driver, String cla, String repositoryId, long pullRequestId) {
		get(driver, url(cla) + "?repositoryId="+repositoryId+"&pullRequestId="+pullRequestId);
		return PageFactory.initElements(driver, SignCclaPage.class);
	}

	public class Form {
		WebElement sign;

		WebElement name;

		WebElement email;

		WebElement mailingAddress;

		WebElement country;

		WebElement telephone;

		WebElement companyName;

		WebElement gitHubOrganization;

		WebElement title;

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

		public InputAssert assertCompanyName() {
			return assertInput(companyName);
		}

		public Form companyName(String companyName) {
			this.companyName.sendKeys(companyName);
			return this;
		}

		public SelectAssert assertGitHubOrganization() {
			return assertSelect(gitHubOrganization);
		}

		public Form gitHubOrganization(String organization) {
			new Select(this.gitHubOrganization).selectByValue(organization);
			return this;
		}

		public Form title(String title) {
			this.title.sendKeys(title);
			return this;
		}

		public InputAssert assertTitle() {
			return assertInput(title);
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