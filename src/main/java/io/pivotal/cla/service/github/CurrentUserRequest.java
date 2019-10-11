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
package io.pivotal.cla.service.github;

/**
 * @author Rob Winch
 */
public class CurrentUserRequest {
	private OAuthAccessTokenParams oauthParams;
	private boolean requestAdminAccess;

	public CurrentUserRequest() {
	}

	public OAuthAccessTokenParams getOauthParams() {
		return this.oauthParams;
	}

	public boolean isRequestAdminAccess() {
		return this.requestAdminAccess;
	}

	public void setOauthParams(final OAuthAccessTokenParams oauthParams) {
		this.oauthParams = oauthParams;
	}

	public void setRequestAdminAccess(final boolean requestAdminAccess) {
		this.requestAdminAccess = requestAdminAccess;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof CurrentUserRequest)) return false;
		final CurrentUserRequest other = (CurrentUserRequest) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$oauthParams = this.getOauthParams();
		final java.lang.Object other$oauthParams = other.getOauthParams();
		if (this$oauthParams == null ? other$oauthParams != null : !this$oauthParams.equals(other$oauthParams)) return false;
		if (this.isRequestAdminAccess() != other.isRequestAdminAccess()) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof CurrentUserRequest;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $oauthParams = this.getOauthParams();
		result = result * PRIME + ($oauthParams == null ? 43 : $oauthParams.hashCode());
		result = result * PRIME + (this.isRequestAdminAccess() ? 79 : 97);
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "CurrentUserRequest(oauthParams=" + this.getOauthParams() + ", requestAdminAccess=" + this.isRequestAdminAccess() + ")";
	}
}
