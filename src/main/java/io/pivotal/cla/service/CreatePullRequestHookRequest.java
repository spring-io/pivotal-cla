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

import java.util.List;

/**
 * @author Rob Winch
 *
 */
public class CreatePullRequestHookRequest {

	private String accessToken;

	private List<String> repositoryIds;

	private String githubEventUrl;

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @return the repositoryIds
	 */
	public List<String> getRepositoryIds() {
		return repositoryIds;
	}

	/**
	 * @param repositoryIds the repositoryIds to set
	 */
	public void setRepositoryIds(List<String> repositoryIds) {
		this.repositoryIds = repositoryIds;
	}

	/**
	 * @return the githubEventUrl
	 */
	public String getGithubEventUrl() {
		return githubEventUrl;
	}

	/**
	 * @param githubEventUrl the githubEventUrl to set
	 */
	public void setGithubEventUrl(String githubEventUrl) {
		this.githubEventUrl = githubEventUrl;
	}
}
