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

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * @author Rob Winch
 *
 */
public class ImportedSignaturesSessionAttrResolverTests {

	Method method;

	ImportedSignaturesSessionAttrResolver resolver;

	@Before
	public void setup() {
		resolver = new ImportedSignaturesSessionAttrResolver();
		method = ReflectionUtils.findMethod(ImportedSignaturesSessionAttrResolverTests.class, "methodParameter", ImportedSignaturesSessionAttr.class, Object.class);
	}

	@Test
	public void supportsParameterFalse() {
		MethodParameter parameter = new MethodParameter(method, 1);
		assertThat(resolver.supportsParameter(parameter)).isFalse();
	}

	@Test
	public void supportsParameterTrue() {
		MethodParameter parameter = new MethodParameter(method, 0);
		assertThat(resolver.supportsParameter(parameter)).isTrue();
	}

	@Test
	public void resolveArgumentFalse() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		NativeWebRequest webRequest = new ServletWebRequest(request);
		MethodParameter parameter = new MethodParameter(method, 0);

		ImportedSignaturesSessionAttr resolved = (ImportedSignaturesSessionAttr) resolver.resolveArgument(parameter, null, webRequest, null);
		assertThat(resolved.getValue()).isFalse();
	}

	@Test
	public void resolveArgumentTrue() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.getSession().setAttribute(ImportedSignaturesSessionAttr.ATTR_NAME, true);
		NativeWebRequest webRequest = new ServletWebRequest(request);
		MethodParameter parameter = new MethodParameter(method, 0);

		ImportedSignaturesSessionAttr resolved = (ImportedSignaturesSessionAttr) resolver.resolveArgument(parameter, null, webRequest, null);
		assertThat(resolved.getValue()).isTrue();
	}

	public void methodParameter(ImportedSignaturesSessionAttr attr, Object other) {}
}
