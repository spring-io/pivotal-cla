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
package io.pivotal.cla.data;

import javax.persistence.Embeddable;
import javax.persistence.Lob;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Rob Winch
 */
@Embeddable
public class MarkdownContent {
	@NotEmpty(message = "This is required")
	@Lob
	private String markdown;
	@Lob
	private String html;

	public MarkdownContent() {
	}

	public String getMarkdown() {
		return this.markdown;
	}

	public String getHtml() {
		return this.html;
	}

	public void setMarkdown(final String markdown) {
		this.markdown = markdown;
	}

	public void setHtml(final String html) {
		this.html = html;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof MarkdownContent)) return false;
		final MarkdownContent other = (MarkdownContent) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$markdown = this.getMarkdown();
		final java.lang.Object other$markdown = other.getMarkdown();
		if (this$markdown == null ? other$markdown != null : !this$markdown.equals(other$markdown)) return false;
		final java.lang.Object this$html = this.getHtml();
		final java.lang.Object other$html = other.getHtml();
		if (this$html == null ? other$html != null : !this$html.equals(other$html)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof MarkdownContent;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $markdown = this.getMarkdown();
		result = result * PRIME + ($markdown == null ? 43 : $markdown.hashCode());
		final java.lang.Object $html = this.getHtml();
		result = result * PRIME + ($html == null ? 43 : $html.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "MarkdownContent(markdown=" + this.getMarkdown() + ", html=" + this.getHtml() + ")";
	}
}
