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
package io.pivotal.cla.service;

import lombok.Data;

@Data
public class CommitStatus {
	private int pullRequestId;
	private String repoId;
	private String sha;
	private boolean success;
	private String url;
	private String githubUsername;


	/**
	 * The URL used to instruct the user of how to resolve the commit status. For example,
	 * https://123456.ngrok.io/sign/apache?repositoryId=spring-projects/spring-security&pullRequestId=10
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
}
