package io.pivotal.cla.mvc.admin;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class UpdatePullRequestStatusesForm implements Serializable {
	String claName;
	List<String> repositories;

	private static final long serialVersionUID = 9167204453927088962L;
}
