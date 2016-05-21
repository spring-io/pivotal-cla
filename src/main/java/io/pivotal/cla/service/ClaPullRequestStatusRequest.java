package io.pivotal.cla.service;

import io.pivotal.cla.service.github.CommitStatus;
import lombok.Data;

@Data
public class ClaPullRequestStatusRequest {
	/**
	 * Used to default {@link CommitStatus#getSuccess()} if it is null.
	 */
	private String claName;

	private CommitStatus commitStatus;
}
