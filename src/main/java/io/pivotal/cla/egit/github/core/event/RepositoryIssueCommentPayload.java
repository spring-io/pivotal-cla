/*
 * Copyright 2016 the original author or authors.
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
package io.pivotal.cla.egit.github.core.event;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.event.IssueCommentPayload;

/**
 * @author Mark Paluch
 */
@SuppressWarnings("serial")
public class RepositoryIssueCommentPayload extends IssueCommentPayload implements RepositoryAware, SenderAware {
	private Repository repository;
	private User sender;

	public RepositoryIssueCommentPayload() {
	}

	public Repository getRepository() {
		return this.repository;
	}

	public User getSender() {
		return this.sender;
	}

	public void setRepository(final Repository repository) {
		this.repository = repository;
	}

	public void setSender(final User sender) {
		this.sender = sender;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "RepositoryIssueCommentPayload(repository=" + this.getRepository() + ", sender=" + this.getSender() + ")";
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof RepositoryIssueCommentPayload)) return false;
		final RepositoryIssueCommentPayload other = (RepositoryIssueCommentPayload) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$repository = this.getRepository();
		final java.lang.Object other$repository = other.getRepository();
		if (this$repository == null ? other$repository != null : !this$repository.equals(other$repository)) return false;
		final java.lang.Object this$sender = this.getSender();
		final java.lang.Object other$sender = other.getSender();
		if (this$sender == null ? other$sender != null : !this$sender.equals(other$sender)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof RepositoryIssueCommentPayload;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $repository = this.getRepository();
		result = result * PRIME + ($repository == null ? 43 : $repository.hashCode());
		final java.lang.Object $sender = this.getSender();
		result = result * PRIME + ($sender == null ? 43 : $sender.hashCode());
		return result;
	}
}
