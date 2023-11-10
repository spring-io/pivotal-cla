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
package io.pivotal.cla.mvc;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import io.pivotal.cla.mvc.util.UrlBuilder;
import io.pivotal.cla.service.ClaPullRequestStatusRequest;
import io.pivotal.cla.service.github.PullRequestStatus;

/**
 * @author Rob Winch
 */
public class ClaRequest {
	private String claName;
	private String repositoryId;
	private Integer pullRequestId;

	public ClaPullRequestStatusRequest createUpdatePullRequestStatus(String currentUserGitHubLogin) throws Exception {
		if (pullRequestId == null) {
			return null;
		}
		PullRequestStatus commitStatus = new PullRequestStatus();
		commitStatus.setRepoId(repositoryId);
		commitStatus.setPullRequestId(pullRequestId);
		commitStatus.setUrl(signUrl());
		commitStatus.setSyncUrl(syncUrl());
		commitStatus.setFaqUrl(faqUrl());
		commitStatus.setGitHubUsername(currentUserGitHubLogin);
		commitStatus.setPullRequestState(PullRequestStatus.UNKNOWN_PULL_REQUEST_STATE);
		ClaPullRequestStatusRequest request = new ClaPullRequestStatusRequest();
		request.setClaName(claName);
		request.setCommitStatus(commitStatus);
		return request;
	}

	private String signUrl() throws Exception {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		return UrlBuilder.signUrl().request(request).claName(claName).repositoryId(repositoryId).pullRequestId(pullRequestId).build();
	}

	private String syncUrl() throws Exception {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		return UrlBuilder.createSyncUrl(request, claName, repositoryId, pullRequestId);
	}

	private String faqUrl() throws Exception {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		return UrlBuilder.createAboutUrl(request);
	}

	public ClaRequest() {
	}

	public String getClaName() {
		return this.claName;
	}

	public String getRepositoryId() {
		return this.repositoryId;
	}

	public Integer getPullRequestId() {
		return this.pullRequestId;
	}

	public void setClaName(final String claName) {
		this.claName = claName;
	}

	public void setRepositoryId(final String repositoryId) {
		this.repositoryId = repositoryId;
	}

	public void setPullRequestId(final Integer pullRequestId) {
		this.pullRequestId = pullRequestId;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof ClaRequest)) return false;
		final ClaRequest other = (ClaRequest) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$claName = this.getClaName();
		final java.lang.Object other$claName = other.getClaName();
		if (this$claName == null ? other$claName != null : !this$claName.equals(other$claName)) return false;
		final java.lang.Object this$repositoryId = this.getRepositoryId();
		final java.lang.Object other$repositoryId = other.getRepositoryId();
		if (this$repositoryId == null ? other$repositoryId != null : !this$repositoryId.equals(other$repositoryId)) return false;
		final java.lang.Object this$pullRequestId = this.getPullRequestId();
		final java.lang.Object other$pullRequestId = other.getPullRequestId();
		if (this$pullRequestId == null ? other$pullRequestId != null : !this$pullRequestId.equals(other$pullRequestId)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof ClaRequest;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $claName = this.getClaName();
		result = result * PRIME + ($claName == null ? 43 : $claName.hashCode());
		final java.lang.Object $repositoryId = this.getRepositoryId();
		result = result * PRIME + ($repositoryId == null ? 43 : $repositoryId.hashCode());
		final java.lang.Object $pullRequestId = this.getPullRequestId();
		result = result * PRIME + ($pullRequestId == null ? 43 : $pullRequestId.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "ClaRequest(claName=" + this.getClaName() + ", repositoryId=" + this.getRepositoryId() + ", pullRequestId=" + this.getPullRequestId() + ")";
	}
}
