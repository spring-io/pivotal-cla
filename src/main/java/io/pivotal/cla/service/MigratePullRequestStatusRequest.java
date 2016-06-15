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
	final List<String> repositoryIds;

	final String commitStatusUrl;

	final String accessToken;

	final String faqUrl;

	final String baseSyncUrl;
}