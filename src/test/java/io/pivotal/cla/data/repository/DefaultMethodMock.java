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
package io.pivotal.cla.data.repository;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DefaultMethodMock<T> implements InvocationHandler {
	public final T mockDelegate;

	public DefaultMethodMock(T delegate) {
		super();
		this.mockDelegate = delegate;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
		if (!constructor.isAccessible()) {
			constructor.setAccessible(true);
		}
		if (method.isDefault()) {
			final Class<?> declaringClass = method.getDeclaringClass();
			return constructor.newInstance(declaringClass, MethodHandles.Lookup.PRIVATE)
					.unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
		}

		return method.invoke(this.mockDelegate, args);
	}

	@SuppressWarnings("unchecked")
	public static <T> T create(T mock) {
		Class<?> mockClass = mock.getClass().getInterfaces()[0];
		return (T) Proxy.newProxyInstance(mockClass.getClassLoader(), new Class[] { mockClass },
				new DefaultMethodMock<T>(mock));

	}
}