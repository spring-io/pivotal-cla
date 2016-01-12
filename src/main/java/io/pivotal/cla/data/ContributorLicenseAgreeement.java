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
import javax.persistence.Lob;
import javax.persistence.Version;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class ContributorLicenseAgreeement {

	@Id
	@GeneratedValue
	private Long id;

	@NotEmpty(message = "This is required")
	private String name;

	// TODO java 8?
	@Version
	private Date created = new Date();

	@NotEmpty(message = "This is required")
	@Lob
	private String individualContent;

	@NotEmpty(message = "This is required")
	@Lob
	private String corporateContent;

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
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created
	 *            the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the individualContent
	 */
	public String getIndividualContent() {
		return individualContent;
	}

	/**
	 * @param individualContent
	 *            the individualContent to set
	 */
	public void setIndividualContent(String individualContent) {
		this.individualContent = individualContent;
	}

	/**
	 * @return the corporateContent
	 */
	public String getCorporateContent() {
		return corporateContent;
	}

	/**
	 * @param corporateContent
	 *            the corporateContent to set
	 */
	public void setCorporateContent(String corporateContent) {
		this.corporateContent = corporateContent;
	}
}