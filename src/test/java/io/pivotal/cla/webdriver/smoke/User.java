package io.pivotal.cla.webdriver.smoke;

import lombok.Data;

@Data
public class User {
	private String gitHubUsername;
	private String gitHubPassword;
	private String gitHubAccessToken;
}
