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

import org.eclipse.egit.github.core.CommitStatus;

public class ContextCommitStatus extends CommitStatus {
	private static final long serialVersionUID = -1578730338049714284L;
	private String context;

	public ContextCommitStatus() {
	}

	public String getContext() {
		return this.context;
	}

	public void setContext(final String context) {
		this.context = context;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "ContextCommitStatus(context=" + this.getContext() + ")";
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof ContextCommitStatus)) return false;
		final ContextCommitStatus other = (ContextCommitStatus) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$context = this.getContext();
		final java.lang.Object other$context = other.getContext();
		if (this$context == null ? other$context != null : !this$context.equals(other$context)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof ContextCommitStatus;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $context = this.getContext();
		result = result * PRIME + ($context == null ? 43 : $context.hashCode());
		return result;
	}
}
