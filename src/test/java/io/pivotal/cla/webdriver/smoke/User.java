package io.pivotal.cla.webdriver.smoke;

import lombok.Data;

@Data
public class User {
	String gitHubUsername;
	String gitHubPassword;
	String gitHubAccessToken;
}
