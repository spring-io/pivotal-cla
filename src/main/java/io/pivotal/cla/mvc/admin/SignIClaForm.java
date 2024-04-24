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
package io.pivotal.cla.mvc.admin;

import io.pivotal.cla.mvc.ClaRequest;
import org.hibernate.validator.constraints.NotEmpty;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public class SignIClaForm extends ClaRequest {
	@NotEmpty(message = "This is required")
	private Long claId;
	@NotEmpty(message = "This is required")
	private String name;
	@NotEmpty(message = "This is required")
	private String email;
	private String gitHubLogin;
	private String mailingAddress;
	private String country;
	private String telephone;

	public SignIClaForm() {
	}

	public Long getClaId() {
		return this.claId;
	}

	public String getName() {
		return this.name;
	}

	public String getEmail() {
		return this.email;
	}

	public String getMailingAddress() {
		return this.mailingAddress;
	}

	public String getCountry() {
		return this.country;
	}

	public String getTelephone() {
		return this.telephone;
	}


	public void setClaId(final Long claId) {
		this.claId = claId;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public void setMailingAddress(final String mailingAddress) {
		this.mailingAddress = mailingAddress;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	public void setTelephone(final String telephone) {
		this.telephone = telephone;
	}

	public String getGitHubLogin() {
		return gitHubLogin;
	}

	public void setGitHubLogin(String gitHubLogin) {
		this.gitHubLogin = gitHubLogin;
	}
}
