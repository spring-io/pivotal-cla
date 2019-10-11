package io.pivotal.cla.egit.github.core;

import java.io.Serializable;
import org.eclipse.egit.github.core.Repository;

public class WithPermissionsRepository extends Repository {
	private Permission permissions;


	public static class Permission implements Serializable {
		private boolean admin;
		private boolean pull;
		private boolean push;
		private static final long serialVersionUID = 8935105985140617959L;

		public Permission() {
		}

		public boolean isAdmin() {
			return this.admin;
		}

		public boolean isPull() {
			return this.pull;
		}

		public boolean isPush() {
			return this.push;
		}

		public void setAdmin(final boolean admin) {
			this.admin = admin;
		}

		public void setPull(final boolean pull) {
			this.pull = pull;
		}

		public void setPush(final boolean push) {
			this.push = push;
		}

		@java.lang.Override
		public boolean equals(final java.lang.Object o) {
			if (o == this) return true;
			if (!(o instanceof WithPermissionsRepository.Permission)) return false;
			final Permission other = (Permission) o;
			if (!other.canEqual((java.lang.Object) this)) return false;
			if (this.isAdmin() != other.isAdmin()) return false;
			if (this.isPull() != other.isPull()) return false;
			if (this.isPush() != other.isPush()) return false;
			return true;
		}

		protected boolean canEqual(final java.lang.Object other) {
			return other instanceof WithPermissionsRepository.Permission;
		}

		@java.lang.Override
		public int hashCode() {
			final int PRIME = 59;
			int result = 1;
			result = result * PRIME + (this.isAdmin() ? 79 : 97);
			result = result * PRIME + (this.isPull() ? 79 : 97);
			result = result * PRIME + (this.isPush() ? 79 : 97);
			return result;
		}

		@java.lang.Override
		public java.lang.String toString() {
			return "WithPermissionsRepository.Permission(admin=" + this.isAdmin() + ", pull=" + this.isPull() + ", push=" + this.isPush() + ")";
		}
	}

	private static final long serialVersionUID = 7009511040709634862L;

	public WithPermissionsRepository() {
	}

	public Permission getPermissions() {
		return this.permissions;
	}

	public void setPermissions(final Permission permissions) {
		this.permissions = permissions;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "WithPermissionsRepository(permissions=" + this.getPermissions() + ")";
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof WithPermissionsRepository)) return false;
		final WithPermissionsRepository other = (WithPermissionsRepository) o;
		if (!other.canEqual((java.lang.Object) this)) return false;
		final java.lang.Object this$permissions = this.getPermissions();
		final java.lang.Object other$permissions = other.getPermissions();
		if (this$permissions == null ? other$permissions != null : !this$permissions.equals(other$permissions)) return false;
		return true;
	}

	protected boolean canEqual(final java.lang.Object other) {
		return other instanceof WithPermissionsRepository;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $permissions = this.getPermissions();
		result = result * PRIME + ($permissions == null ? 43 : $permissions.hashCode());
		return result;
	}
}
