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
package io.pivotal.cla.data;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity
public class CorporateSignature {
	@Id
	@GeneratedValue
	private Long id;
	@ManyToOne
	private ContributorLicenseAgreement cla;
	@Version
	private Date dateOfSignature;
	private String name;
	private String email;
	private String mailingAddress;
	private String country;
	private String telephone;
	@Column(name = "github_login")
	private String gitHubLogin;
	private String companyName;
	/**
	 * The GitHub organization that is covered by this signature (null if
	 * emailDomain is non-null)
	 */
	private String gitHubOrganization;
	/**
	 * The email domain that is covered by this signature (null if
	 * gitHubOrganization is non-null)
	 */
	private String emailDomain;
	private String title;

	public CorporateSignature() {
	}

	public Long getId() {
		return this.id;
	}

	public ContributorLicenseAgreement getCla() {
		return this.cla;
	}

	public Date getDateOfSignature() {
		return this.dateOfSignature;
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

	public String getGitHubLogin() {
		return this.gitHubLogin;
	}

	public String getCompanyName() {
		return this.companyName;
	}

	/**
	 * The GitHub organization that is covered by this signature (null if
	 * emailDomain is non-null)
	 */
	public String getGitHubOrganization() {
		return this.gitHubOrganization;
	}

	/**
	 * The email domain that is covered by this signature (null if
	 * gitHubOrganization is non-null)
	 */
	public String getEmailDomain() {
		return this.emailDomain;
	}

	public String getTitle() {
		return this.title;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setCla(final ContributorLicenseAgreement cla) {
		this.cla = cla;
	}

	public void setDateOfSignature(final Date dateOfSignature) {
		this.dateOfSignature = dateOfSignature;
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

	public void setGitHubLogin(final String gitHubLogin) {
		this.gitHubLogin = gitHubLogin;
	}

	public void setCompanyName(final String companyName) {
		this.companyName = companyName;
	}

	/**
	 * The GitHub organization that is covered by this signature (null if
	 * emailDomain is non-null)
	 */
	public void setGitHubOrganization(final String gitHubOrganization) {
		this.gitHubOrganization = gitHubOrganization;
	}

	/**
	 * The email domain that is covered by this signature (null if
	 * gitHubOrganization is non-null)
	 */
	public void setEmailDomain(final String emailDomain) {
		this.emailDomain = emailDomain;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof CorporateSignature)) return false;
		final CorporateSignature other = (CorporateSignature) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$id = this.getId();
		final java.lang.Object other$id = other.getId();
		if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
		final java.lang.Object this$cla = this.getCla();
		final java.lang.Object other$cla = other.getCla();
		if (this$cla == null ? other$cla != null : !this$cla.equals(other$cla)) return false;
		final java.lang.Object this$dateOfSignature = this.getDateOfSignature();
		final java.lang.Object other$dateOfSignature = other.getDateOfSignature();
		if (this$dateOfSignature == null ? other$dateOfSignature != null : !this$dateOfSignature.equals(other$dateOfSignature)) return false;
		final java.lang.Object this$name = this.getName();
		final java.lang.Object other$name = other.getName();
		if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
		final java.lang.Object this$email = this.getEmail();
		final java.lang.Object other$email = other.getEmail();
		if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
		final java.lang.Object this$mailingAddress = this.getMailingAddress();
		final java.lang.Object other$mailingAddress = other.getMailingAddress();
		if (this$mailingAddress == null ? other$mailingAddress != null : !this$mailingAddress.equals(other$mailingAddress)) return false;
		final java.lang.Object this$country = this.getCountry();
		final java.lang.Object other$country = other.getCountry();
		if (this$country == null ? other$country != null : !this$country.equals(other$country)) return false;
		final java.lang.Object this$telephone = this.getTelephone();
		final java.lang.Object other$telephone = other.getTelephone();
		if (this$telephone == null ? other$telephone != null : !this$telephone.equals(other$telephone)) return false;
		final java.lang.Object this$gitHubLogin = this.getGitHubLogin();
		final java.lang.Object other$gitHubLogin = other.getGitHubLogin();
		if (this$gitHubLogin == null ? other$gitHubLogin != null : !this$gitHubLogin.equals(other$gitHubLogin)) return false;
		final java.lang.Object this$companyName = this.getCompanyName();
		final java.lang.Object other$companyName = other.getCompanyName();
		if (this$companyName == null ? other$companyName != null : !this$companyName.equals(other$companyName)) return false;
		final java.lang.Object this$gitHubOrganization = this.getGitHubOrganization();
		final java.lang.Object other$gitHubOrganization = other.getGitHubOrganization();
		if (this$gitHubOrganization == null ? other$gitHubOrganization != null : !this$gitHubOrganization.equals(other$gitHubOrganization)) return false;
		final java.lang.Object this$emailDomain = this.getEmailDomain();
		final java.lang.Object other$emailDomain = other.getEmailDomain();
		if (this$emailDomain == null ? other$emailDomain != null : !this$emailDomain.equals(other$emailDomain)) return false;
		final java.lang.Object this$title = this.getTitle();
		final java.lang.Object other$title = other.getTitle();
		if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof CorporateSignature;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $id = this.getId();
		result = result * PRIME + ($id == null ? 43 : $id.hashCode());
		final java.lang.Object $cla = this.getCla();
		result = result * PRIME + ($cla == null ? 43 : $cla.hashCode());
		final java.lang.Object $dateOfSignature = this.getDateOfSignature();
		result = result * PRIME + ($dateOfSignature == null ? 43 : $dateOfSignature.hashCode());
		final java.lang.Object $name = this.getName();
		result = result * PRIME + ($name == null ? 43 : $name.hashCode());
		final java.lang.Object $email = this.getEmail();
		result = result * PRIME + ($email == null ? 43 : $email.hashCode());
		final java.lang.Object $mailingAddress = this.getMailingAddress();
		result = result * PRIME + ($mailingAddress == null ? 43 : $mailingAddress.hashCode());
		final java.lang.Object $country = this.getCountry();
		result = result * PRIME + ($country == null ? 43 : $country.hashCode());
		final java.lang.Object $telephone = this.getTelephone();
		result = result * PRIME + ($telephone == null ? 43 : $telephone.hashCode());
		final java.lang.Object $gitHubLogin = this.getGitHubLogin();
		result = result * PRIME + ($gitHubLogin == null ? 43 : $gitHubLogin.hashCode());
		final java.lang.Object $companyName = this.getCompanyName();
		result = result * PRIME + ($companyName == null ? 43 : $companyName.hashCode());
		final java.lang.Object $gitHubOrganization = this.getGitHubOrganization();
		result = result * PRIME + ($gitHubOrganization == null ? 43 : $gitHubOrganization.hashCode());
		final java.lang.Object $emailDomain = this.getEmailDomain();
		result = result * PRIME + ($emailDomain == null ? 43 : $emailDomain.hashCode());
		final java.lang.Object $title = this.getTitle();
		result = result * PRIME + ($title == null ? 43 : $title.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "CorporateSignature(id=" + this.getId() + ", cla=" + this.getCla() + ", dateOfSignature=" + this.getDateOfSignature() + ", name=" + this.getName() + ", email=" + this.getEmail() + ", mailingAddress=" + this.getMailingAddress() + ", country=" + this.getCountry() + ", telephone=" + this.getTelephone() + ", gitHubLogin=" + this.getGitHubLogin() + ", companyName=" + this.getCompanyName() + ", gitHubOrganization=" + this.getGitHubOrganization() + ", emailDomain=" + this.getEmailDomain() + ", title=" + this.getTitle() + ")";
	}
}
