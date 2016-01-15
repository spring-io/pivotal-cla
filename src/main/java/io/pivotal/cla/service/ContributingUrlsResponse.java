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
package io.pivotal.cla.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Rob Winch
 *
 */
public class ContributingUrlsResponse {
	private Collection<String> markdown = new ArrayList<>();

	private Collection<String> asciidoc = new ArrayList<>();

	/**
	 * @return the markdown
	 */
	public Collection<String> getMarkdown() {
		return markdown;
	}

	/**
	 * @param markdown the markdown to set
	 */
	public void setMarkdown(Collection<String> markdown) {
		this.markdown = new HashSet<>(markdown);
	}

	/**
	 * @return the asciidoc
	 */
	public Collection<String> getAsciidoc() {
		return asciidoc;
	}

	/**
	 * @param asciidoc the asciidoc to set
	 */
	public void setAsciidoc(Collection<String> asciidoc) {
		this.asciidoc = new HashSet<>(asciidoc);
	}
}