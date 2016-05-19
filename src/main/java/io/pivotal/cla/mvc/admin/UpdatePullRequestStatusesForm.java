package io.pivotal.cla.mvc.admin;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class UpdatePullRequestStatusesForm implements Serializable {
	private String claName;
	private List<String> repositories;

	private static final long serialVersionUID = 9167204453927088962L;
}
