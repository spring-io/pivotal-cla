package io.pivotal.cla.service;


import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MigratePullRequestStatusRequest {
	/**
	 * Repository ids in the format of spring-projects/spring-security
	 */
	private final List<String> repositoryIds;

	private final String commitStatusUrl;

	private final String accessToken;
}