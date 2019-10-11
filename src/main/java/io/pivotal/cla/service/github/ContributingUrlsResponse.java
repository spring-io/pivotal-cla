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

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Rob Winch
 */
public class ContributingUrlsResponse {
	private Collection<String> markdown = new ArrayList<>();
	private Collection<String> asciidoc = new ArrayList<>();

	public ContributingUrlsResponse() {
	}

	public Collection<String> getMarkdown() {
		return this.markdown;
	}

	public Collection<String> getAsciidoc() {
		return this.asciidoc;
	}

	public void setMarkdown(final Collection<String> markdown) {
		this.markdown = markdown;
	}

	public void setAsciidoc(final Collection<String> asciidoc) {
		this.asciidoc = asciidoc;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof ContributingUrlsResponse)) return false;
		final ContributingUrlsResponse other = (ContributingUrlsResponse) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$markdown = this.getMarkdown();
		final java.lang.Object other$markdown = other.getMarkdown();
		if (this$markdown == null ? other$markdown != null : !this$markdown.equals(other$markdown)) return false;
		final java.lang.Object this$asciidoc = this.getAsciidoc();
		final java.lang.Object other$asciidoc = other.getAsciidoc();
		if (this$asciidoc == null ? other$asciidoc != null : !this$asciidoc.equals(other$asciidoc)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof ContributingUrlsResponse;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $markdown = this.getMarkdown();
		result = result * PRIME + ($markdown == null ? 43 : $markdown.hashCode());
		final java.lang.Object $asciidoc = this.getAsciidoc();
		result = result * PRIME + ($asciidoc == null ? 43 : $asciidoc.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "ContributingUrlsResponse(markdown=" + this.getMarkdown() + ", asciidoc=" + this.getAsciidoc() + ")";
	}
}
