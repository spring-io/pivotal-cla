/*
 * Copyright 2016 the original author or authors.
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
package io.pivotal.cla.egit.github.core;

import org.eclipse.egit.github.core.RepositoryId;

/**
 * @author Mark Paluch
 */
public final class PullRequestId {
	private final RepositoryId repositoryId;
	private final int id;

	private PullRequestId(final RepositoryId repositoryId, final int id) {
		this.repositoryId = repositoryId;
		this.id = id;
	}

	public static PullRequestId of(final RepositoryId repositoryId, final int id) {
		return new PullRequestId(repositoryId, id);
	}

	public RepositoryId getRepositoryId() {
		return this.repositoryId;
	}

	public int getId() {
		return this.id;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof PullRequestId)) return false;
		final PullRequestId other = (PullRequestId) o;
		final java.lang.Object this$repositoryId = this.getRepositoryId();
		final java.lang.Object other$repositoryId = other.getRepositoryId();
		if (this$repositoryId == null ? other$repositoryId != null : !this$repositoryId.equals(other$repositoryId)) return false;
		if (this.getId() != other.getId()) return false;
		return true;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $repositoryId = this.getRepositoryId();
		result = result * PRIME + ($repositoryId == null ? 43 : $repositoryId.hashCode());
		result = result * PRIME + this.getId();
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "PullRequestId(repositoryId=" + this.getRepositoryId() + ", id=" + this.getId() + ")";
	}
}
