package io.pivotal.cla.mvc.admin;

/**
 * @author Rob Winch
 */
public class LookupForm {
	private String gitHubLogin;

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
