/*
 * Copyright 2002-2016 the original author or authors.
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
package io.pivotal.cla;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import io.pivotal.cla.security.GithubAuthenticationEntryPoint;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	ClaOAuthConfig oauthConfig;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		AuthenticationEntryPoint entryPoint = entryPoint();
		http
		.exceptionHandling()
			.authenticationEntryPoint(entryPoint)
			.accessDeniedHandler( (request, response, accessDeniedException) -> {
					new HttpSessionRequestCache().saveRequest(request, response);
					entryPoint.commence(request, response, new InsufficientAuthenticationException("Additional OAuth Scopes required", accessDeniedException));
				})
			.and()
		.csrf()
			.ignoringAntMatchers("/github/hooks/**").and()
		.authorizeRequests()
			.antMatchers("/login/**", "/github/hooks/**").permitAll().antMatchers("/admin/**").hasRole("ADMIN")
			.anyRequest().authenticated();
	}

	private AuthenticationEntryPoint entryPoint() {
		LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();
		entryPoints.put(new AntPathRequestMatcher("/admin/**"), new GithubAuthenticationEntryPoint(oauthConfig.getAdmin(), "user:email,repo:status,admin:repo_hook,admin:org_hook,read:org"));
		DelegatingAuthenticationEntryPoint entryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
		entryPoint.setDefaultEntryPoint(new GithubAuthenticationEntryPoint(oauthConfig.getMain(), "user:email"));
		return entryPoint;
	}
}
