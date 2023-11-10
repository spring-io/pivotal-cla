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

@Entity
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
	@AttributeOverrides({@AttributeOverride(name = "markdown", column = @Column(name = "individual_markdown")), @AttributeOverride(name = "html", column = @Column(name = "individual_html"))})
	private MarkdownContent individualContent;
	@Lob
	@Valid
	@NotNull(message = "This is required")
	@AttributeOverrides({@AttributeOverride(name = "markdown", column = @Column(name = "corporate_markdown")), @AttributeOverride(name = "html", column = @Column(name = "corporate_html"))})
	private MarkdownContent corporateContent;

	public ContributorLicenseAgreement() {
	}

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * Allows differentiating agreements from one another. This is currently
	 * only able to be filled out via sql import.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Allows defining if the is the primary agreement. There may be additional
	 * agreements that have the same name, but are older versions of the
	 * agreement, variations of the agreement for different companies, etc.
	 */
	public boolean isPrimary() {
		return this.primary;
	}

	/**
	 * The {@link ContributorLicenseAgreement} that replaces this
	 * {@link ContributorLicenseAgreement}. If this is not signed, we check to
	 * see if {@link #getSupersedingCla()} is signed. If neither are signed,
	 * then the user signs {@link #getSupersedingCla()}.
	 */
	public ContributorLicenseAgreement getSupersedingCla() {
		return this.supersedingCla;
	}

	public Date getCreated() {
		return this.created;
	}

	public MarkdownContent getIndividualContent() {
		return this.individualContent;
	}

	public MarkdownContent getCorporateContent() {
		return this.corporateContent;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Allows differentiating agreements from one another. This is currently
	 * only able to be filled out via sql import.
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Allows defining if the is the primary agreement. There may be additional
	 * agreements that have the same name, but are older versions of the
	 * agreement, variations of the agreement for different companies, etc.
	 */
	public void setPrimary(final boolean primary) {
		this.primary = primary;
	}

	/**
	 * The {@link ContributorLicenseAgreement} that replaces this
	 * {@link ContributorLicenseAgreement}. If this is not signed, we check to
	 * see if {@link #getSupersedingCla()} is signed. If neither are signed,
	 * then the user signs {@link #getSupersedingCla()}.
	 */
	public void setSupersedingCla(final ContributorLicenseAgreement supersedingCla) {
		this.supersedingCla = supersedingCla;
	}

	public void setCreated(final Date created) {
		this.created = created;
	}

	public void setIndividualContent(final MarkdownContent individualContent) {
		this.individualContent = individualContent;
	}

	public void setCorporateContent(final MarkdownContent corporateContent) {
		this.corporateContent = corporateContent;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof ContributorLicenseAgreement)) return false;
		final ContributorLicenseAgreement other = (ContributorLicenseAgreement) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$id = this.getId();
		final java.lang.Object other$id = other.getId();
		if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
		final java.lang.Object this$name = this.getName();
		final java.lang.Object other$name = other.getName();
		if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
		final java.lang.Object this$description = this.getDescription();
		final java.lang.Object other$description = other.getDescription();
		if (this$description == null ? other$description != null : !this$description.equals(other$description)) return false;
		if (this.isPrimary() != other.isPrimary()) return false;
		final java.lang.Object this$supersedingCla = this.getSupersedingCla();
		final java.lang.Object other$supersedingCla = other.getSupersedingCla();
		if (this$supersedingCla == null ? other$supersedingCla != null : !this$supersedingCla.equals(other$supersedingCla)) return false;
		final java.lang.Object this$created = this.getCreated();
		final java.lang.Object other$created = other.getCreated();
		if (this$created == null ? other$created != null : !this$created.equals(other$created)) return false;
		final java.lang.Object this$individualContent = this.getIndividualContent();
		final java.lang.Object other$individualContent = other.getIndividualContent();
		if (this$individualContent == null ? other$individualContent != null : !this$individualContent.equals(other$individualContent)) return false;
		final java.lang.Object this$corporateContent = this.getCorporateContent();
		final java.lang.Object other$corporateContent = other.getCorporateContent();
		if (this$corporateContent == null ? other$corporateContent != null : !this$corporateContent.equals(other$corporateContent)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof ContributorLicenseAgreement;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $id = this.getId();
		result = result * PRIME + ($id == null ? 43 : $id.hashCode());
		final java.lang.Object $name = this.getName();
		result = result * PRIME + ($name == null ? 43 : $name.hashCode());
		final java.lang.Object $description = this.getDescription();
		result = result * PRIME + ($description == null ? 43 : $description.hashCode());
		result = result * PRIME + (this.isPrimary() ? 79 : 97);
		final java.lang.Object $supersedingCla = this.getSupersedingCla();
		result = result * PRIME + ($supersedingCla == null ? 43 : $supersedingCla.hashCode());
		final java.lang.Object $created = this.getCreated();
		result = result * PRIME + ($created == null ? 43 : $created.hashCode());
		final java.lang.Object $individualContent = this.getIndividualContent();
		result = result * PRIME + ($individualContent == null ? 43 : $individualContent.hashCode());
		final java.lang.Object $corporateContent = this.getCorporateContent();
		result = result * PRIME + ($corporateContent == null ? 43 : $corporateContent.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "ContributorLicenseAgreement(id=" + this.getId() + ", name=" + this.getName() + ", description=" + this.getDescription() + ", primary=" + this.isPrimary() + ", supersedingCla=" + this.getSupersedingCla() + ", created=" + this.getCreated() + ")";
	}
}
