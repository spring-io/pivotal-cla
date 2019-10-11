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
package io.pivotal.cla.mvc.admin;

import java.util.List;
import org.hibernate.validator.constraints.NotEmpty;

public class LinkClaForm {
	@NotEmpty(message = "This is required")
	private List<String> repositories;
	@NotEmpty(message = "This is required")
	private String claName;

	public LinkClaForm() {
	}

	public List<String> getRepositories() {
		return this.repositories;
	}

	public String getClaName() {
		return this.claName;
	}

	public void setRepositories(final List<String> repositories) {
		this.repositories = repositories;
	}

	public void setClaName(final String claName) {
		this.claName = claName;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof LinkClaForm)) return false;
		final LinkClaForm other = (LinkClaForm) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$repositories = this.getRepositories();
		final java.lang.Object other$repositories = other.getRepositories();
		if (this$repositories == null ? other$repositories != null : !this$repositories.equals(other$repositories)) return false;
		final java.lang.Object this$claName = this.getClaName();
		final java.lang.Object other$claName = other.getClaName();
		if (this$claName == null ? other$claName != null : !this$claName.equals(other$claName)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof LinkClaForm;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $repositories = this.getRepositories();
		result = result * PRIME + ($repositories == null ? 43 : $repositories.hashCode());
		final java.lang.Object $claName = this.getClaName();
		result = result * PRIME + ($claName == null ? 43 : $claName.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "LinkClaForm(repositories=" + this.getRepositories() + ", claName=" + this.getClaName() + ")";
	}
}
