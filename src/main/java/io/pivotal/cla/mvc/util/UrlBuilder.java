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
package io.pivotal.cla.mvc.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.Builder;

/**
 * @author Rob Winch
 *
 */
public class UrlBuilder {

	private final HttpServletRequest request;

	private String path;

	private MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

	private UrlBuilder(HttpServletRequest request) {
		super();
		this.request = request;
	}

	public String callbackUrl() {
		return path("/login/oauth2/github").build();
	}

	public UrlBuilder param(String name, String value) {
		this.params.add(name, value);
		return this;
	}

	public UrlBuilder path(String path) {
		this.path = path;
		return this;
	}

	@Builder(builderMethodName="signUrl")
	private static String create(HttpServletRequest request, String claName, String repositoryId, int pullRequestId) {
		UrlBuilder url = UrlBuilder
				.fromRequest(request)
				.path("/sign/"+claName)
				.param("repositoryId", repositoryId)
				.param("pullRequestId", String.valueOf(pullRequestId));
		return url.build();
	}

	public String build() {
		String url = UriComponentsBuilder
				.fromHttpRequest(new ServletServerHttpRequest(request))
				.replacePath(path)
				.replaceQueryParams(params)
				.build()
				.toUriString();

		this.path = null;
		this.params.clear();
		if (url.contains("ngrok.io")) {
			url = url.replaceFirst(":80", "");
			url = url.replaceFirst("http:", "https:");
		}
		return url;
	}

	public static UrlBuilder fromRequest(HttpServletRequest request) {
		return new UrlBuilder(request);
	}
}
