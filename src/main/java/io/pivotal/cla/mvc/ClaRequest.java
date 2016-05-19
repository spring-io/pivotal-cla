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

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.pivotal.cla.mvc.util.UrlBuilder;
import io.pivotal.cla.service.github.UpdatePullRequestStatusRequest;
import lombok.Data;

/**
 * @author Rob Winch
 *
 */
@Data
public class ClaRequest {
	String claName;
	String repositoryId;
	Integer pullRequestId;

	public UpdatePullRequestStatusRequest createUpdatePullRequestStatus(String currentUserGitHubLogin) throws Exception {
		if(pullRequestId == null) {
			return null;
		}
		UpdatePullRequestStatusRequest request = new UpdatePullRequestStatusRequest();
		request.setCurrentUserGitHubLogin(currentUserGitHubLogin);
		request.setPullRequestId(pullRequestId);
		request.setRepositoryId(repositoryId);
		request.setCommitStatusUrl(signUrl());
		return request;
	}

	private String signUrl() throws Exception {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();

		return UrlBuilder.signUrl()
				.request(request)
				.claName(claName)
				.repositoryId(repositoryId)
				.pullRequestId(pullRequestId)
				.build();
	}
}
