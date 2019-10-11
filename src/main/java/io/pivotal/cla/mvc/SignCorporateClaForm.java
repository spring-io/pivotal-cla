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
package io.pivotal.cla.mvc;

import java.util.List;
import org.hibernate.validator.constraints.NotEmpty;

public class SignCorporateClaForm extends SignClaForm {
	@NotEmpty(message = "This is required")
	private String companyName;
	@NotEmpty(message = "This is required")
	private String gitHubOrganization;
	@NotEmpty(message = "This is required")
	private String title;
	@NotEmpty(message = "This is required")
	private String mailingAddress;
	@NotEmpty(message = "This is required")
	private String country;
	@NotEmpty(message = "This is required")
	private String telephone;
	private List<String> gitHubOrganizations;

	public SignCorporateClaForm() {
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public String getGitHubOrganization() {
		return this.gitHubOrganization;
	}

	public String getTitle() {
		return this.title;
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

	public List<String> getGitHubOrganizations() {
		return this.gitHubOrganizations;
	}

	public void setCompanyName(final String companyName) {
		this.companyName = companyName;
	}

	public void setGitHubOrganization(final String gitHubOrganization) {
		this.gitHubOrganization = gitHubOrganization;
	}

	public void setTitle(final String title) {
		this.title = title;
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

	public void setGitHubOrganizations(final List<String> gitHubOrganizations) {
		this.gitHubOrganizations = gitHubOrganizations;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof SignCorporateClaForm)) return false;
		final SignCorporateClaForm other = (SignCorporateClaForm) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		if (!super.equals(o)) return false;
		final java.lang.Object this$companyName = this.getCompanyName();
		final java.lang.Object other$companyName = other.getCompanyName();
		if (this$companyName == null ? other$companyName != null : !this$companyName.equals(other$companyName)) return false;
		final java.lang.Object this$gitHubOrganization = this.getGitHubOrganization();
		final java.lang.Object other$gitHubOrganization = other.getGitHubOrganization();
		if (this$gitHubOrganization == null ? other$gitHubOrganization != null : !this$gitHubOrganization.equals(other$gitHubOrganization)) return false;
		final java.lang.Object this$title = this.getTitle();
		final java.lang.Object other$title = other.getTitle();
		if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
		final java.lang.Object this$mailingAddress = this.getMailingAddress();
		final java.lang.Object other$mailingAddress = other.getMailingAddress();
		if (this$mailingAddress == null ? other$mailingAddress != null : !this$mailingAddress.equals(other$mailingAddress)) return false;
		final java.lang.Object this$country = this.getCountry();
		final java.lang.Object other$country = other.getCountry();
		if (this$country == null ? other$country != null : !this$country.equals(other$country)) return false;
		final java.lang.Object this$telephone = this.getTelephone();
		final java.lang.Object other$telephone = other.getTelephone();
		if (this$telephone == null ? other$telephone != null : !this$telephone.equals(other$telephone)) return false;
		final java.lang.Object this$gitHubOrganizations = this.getGitHubOrganizations();
		final java.lang.Object other$gitHubOrganizations = other.getGitHubOrganizations();
		if (this$gitHubOrganizations == null ? other$gitHubOrganizations != null : !this$gitHubOrganizations.equals(other$gitHubOrganizations)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof SignCorporateClaForm;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		result = result * PRIME + super.hashCode();
		final java.lang.Object $companyName = this.getCompanyName();
		result = result * PRIME + ($companyName == null ? 43 : $companyName.hashCode());
		final java.lang.Object $gitHubOrganization = this.getGitHubOrganization();
		result = result * PRIME + ($gitHubOrganization == null ? 43 : $gitHubOrganization.hashCode());
		final java.lang.Object $title = this.getTitle();
		result = result * PRIME + ($title == null ? 43 : $title.hashCode());
		final java.lang.Object $mailingAddress = this.getMailingAddress();
		result = result * PRIME + ($mailingAddress == null ? 43 : $mailingAddress.hashCode());
		final java.lang.Object $country = this.getCountry();
		result = result * PRIME + ($country == null ? 43 : $country.hashCode());
		final java.lang.Object $telephone = this.getTelephone();
		result = result * PRIME + ($telephone == null ? 43 : $telephone.hashCode());
		final java.lang.Object $gitHubOrganizations = this.getGitHubOrganizations();
		result = result * PRIME + ($gitHubOrganizations == null ? 43 : $gitHubOrganizations.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "SignCorporateClaForm(super=" + super.toString() + ", companyName=" + this.getCompanyName() + ", gitHubOrganization=" + this.getGitHubOrganization() + ", title=" + this.getTitle() + ", mailingAddress=" + this.getMailingAddress() + ", country=" + this.getCountry() + ", telephone=" + this.getTelephone() + ", gitHubOrganizations=" + this.getGitHubOrganizations() + ")";
	}
}
