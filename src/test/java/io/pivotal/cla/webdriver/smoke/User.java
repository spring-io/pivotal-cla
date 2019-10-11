package io.pivotal.cla.webdriver.smoke;

public class User {
	String gitHubUsername;
	String gitHubPassword;
	String gitHubAccessToken;

	public String getGitHubUsername() {
		return this.gitHubUsername;
	}

	public void setGitHubUsername(String gitHubUsername) {
		this.gitHubUsername = gitHubUsername;
	}

	public String getGitHubPassword() {
		return this.gitHubPassword;
	}

	public void setGitHubPassword(String gitHubPassword) {
		this.gitHubPassword = gitHubPassword;
	}

	public String getGitHubAccessToken() {
		return this.gitHubAccessToken;
	}

	public void setGitHubAccessToken(String gitHubAccessToken) {
		this.gitHubAccessToken = gitHubAccessToken;
	}
}
