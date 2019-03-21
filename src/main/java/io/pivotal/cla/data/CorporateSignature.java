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
package io.pivotal.cla.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import lombok.Data;

@Entity
@Data
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

}