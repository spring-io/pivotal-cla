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
package io.pivotal.cla.webdriver.pages.admin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import io.pivotal.cla.webdriver.pages.BasePage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

public class AdminListClasPage extends BasePage {

	private WebElement create;

	private List<Row> rows;

	public AdminListClasPage(WebDriver driver) {
		super(driver);
		List<WebElement> rowElements = driver.findElements(By.cssSelector("#clas tr"));
		rows = rowElements.stream().map( r-> {
			List<WebElement> cols = r.findElements(By.cssSelector("td"));
			if(cols.isEmpty()) {
				return (Row) null;
			}
			return Row.builder()
					.driver(getDriver())
					.name(cols.get(0).getText())
					.description(cols.get(1).getText())
					.edit(cols.get(2).findElement(By.cssSelector("a")))
					.delete(cols.get(3).findElement(By.cssSelector("input[type=\"submit\"]")))
					.build();
		})
		.filter( e-> e != null)
		.collect(Collectors.toList());
	}

	public Row row(int index) {
		return rows.get(index);
	}

	public AdminCreateClaPage createCla() {
		create.click();
		return PageFactory.initElements(getDriver(), AdminCreateClaPage.class);
	}

	public void assertAt() {
		assertThat(getDriver().getTitle()).endsWith("Manage CLAs");
	}

	public static AdminListClasPage go(WebDriver driver) {
		get(driver, "/admin/cla/");
		return PageFactory.initElements(driver, AdminListClasPage.class);
	}

	@Data
	@Builder
	public static class Row {
		final String name;
		final String description;
		@Getter(AccessLevel.PRIVATE)
		final WebElement delete;
		@Getter(AccessLevel.PRIVATE)
		final WebElement edit;
		@Getter(AccessLevel.PRIVATE)
		final WebDriver driver;

		public AdminEditClaPage edit() {
			edit.click();
			return PageFactory.initElements(getDriver(), AdminEditClaPage.class);
		}

		public AdminListClasPage delete() {
			delete.click();
			return PageFactory.initElements(getDriver(), AdminListClasPage.class);
		}
	}
}
