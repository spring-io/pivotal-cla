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
package io.pivotal.cla.mvc.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * @author Rob Winch
 *
 */
public class ImportedSignaturesSessionAttrTests {
	ImportedSignaturesSessionAttr attr;

	MockHttpServletRequest request;

	@Before
	public void setup() {
		request = new MockHttpServletRequest();
		attr = new ImportedSignaturesSessionAttr(new ServletWebRequest(request));
	}

	@Test
	public void getValueDefault() {
		assertThat(attr.getValue()).isFalse();
	}

	@Test
	public void setValueTrue() {
		attr.setValue(true);
		assertThat(attr.getValue()).isTrue();
	}

	@Test
	public void setValueFalse() {
		attr.setValue(false);
		assertThat(attr.getValue()).isFalse();
		assertThat(request.getSession().getAttributeNames().hasMoreElements()).isFalse();
	}
}
