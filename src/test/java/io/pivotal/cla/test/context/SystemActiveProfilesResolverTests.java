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
package io.pivotal.cla.test.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author Rob Winch
 *
 */
public class SystemActiveProfilesResolverTests {
	SystemActiveProfilesResolver resolver;

	@Before
	public void setup() {
		resolver = new SystemActiveProfilesResolver();
	}

	@Test
	public void noAnnotationIsEmpty() {
		assertThat(resolver.resolve(NoAnnotationTests.class)).isEmpty();
	}

	@Test
	public void noValueIsEmpty() {
		assertThat(resolver.resolve(NoAnnotationTests.class)).isEmpty();
	}

	@Test
	public void valueWithNoSystemProperty() {
		assertThat(resolver.resolve(ValueWithNoSystemProperty.class)).isEmpty();
	}

	@Test
	public void valueWithSystemProperty() {
		String value = "foo";
		System.setProperty(ValueWithSystemProperty.NAME, value);
		assertThat(resolver.resolve(ValueWithSystemProperty.class)).containsOnly(value);
		System.clearProperty(ValueWithSystemProperty.NAME);
	}

	static class NoAnnotationTests {}

	@ActiveProfiles
	static class NoValueTests {}

	@ActiveProfiles("spring.active.profiles.thisisnotgoingtobeset")
	static class ValueWithNoSystemProperty {
	}

	@ActiveProfiles(ValueWithSystemProperty.NAME)
	static class ValueWithSystemProperty {
		public static final String NAME = "spring.active.profiles.SystemActiveProfilesResolverTests.valueWithSystemProperty";

	}
}
