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
package io.pivotal.cla.mvc.admin;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

public class LinkClaForm {
	@NotEmpty(message = "This is required")
	private List<String> repositories;

	@NotEmpty(message = "This is required")
	private String claName;

	/**
	 * @return the repositories
	 */
	public List<String> getRepositories() {
		return repositories;
	}

	/**
	 * @param repositories
	 *            the repositories to set
	 */
	public void setRepositories(List<String> repositories) {
		this.repositories = repositories;
	}

	/**
	 * @return the claName
	 */
	public String getClaName() {
		return claName;
	}

	/**
	 * @param claName
	 *            the claName to set
	 */
	public void setClaName(String claName) {
		this.claName = claName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LinkClaForm [repositories=" + repositories + ", claName=" + claName + "]";
	}
}
