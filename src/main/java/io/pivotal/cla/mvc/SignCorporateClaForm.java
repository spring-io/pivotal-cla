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
package io.pivotal.cla.mvc;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import groovy.transform.EqualsAndHashCode;
import lombok.Data;

@Data
@EqualsAndHashCode(callSuper = false)
public class SignCorporateClaForm extends SignClaForm {
	@NotEmpty(message = "This is required")
	private String companyName;
	@NotEmpty(message = "This is required")
	private String organization;

	private List<String> organizations;
}
