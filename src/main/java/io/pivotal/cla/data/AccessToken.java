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

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessToken {
	public static String CLA_ACCESS_TOKEN_ID = "this";

	@Id
	private String id;

	private String token;

	/**
	 * <p>
	 * Get's the id for the access token.
	 * </p>
	 * <p>
	 * For GitHub repositories this is represented using the relative identifier
	 * of the repository. For example, for the repository
	 * https://github.com/spring-projects/spring-security the id would be
	 * spring-projects/spring-security.
	 * </p>
	 * <p>
	 * For accessing the AccessToken used to make requests to this application
	 * (i.e. when GitHub's pull request hook makes calls back into the
	 * application) the {@link AccessToken#CLA_ACCESS_TOKEN_ID} is used for the
	 * id.
	 * </p>
	 *
	 * @return the githubId
	 */
	public String getId() {
		return id;
	}
}
