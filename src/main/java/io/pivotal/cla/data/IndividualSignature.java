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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity
public class IndividualSignature {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	private ContributorLicenseAgreeement cla;

	@Version
	private Date dateOfSignature;

	private String name;

	private String email;

	private String mailingAddress;

	private String country;

	private String telephone;

	private String githubLogin;

	/**
	 * @return the githubLogin
	 */
	public String getGithubLogin() {
		return githubLogin;
	}

	/**
	 * @param githubLogin
	 *            the githubLogin to set
	 */
	public void setGithubLogin(String githubLogin) {
		this.githubLogin = githubLogin;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the cla
	 */
	public ContributorLicenseAgreeement getCla() {
		return cla;
	}

	/**
	 * @param cla
	 *            the cla to set
	 */
	public void setCla(ContributorLicenseAgreeement cla) {
		this.cla = cla;
	}

	/**
	 * @return the dateOfSignature
	 */
	public Date getDateOfSignature() {
		return dateOfSignature;
	}

	/**
	 * @param dateOfSignature
	 *            the dateOfSignature to set
	 */
	public void setDateOfSignature(Date dateOfSignature) {
		this.dateOfSignature = dateOfSignature;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the mailingAddress
	 */
	public String getMailingAddress() {
		return mailingAddress;
	}

	/**
	 * @param mailingAddress
	 *            the mailingAddress to set
	 */
	public void setMailingAddress(String mailingAddress) {
		this.mailingAddress = mailingAddress;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the telephone
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * @param telephone
	 *            the telephone to set
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
}
