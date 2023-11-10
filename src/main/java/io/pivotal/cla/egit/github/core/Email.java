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
package io.pivotal.cla.egit.github.core;

public class Email {
	private String email;
	private boolean verified;

	public Email() {
	}

	public String getEmail() {
		return this.email;
	}

	public boolean isVerified() {
		return this.verified;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public void setVerified(final boolean verified) {
		this.verified = verified;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof Email)) return false;
		final Email other = (Email) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$email = this.getEmail();
		final java.lang.Object other$email = other.getEmail();
		if (this$email == null ? other$email != null : !this$email.equals(other$email)) return false;
		if (this.isVerified() != other.isVerified()) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof Email;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $email = this.getEmail();
		result = result * PRIME + ($email == null ? 43 : $email.hashCode());
		result = result * PRIME + (this.isVerified() ? 79 : 97);
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "Email(email=" + this.getEmail() + ", verified=" + this.isVerified() + ")";
	}
}
