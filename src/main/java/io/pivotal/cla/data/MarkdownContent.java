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

import javax.persistence.Embeddable;
import javax.persistence.Lob;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Rob Winch
 *
 */
@Embeddable
public class MarkdownContent {
	@NotEmpty(message = "This is required")
	@Lob
	private String markdown;

	@Lob
	private String html;

	/**
	 * @return the markdown
	 */
	public String getMarkdown() {
		return markdown;
	}

	/**
	 * @param markdown the markdown to set
	 */
	public void setMarkdown(String markdown) {
		this.markdown = markdown;
	}

	/**
	 * @return the html
	 */
	public String getHtml() {
		return html;
	}

	/**
	 * @param html the html to set
	 */
	public void setHtml(String html) {
		this.html = html;
	}
}
