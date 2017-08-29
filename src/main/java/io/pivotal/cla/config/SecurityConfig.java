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
package io.pivotal.cla.config;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsUtils;

import io.pivotal.cla.data.User;
import io.pivotal.cla.security.GitHubAuthenticationEntryPoint;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	ClaOAuthConfig oauthConfig;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		AuthenticationEntryPoint entryPoint = entryPoint();
		AdminRequestedAccessDeniedHandler accessDeniedHandler = new AdminRequestedAccessDeniedHandler(entryPoint);
		http
			.requiresChannel()
				.requestMatchers(request -> request.getHeader("x-forwarded-port") != null).requiresSecure()
				.and()
			.exceptionHandling()
				.authenticationEntryPoint(entryPoint)
				.accessDeniedHandler(accessDeniedHandler)
				.and()
			.csrf()
				.ignoringAntMatchers("/github/hooks/**").and()
			.authorizeRequests()
				.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
				.mvcMatchers("/login/**", "/", "/about", "/faq").permitAll()
				.mvcMatchers("/view/**").permitAll()
				.mvcMatchers("/webjars/**", "/assets/**").permitAll()
				.mvcMatchers("/github/hooks/**").permitAll()
				.mvcMatchers("/admin","/admin/cla/link/**","/admin/help/**").hasRole("ADMIN")
				.mvcMatchers("/admin/**","/manage/**").hasRole("CLA_AUTHOR")
				.anyRequest().authenticated()
				.and()
			.logout()
				.logoutSuccessUrl("/?logout");
	}

	static class AdminRequestedAccessDeniedHandler implements AccessDeniedHandler {
		private AuthenticationEntryPoint entryPoint;

		private AccessDeniedHandler deniedHandler;

		private AdminRequestedAccessDeniedHandler(AuthenticationEntryPoint entryPoint) {
			AccessDeniedHandlerImpl deniedHandler = new AccessDeniedHandlerImpl();
			deniedHandler.setErrorPage("/error/403");
			this.deniedHandler = deniedHandler;
			this.entryPoint = entryPoint;
		}

		@Override
		public void handle(HttpServletRequest request, HttpServletResponse response,
				AccessDeniedException accessDeniedException) throws IOException, ServletException {
			User currentUser = getUser(SecurityContextHolder.getContext().getAuthentication());

			if (currentUser == null || currentUser.isAdminAccessRequested()) {
				deniedHandler.handle(request, response, accessDeniedException);
				return;
			}

			new HttpSessionRequestCache().saveRequest(request, response);
			entryPoint.commence(request, response,
					new InsufficientAuthenticationException("Additional OAuth Scopes required", accessDeniedException));
		}

		private User getUser(Authentication authentication) {

			if (authentication != null && authentication.getPrincipal() instanceof User) {
				return (User) authentication.getPrincipal();
			}

			return null;
		}
	}


	private AuthenticationEntryPoint entryPoint() {
		LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();
		entryPoints.put(new AntPathRequestMatcher("/github/hooks/**"), new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
		entryPoints.put(new AntPathRequestMatcher("/admin/**"), new GitHubAuthenticationEntryPoint(oauthConfig.getMain(), "user:email,repo:status,admin:repo_hook,admin:org_hook,read:org"));
		BasicAuthenticationEntryPoint basicEntryPoint = new BasicAuthenticationEntryPoint();
		basicEntryPoint.setRealmName("Pivotal CLA");
		entryPoints.put(new AntPathRequestMatcher("/manage/**"), basicEntryPoint);
		DelegatingAuthenticationEntryPoint entryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
		entryPoint.setDefaultEntryPoint(new GitHubAuthenticationEntryPoint(oauthConfig.getMain(), "user:email"));
		return entryPoint;
	}
}
