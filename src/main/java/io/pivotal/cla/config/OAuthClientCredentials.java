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
package io.pivotal.cla.config;

public class OAuthClientCredentials {
	private String clientSecret;
	private String clientId;

	public OAuthClientCredentials() {
	}

	public String getClientSecret() {
		return this.clientSecret;
	}

	public String getClientId() {
		return this.clientId;
	}

	public void setClientSecret(final String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public void setClientId(final String clientId) {
		this.clientId = clientId;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof OAuthClientCredentials)) return false;
		final OAuthClientCredentials other = (OAuthClientCredentials) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$clientSecret = this.getClientSecret();
		final java.lang.Object other$clientSecret = other.getClientSecret();
		if (this$clientSecret == null ? other$clientSecret != null : !this$clientSecret.equals(other$clientSecret)) return false;
		final java.lang.Object this$clientId = this.getClientId();
		final java.lang.Object other$clientId = other.getClientId();
		if (this$clientId == null ? other$clientId != null : !this$clientId.equals(other$clientId)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof OAuthClientCredentials;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $clientSecret = this.getClientSecret();
		result = result * PRIME + ($clientSecret == null ? 43 : $clientSecret.hashCode());
		final java.lang.Object $clientId = this.getClientId();
		result = result * PRIME + ($clientId == null ? 43 : $clientId.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "OAuthClientCredentials(clientSecret=" + this.getClientSecret() + ", clientId=" + this.getClientId() + ")";
	}
}
