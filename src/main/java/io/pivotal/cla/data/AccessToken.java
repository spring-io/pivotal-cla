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

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class AccessToken {
	public static String CLA_ACCESS_TOKEN_ID = "this";
	@Id
	private String id;
	private String token;

	/**
	 * <p>
	 * Get's the id for the access token.
	 * </p>
	 * <p>
	 * For GitHub repositories this is represented using the relative identifier
	 * of the repository. For example, for the repository
	 * https://github.com/spring-projects/spring-security the id would be
	 * spring-projects/spring-security.
	 * </p>
	 * <p>
	 * For accessing the AccessToken used to make requests to this application
	 * (i.e. when GitHub's pull request hook makes calls back into the
	 * application) the {@link AccessToken#CLA_ACCESS_TOKEN_ID} is used for the
	 * id.
	 * </p>
	 *
	 * @return the githubId
	 */
	public String getId() {
		return id;
	}

	public String getToken() {
		return this.token;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setToken(final String token) {
		this.token = token;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof AccessToken)) return false;
		final AccessToken other = (AccessToken) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$id = this.getId();
		final java.lang.Object other$id = other.getId();
		if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
		final java.lang.Object this$token = this.getToken();
		final java.lang.Object other$token = other.getToken();
		if (this$token == null ? other$token != null : !this$token.equals(other$token)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof AccessToken;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $id = this.getId();
		result = result * PRIME + ($id == null ? 43 : $id.hashCode());
		final java.lang.Object $token = this.getToken();
		result = result * PRIME + ($token == null ? 43 : $token.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "AccessToken(id=" + this.getId() + ", token=" + this.getToken() + ")";
	}

	@java.beans.ConstructorProperties({"id", "token"})
	public AccessToken(final String id, final String token) {
		this.id = id;
		this.token = token;
	}

	public AccessToken() {
	}
}
