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
package io.pivotal.cla.service.github;

import io.pivotal.cla.data.repository.AccessTokenRepository;
import lombok.Data;

@Data
public class PullRequestStatus {

	public static final String UNKNOWN_PULL_REQUEST_STATE = "unknown";
	public static final String OPEN_PULL_REQUEST_STATE = "open";

	int pullRequestId;
	String repoId;
	String sha;
	Boolean success;
	String url;
	/**
	 * The URL used to sync the Pull Request. For example,
	 * https://cla.pivotal.io/sync/pivotal?repositoryId=spring-projects/spring-security&pullRequestId=10
	 */
	String syncUrl;
	/**
	 * The URL to the FAQ. For example,
	 * https://cla.pivotal.io/faq
	 */
	String faqUrl;
	String gitHubUsername;
	boolean admin;
	/**
	 * The Access Token used for updating the commit status. This is typically
	 * looked up using the {@link AccessTokenRepository} by the repoId.
	 */
	String accessToken;

	String pullRequestState;

	public boolean isSuccess() {
		return Boolean.TRUE.equals(success);
	}

	public boolean shouldInteractWithComments() {
		return UNKNOWN_PULL_REQUEST_STATE.equals(pullRequestState) || OPEN_PULL_REQUEST_STATE.equalsIgnoreCase(pullRequestState);
	}

	/**
	 * The URL used to instruct the user of how to resolve the commit status. For example,
	 * https://cla.pivotal.io/sign/pivotal?repositoryId=spring-projects/spring-security&pullRequestId=10
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
}
