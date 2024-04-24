package io.pivotal.cla.mvc.admin;

import jakarta.validation.constraints.NotEmpty;

/**
 * @author Rob Winch
 */
public class LookupForm {
	@NotEmpty(message = "This is required.")
	private String gitHubLogin;

	@NotEmpty(message = "This is required.")
	private String claName;

	public String getGitHubLogin() {
		return this.gitHubLogin;
	}

	public void setGitHubLogin(String gitHubLogin) {
		this.gitHubLogin = gitHubLogin;
	}

	public String getClaName() {
		return this.claName;
	}

	public void setClaName(String claName) {
		this.claName = claName;
	}
}
