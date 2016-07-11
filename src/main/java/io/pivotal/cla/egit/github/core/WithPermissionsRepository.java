package io.pivotal.cla.egit.github.core;

import java.io.Serializable;

import org.eclipse.egit.github.core.Repository;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class WithPermissionsRepository extends Repository {
	Permission permissions;

	@Data
	public static class Permission implements Serializable {
		boolean admin;
		boolean pull;
		boolean push;

		private static final long serialVersionUID = 8935105985140617959L;
	}

	private static final long serialVersionUID = 7009511040709634862L;
}
