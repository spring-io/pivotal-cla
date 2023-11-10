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
package io.pivotal.cla.mvc;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

public class SignClaForm extends ClaRequest {
	@NotNull
	private Long claId;
	@NotEmpty(message = "This is required")
	private String name;
	@NotEmpty(message = "This is required")
	private String email;
	private String mailingAddress;
	private String country;
	private String telephone;
	@AssertTrue(message = "This is required")
	private Boolean confirm;
	private boolean signed;

	public SignClaForm() {
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

	public Boolean getConfirm() {
		return this.confirm;
	}

	public boolean isSigned() {
		return this.signed;
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

	public void setConfirm(final Boolean confirm) {
		this.confirm = confirm;
	}

	public void setSigned(final boolean signed) {
		this.signed = signed;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof SignClaForm)) return false;
		final SignClaForm other = (SignClaForm) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		if (!super.equals(o)) return false;
		final java.lang.Object this$claId = this.getClaId();
		final java.lang.Object other$claId = other.getClaId();
		if (this$claId == null ? other$claId != null : !this$claId.equals(other$claId)) return false;
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
		final java.lang.Object this$confirm = this.getConfirm();
		final java.lang.Object other$confirm = other.getConfirm();
		if (this$confirm == null ? other$confirm != null : !this$confirm.equals(other$confirm)) return false;
		if (this.isSigned() != other.isSigned()) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof SignClaForm;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		result = result * PRIME + super.hashCode();
		final java.lang.Object $claId = this.getClaId();
		result = result * PRIME + ($claId == null ? 43 : $claId.hashCode());
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
		final java.lang.Object $confirm = this.getConfirm();
		result = result * PRIME + ($confirm == null ? 43 : $confirm.hashCode());
		result = result * PRIME + (this.isSigned() ? 79 : 97);
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "SignClaForm(super=" + super.toString() + ", claId=" + this.getClaId() + ", name=" + this.getName() + ", email=" + this.getEmail() + ", mailingAddress=" + this.getMailingAddress() + ", country=" + this.getCountry() + ", telephone=" + this.getTelephone() + ", confirm=" + this.getConfirm() + ", signed=" + this.isSigned() + ")";
	}
}
