package io.pivotal.cla.service;

import io.pivotal.cla.service.github.PullRequestStatus;
import lombok.Data;

@Data
public class ClaPullRequestStatusRequest {
	/**
	 * Used to default {@link PullRequestStatus#getSuccess()} if it is null.
	 */
	private String claName;

	private PullRequestStatus commitStatus;
}
