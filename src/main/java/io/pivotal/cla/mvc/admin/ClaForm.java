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
package io.pivotal.cla.mvc.admin;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.StringUtils;

import io.pivotal.cla.data.ContributorLicenseAgreement;
import io.pivotal.cla.data.MarkdownContent;
import lombok.Data;

/**
 * @author Rob Winch
 *
 */
@Data
public class ClaForm {
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
	private boolean primary;

	/**
	 * The {@link ContributorLicenseAgreement} that replaces this
	 * {@link ContributorLicenseAgreement}. If this is not signed, we check to
	 * see if {@link #getSupersedingCla()} is signed. If neither are signed,
	 * then the user signs {@link #getSupersedingCla()}.
	 */
	private Long supersedingCla;

	@NotNull(message = "This is required")
	@Valid
	private MarkdownContent individualContent;

	@Valid
	@NotNull(message = "This is required")
	private MarkdownContent corporateContent;

	public void setDescription(String description) {
		if(StringUtils.hasLength(description)) {
			this.description = description;
		}
	}
}
