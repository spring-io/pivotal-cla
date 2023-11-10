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
package io.pivotal.cla.test.context;

import org.springframework.test.context.ActiveProfilesResolver;
import org.springframework.test.context.support.DefaultActiveProfilesResolver;

/**
 *
 * @author Rob Winch
 *
 */
public class SystemActiveProfilesResolver implements ActiveProfilesResolver {
	DefaultActiveProfilesResolver resolver = new DefaultActiveProfilesResolver();

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.springframework.test.context.ActiveProfilesResolver#resolve(java.lang
	 * .Class)
	 */
	@Override
	public String[] resolve(Class<?> testClass) {
		String[] resolve = resolver.resolve(testClass);
		if (resolve.length == 0) {
			return resolve;
		}

		String resolved = System.getProperty(resolve[0]);

		return resolved == null ? new String[] {} : new String[] { resolved };
	}

}
