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

public class CommitStatus {
	private int pullRequestId;
	private String repoId;
	private String sha;
	private String cla;
	private boolean success;
	private String url;
	private String githubUsername;

	/**
	 * @return the githubUsername
	 */
	public String getGithubUsername() {
		return githubUsername;
	}

	/**
	 * @param githubUsername the githubUsername to set
	 */
	public void setGithubUsername(String githubUsername) {
		this.githubUsername = githubUsername;
	}

	/**
	 * @return the pullRequestId
	 */
	public int getPullRequestId() {
		return pullRequestId;
	}

	/**
	 * @param pullRequestId
	 *            the pullRequestId to set
	 */
	public void setPullRequestId(int pullRequestId) {
		this.pullRequestId = pullRequestId;
	}

	/**
	 * @return the repoId
	 */
	public String getRepoId() {
		return repoId;
	}

	/**
	 * @param repoId
	 *            the repoId to set
	 */
	public void setRepoId(String repoId) {
		this.repoId = repoId;
	}

	/**
	 * @return the sha
	 */
	public String getSha() {
		return sha;
	}

	/**
	 * @param sha
	 *            the sha to set
	 */
	public void setSha(String sha) {
		this.sha = sha;
	}

	/**
	 * @return the cla
	 */
	public String getCla() {
		return cla;
	}

	/**
	 * @param cla
	 *            the cla to set
	 */
	public void setCla(String cla) {
		this.cla = cla;
	}

	/**
	 * @return the success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success
	 *            the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * The URL used to instruct the user of how to resolve the commit status. For example,
	 * https://123456.ngrok.io/sign/apache?repositoryId=spring-projects/spring-security&pullRequestId=10
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}


}
