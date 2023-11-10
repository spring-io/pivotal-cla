/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.pivotal.cla.mvc.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Rob Winch
 */
public class UrlBuilder {
	private final HttpServletRequest request;
	private String path;
	private MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

	private UrlBuilder(HttpServletRequest request) {
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

	private static String createSignUrl(HttpServletRequest request, String claName, String repositoryId, int pullRequestId) {
		String urlEncodedClaName = urlEncode(claName);
		UrlBuilder url =  UrlBuilder.fromRequest(request)
				.path("/sign/" + urlEncodedClaName)
				.param("repositoryId", repositoryId)
				.param("pullRequestId", String.valueOf(pullRequestId));
		return url.build();
	}

	public static String createSyncUrl(HttpServletRequest request, String claName, String repositoryId, int pullRequestId) {
			String urlEncodedClaName = urlEncode(claName);
			UrlBuilder url =  UrlBuilder.fromRequest(request)
				.path("/sync/" + urlEncodedClaName)
					.param("repositoryId", repositoryId)
					.param("pullRequestId", String.valueOf(pullRequestId));
			return url.build();
	}

	private static String urlEncode(String v) {
		try {
			return URLEncoder.encode(v, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	public static String createAboutUrl(HttpServletRequest request) {
		UrlBuilder url = UrlBuilder.fromRequest(request)
			.path("/about");
		return url.build();
	}

	public String build() {
		String url =  UriComponentsBuilder.fromHttpRequest(new ServletServerHttpRequest(request))
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

	public static String pullRequestHookCallbackPath(String claName) {
		String urlEncodedClaName = urlEncode(claName);
		return String.format("%s/%s", "/github/hooks/pull_request", urlEncodedClaName);
	}


	public static class StringBuilder {
		private HttpServletRequest request;
		private String claName;
		private String repositoryId;
		private int pullRequestId;

		StringBuilder() {
		}

		public StringBuilder request(final HttpServletRequest request) {
			this.request = request;
			return this;
		}

		public StringBuilder claName(final String claName) {
			this.claName = claName;
			return this;
		}

		public StringBuilder repositoryId(final String repositoryId) {
			this.repositoryId = repositoryId;
			return this;
		}

		public StringBuilder pullRequestId(final int pullRequestId) {
			this.pullRequestId = pullRequestId;
			return this;
		}

		public String build() {
			return UrlBuilder.createSignUrl(request, claName, repositoryId, pullRequestId);
		}

		@java.lang.Override
		public java.lang.String toString() {
			return "UrlBuilder.StringBuilder(request=" + this.request + ", claName=" + this.claName + ", repositoryId=" + this.repositoryId + ", pullRequestId=" + this.pullRequestId + ")";
		}
	}

	public static StringBuilder signUrl() {
		return new StringBuilder();
	}
}
