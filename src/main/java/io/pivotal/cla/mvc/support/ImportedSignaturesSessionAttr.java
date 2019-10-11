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
package io.pivotal.cla.mvc.support;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

/**
 * @author Rob Winch
 */
public final class ImportedSignaturesSessionAttr {
	// visible for testing
	static final String ATTR_NAME = "importedSignatures";
	private final WebRequest webRequest;

	public ImportedSignaturesSessionAttr(WebRequest webRequest) {
		this.webRequest = webRequest;
	}

	public boolean getValue() {
		return Boolean.TRUE.equals(webRequest.getAttribute(ATTR_NAME, RequestAttributes.SCOPE_SESSION));
	}

	public void setValue(boolean value) {
		if (value) {
			webRequest.setAttribute(ATTR_NAME, value, RequestAttributes.SCOPE_SESSION);
		} else {
			webRequest.removeAttribute(ATTR_NAME, RequestAttributes.SCOPE_SESSION);
		}
	}
}
