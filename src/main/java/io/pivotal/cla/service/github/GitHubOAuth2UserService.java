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
package io.pivotal.cla.service.github;

import io.pivotal.cla.config.ClaOAuthConfig;
import io.pivotal.cla.data.User;
import io.pivotal.cla.data.repository.UserRepository;
import io.pivotal.cla.security.GitHubOAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Joe Grandja
 */
public class GitHubOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private final OAuth2UserService<OAuth2UserRequest, OAuth2User> defaultUserService = new DefaultOAuth2UserService();

	private final RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private GitHubApi gitHubApi;

	@Autowired
	private ClaOAuthConfig oauthConfig;

	@Autowired
	private UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oauth2User = this.defaultUserService.loadUser(userRequest);

		Map<String, Object> userAttributes = oauth2User.getAttributes();
		String login = userAttributes.containsKey("login") ? userAttributes.get("login").toString() : null;
		String name = userAttributes.containsKey("name") ? userAttributes.get("name").toString() : null;
		String avatarUrl = userAttributes.containsKey("avatar_url") ? userAttributes.get("avatar_url").toString() : null;

		OAuth2AccessToken accessToken = userRequest.getAccessToken();

		Set<String> verifiedEmails = this.gitHubApi.getVerifiedEmails(accessToken.getTokenValue());

		boolean hasAdminEmail = verifiedEmails.stream().anyMatch(e -> e.endsWith(MylynGitHubApi.ADMIN_MAIL_SUFFIX));
		boolean hasAdminAccess = accessToken.getScopes().stream().anyMatch(s -> s.equals("repo:status"));
		boolean isAdmin = hasAdminEmail && hasAdminAccess;
		boolean isClaAuthor = isAdmin && this.isAuthor(login, accessToken.getTokenValue());

		User user = new User();
		user.setFullName(name);
		user.setAccessToken(accessToken.getTokenValue());
		user.setAvatarUrl(avatarUrl);
		user.setEmails(new TreeSet<>(verifiedEmails));
		user.setGitHubLogin(login);
		user.setAdminAccessRequested(hasAdminAccess);
		user.setAdmin(isAdmin);
		user.setClaAuthor(isClaAuthor);

		User existingUser = this.userRepository.findOne(user.getGitHubLogin());
		boolean isNewUser = existingUser == null;
		this.userRepository.save(user);

		List<GrantedAuthority> authorities;
		if (isClaAuthor) {
			authorities = AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN", "ROLE_CLA_AUTHOR", "ACTUATOR");
		} else if (isAdmin) {
			authorities = AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN");
		} else {
			authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
		}

		GitHubOAuth2User gitHubUser = new GitHubOAuth2User(authorities, userAttributes, isNewUser);
		gitHubUser.setFullName(user.getFullName());
		gitHubUser.setAccessToken(user.getAccessToken());
		gitHubUser.setAvatarUrl(user.getAvatarUrl());
		gitHubUser.setEmails(user.getEmails());
		gitHubUser.setGitHubLogin(user.getGitHubLogin());
		gitHubUser.setAdminAccessRequested(user.isAdminAccessRequested());
		gitHubUser.setAdmin(user.isAdmin());
		gitHubUser.setClaAuthor(user.isClaAuthor());

		return gitHubUser;
	}

	private boolean isAuthor(String username, String accessToken) {
		try {
			ResponseEntity<String> entity = this.restTemplate.getForEntity(
					this.oauthConfig.getGitHubApiBaseUrl() + "/teams/{id}/memberships/{username}?access_token={token}",
					String.class, "2006839", username, accessToken);
			return entity.getStatusCode().value() == 200;
		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
				return false;
			}
			throw ex;
		}
	}
}