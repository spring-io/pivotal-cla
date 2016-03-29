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
package io.pivotal.cla.data.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.pivotal.cla.data.User;
import io.pivotal.cla.security.WithSigningUserFactory;

/**
 * @author Rob Winch
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class IndividualSignatureRepositoryTests {
	@Mock
	IndividualSignatureRepository mockSignatures;

	IndividualSignatureRepository proxiedSignatures;

	@Before
	public void setup() {
		proxiedSignatures = DefaultMethodMock.create(mockSignatures);
	}

	@Test
	public void getSignatureAllNull() throws Exception {
		assertThat(proxiedSignatures.getSignature(null,null,null)).isNull();
		verifyZeroInteractions(mockSignatures);
	}


	@Test
	public void getSignatureNullUserEmail() throws Exception {
		User user = WithSigningUserFactory.create();
		user.setEmails(null);

		assertThat(proxiedSignatures.getSignature(user,null,null)).isNull();

		verifyZeroInteractions(mockSignatures);
	}
}
