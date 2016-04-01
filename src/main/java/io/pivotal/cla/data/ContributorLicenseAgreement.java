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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString(exclude = {"individualContent","corporateContent"})
public class ContributorLicenseAgreement {

	@Id
	@GeneratedValue
	private Long id;

	@NotEmpty(message = "This is required")
	private String name;

	/**
	 * Allows differentiating agreements from one another. This is currently
	 * only able to be filled out via sql import.
	 */
	private String description;

	/**
	 * Allows defining if the is the primary agreement. There may be additional
	 * agreements that have the same name, but are older versions of the
	 * agreement, variations of the agreement for different companies, etc.
	 */
	@Column(name = "primary_cla")
	private boolean primary;

	/**
	 * The {@link ContributorLicenseAgreement} that replaces this
	 * {@link ContributorLicenseAgreement}. If this is not signed, we check to
	 * see if {@link #getSupersedingCla()} is signed. If neither are signed,
	 * then the user signs {@link #getSupersedingCla()}.
	 */
	@OneToOne
	private ContributorLicenseAgreement supersedingCla;

	// TODO java 8?
	@Version
	private Date created = new Date();

	@Lob
	@NotNull(message = "This is required")
	@Valid
	@AttributeOverrides({ @AttributeOverride(name = "markdown", column = @Column(name = "individual_markdown") ),
			@AttributeOverride(name = "html", column = @Column(name = "individual_html") ) })
	private MarkdownContent individualContent;

	@Lob
	@Valid
	@NotNull(message = "This is required")
	@AttributeOverrides({ @AttributeOverride(name = "markdown", column = @Column(name = "corporate_markdown") ),
			@AttributeOverride(name = "html", column = @Column(name = "corporate_html") ) })
	private MarkdownContent corporateContent;

}