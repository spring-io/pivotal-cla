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
package io.pivotal.cla.service.github;

/**
 * @author Rob Winch
 */
public class OAuthAccessTokenParams {
	private String state;
	private String callbackUrl;
	private String code;

	public OAuthAccessTokenParams() {
	}

	public String getState() {
		return this.state;
	}

	public String getCallbackUrl() {
		return this.callbackUrl;
	}

	public String getCode() {
		return this.code;
	}

	public void setState(final String state) {
		this.state = state;
	}

	public void setCallbackUrl(final String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof OAuthAccessTokenParams)) return false;
		final OAuthAccessTokenParams other = (OAuthAccessTokenParams) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$state = this.getState();
		final java.lang.Object other$state = other.getState();
		if (this$state == null ? other$state != null : !this$state.equals(other$state)) return false;
		final java.lang.Object this$callbackUrl = this.getCallbackUrl();
		final java.lang.Object other$callbackUrl = other.getCallbackUrl();
		if (this$callbackUrl == null ? other$callbackUrl != null : !this$callbackUrl.equals(other$callbackUrl)) return false;
		final java.lang.Object this$code = this.getCode();
		final java.lang.Object other$code = other.getCode();
		if (this$code == null ? other$code != null : !this$code.equals(other$code)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof OAuthAccessTokenParams;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $state = this.getState();
		result = result * PRIME + ($state == null ? 43 : $state.hashCode());
		final java.lang.Object $callbackUrl = this.getCallbackUrl();
		result = result * PRIME + ($callbackUrl == null ? 43 : $callbackUrl.hashCode());
		final java.lang.Object $code = this.getCode();
		result = result * PRIME + ($code == null ? 43 : $code.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "OAuthAccessTokenParams(state=" + this.getState() + ", callbackUrl=" + this.getCallbackUrl() + ", code=" + this.getCode() + ")";
	}
}
