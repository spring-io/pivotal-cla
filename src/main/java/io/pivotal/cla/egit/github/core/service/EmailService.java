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
package io.pivotal.cla.egit.github.core.service;

import static org.eclipse.egit.github.core.client.IGitHubConstants.AUTH_TOKEN;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_EMAILS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_USER;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PagedRequest;
import org.eclipse.egit.github.core.service.GitHubService;
import org.eclipse.egit.github.core.util.EncodingUtils;

import com.google.gson.reflect.TypeToken;

import io.pivotal.cla.egit.github.core.Email;

public class EmailService extends GitHubService {

	public List<Email> getEmails() throws IOException {
		PagedRequest<Email> request = createPagedRequest();
		request.setUri(SEGMENT_USER + SEGMENT_EMAILS);
		request.setType(new TypeToken<List<Email>>() {
		}.getType());
		return getAll(request);
	}

	private EmailService(GitHubClient client) {
		super(client);
	}

	public static EmailService forOAuth(String token) {
		return new EmailService(new EmailGitHubClient().setOAuth2Token(token));
	}

	private static final class EmailGitHubClient extends GitHubClient {
		private String credentials;

		public GitHubClient setCredentials(String user, String password) {
			super.setCredentials(user, password);
			if (user != null && user.length() > 0 && password != null && password.length() > 0) {
				credentials = "Basic " //$NON-NLS-1$
						+ EncodingUtils.toBase64(user + ':' + password);
			} else {
				credentials = null;
			}
			return this;
		}

		@Override
		public GitHubClient setOAuth2Token(String token) {
			if (token != null && token.length() > 0) {
				credentials = AUTH_TOKEN + ' ' + token;
			} else {
				credentials = null;
			}
			return this;
		}

		@Override
		protected HttpURLConnection configureRequest(HttpURLConnection request) {
			if (credentials != null)
				request.setRequestProperty(HEADER_AUTHORIZATION, credentials);
			request.setRequestProperty(HEADER_USER_AGENT, USER_AGENT);
			return request;
		}
	}
}
