/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.pivotal.cla.security;

import io.pivotal.cla.data.User;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author Joe Grandja
 */
@EqualsAndHashCode(callSuper=true, exclude = {"newUser"})
public class GitHubOAuth2User extends User implements OAuth2User {
	private final Collection<? extends GrantedAuthority> authorities;
	private final Map<String, Object> attributes;
	private final boolean newUser;

	public GitHubOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
		this(authorities, attributes, false);
	}

	public GitHubOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, boolean newUser) {
		Assert.notEmpty(authorities, "authorities cannot be empty");
		Assert.notEmpty(attributes, "attributes cannot be empty");
		this.authorities = Collections.unmodifiableSet(new LinkedHashSet<>(authorities));
		this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
		this.newUser = newUser;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	@Override
	public String getName() {
		return this.getGitHubLogin();
	}

	boolean isNewUser() {
		return this.newUser;
	}
}