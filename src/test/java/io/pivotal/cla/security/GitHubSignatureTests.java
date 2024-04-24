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
package io.pivotal.cla.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.pivotal.cla.data.AccessToken;
import io.pivotal.cla.data.repository.AccessTokenRepository;

/**
 * @author Rob Winch
 *
 */
@ExtendWith(MockitoExtension.class)
public class GitHubSignatureTests {
	@Mock
	AccessTokenRepository mockAccessTokens;

	GitHubSignature gitHubSignature;

	AccessToken token;

	@BeforeEach
	public void setup() {
		gitHubSignature = new GitHubSignature(mockAccessTokens);
		token = new AccessToken();
		token.setToken("bff3b811-ea31-437b-8083-e451c50f0f99");
	}

	@Test
	public void checkSignatureSuccess() throws Exception {
		when(mockAccessTokens.findOne(anyString())).thenReturn(token);
		String body = "{\"zen\":\"Speak like a human.\",\"hook_id\":7971389,\"hook\":{\"type\":\"Repository\",\"id\":7971389,\"name\":\"web\",\"active\":true,\"events\":[\"pull_request\"],\"config\":{\"content_type\":\"json\",\"url\":\"https://0d7d114e.ngrok.io/github/hooks/pull_request/pivotal?access_token=bff3b811-ea31-437b-8083-e451c50f0f99\"},\"updated_at\":\"2016-04-05T15:53:26Z\",\"created_at\":\"2016-04-05T15:53:26Z\",\"url\":\"https://api.github.com/repos/rwinch/cla-test/hooks/7971389\",\"test_url\":\"https://api.github.com/repos/rwinch/cla-test/hooks/7971389/test\",\"ping_url\":\"https://api.github.com/repos/rwinch/cla-test/hooks/7971389/pings\",\"last_response\":{\"code\":null,\"status\":\"unused\",\"message\":null}},\"repository\":{\"id\":55009549,\"name\":\"cla-test\",\"full_name\":\"rwinch/cla-test\",\"owner\":{\"login\":\"rwinch\",\"id\":362503,\"avatar_url\":\"https://avatars.githubusercontent.com/u/362503?v=3\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/rwinch\",\"html_url\":\"https://github.com/rwinch\",\"followers_url\":\"https://api.github.com/users/rwinch/followers\",\"following_url\":\"https://api.github.com/users/rwinch/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/rwinch/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/rwinch/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/rwinch/subscriptions\",\"organizations_url\":\"https://api.github.com/users/rwinch/orgs\",\"repos_url\":\"https://api.github.com/users/rwinch/repos\",\"events_url\":\"https://api.github.com/users/rwinch/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/rwinch/received_events\",\"type\":\"User\",\"site_admin\":false},\"private\":false,\"html_url\":\"https://github.com/rwinch/cla-test\",\"description\":\"\",\"fork\":false,\"url\":\"https://api.github.com/repos/rwinch/cla-test\",\"forks_url\":\"https://api.github.com/repos/rwinch/cla-test/forks\",\"keys_url\":\"https://api.github.com/repos/rwinch/cla-test/keys{/key_id}\",\"collaborators_url\":\"https://api.github.com/repos/rwinch/cla-test/collaborators{/collaborator}\",\"teams_url\":\"https://api.github.com/repos/rwinch/cla-test/teams\",\"hooks_url\":\"https://api.github.com/repos/rwinch/cla-test/hooks\",\"issue_events_url\":\"https://api.github.com/repos/rwinch/cla-test/issues/events{/number}\",\"events_url\":\"https://api.github.com/repos/rwinch/cla-test/events\",\"assignees_url\":\"https://api.github.com/repos/rwinch/cla-test/assignees{/user}\",\"branches_url\":\"https://api.github.com/repos/rwinch/cla-test/branches{/branch}\",\"tags_url\":\"https://api.github.com/repos/rwinch/cla-test/tags\",\"blobs_url\":\"https://api.github.com/repos/rwinch/cla-test/git/blobs{/sha}\",\"git_tags_url\":\"https://api.github.com/repos/rwinch/cla-test/git/tags{/sha}\",\"git_refs_url\":\"https://api.github.com/repos/rwinch/cla-test/git/refs{/sha}\",\"trees_url\":\"https://api.github.com/repos/rwinch/cla-test/git/trees{/sha}\",\"statuses_url\":\"https://api.github.com/repos/rwinch/cla-test/statuses/{sha}\",\"languages_url\":\"https://api.github.com/repos/rwinch/cla-test/languages\",\"stargazers_url\":\"https://api.github.com/repos/rwinch/cla-test/stargazers\",\"contributors_url\":\"https://api.github.com/repos/rwinch/cla-test/contributors\",\"subscribers_url\":\"https://api.github.com/repos/rwinch/cla-test/subscribers\",\"subscription_url\":\"https://api.github.com/repos/rwinch/cla-test/subscription\",\"commits_url\":\"https://api.github.com/repos/rwinch/cla-test/commits{/sha}\",\"git_commits_url\":\"https://api.github.com/repos/rwinch/cla-test/git/commits{/sha}\",\"comments_url\":\"https://api.github.com/repos/rwinch/cla-test/comments{/number}\",\"issue_comment_url\":\"https://api.github.com/repos/rwinch/cla-test/issues/comments{/number}\",\"contents_url\":\"https://api.github.com/repos/rwinch/cla-test/contents/{+path}\",\"compare_url\":\"https://api.github.com/repos/rwinch/cla-test/compare/{base}...{head}\",\"merges_url\":\"https://api.github.com/repos/rwinch/cla-test/merges\",\"archive_url\":\"https://api.github.com/repos/rwinch/cla-test/{archive_format}{/ref}\",\"downloads_url\":\"https://api.github.com/repos/rwinch/cla-test/downloads\",\"issues_url\":\"https://api.github.com/repos/rwinch/cla-test/issues{/number}\",\"pulls_url\":\"https://api.github.com/repos/rwinch/cla-test/pulls{/number}\",\"milestones_url\":\"https://api.github.com/repos/rwinch/cla-test/milestones{/number}\",\"notifications_url\":\"https://api.github.com/repos/rwinch/cla-test/notifications{?since,all,participating}\",\"labels_url\":\"https://api.github.com/repos/rwinch/cla-test/labels{/name}\",\"releases_url\":\"https://api.github.com/repos/rwinch/cla-test/releases{/id}\",\"deployments_url\":\"https://api.github.com/repos/rwinch/cla-test/deployments\",\"created_at\":\"2016-03-29T20:49:20Z\",\"updated_at\":\"2016-03-29T20:49:20Z\",\"pushed_at\":\"2016-03-29T20:53:58Z\",\"git_url\":\"git://github.com/rwinch/cla-test.git\",\"ssh_url\":\"git@github.com:rwinch/cla-test.git\",\"clone_url\":\"https://github.com/rwinch/cla-test.git\",\"svn_url\":\"https://github.com/rwinch/cla-test\",\"homepage\":null,\"size\":0,\"stargazers_count\":0,\"watchers_count\":0,\"language\":null,\"has_issues\":true,\"has_downloads\":true,\"has_wiki\":true,\"has_pages\":false,\"forks_count\":1,\"mirror_url\":null,\"open_issues_count\":1,\"forks\":1,\"open_issues\":1,\"watchers\":0,\"default_branch\":\"main\"},\"sender\":{\"login\":\"rwinch\",\"id\":362503,\"avatar_url\":\"https://avatars.githubusercontent.com/u/362503?v=3\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/rwinch\",\"html_url\":\"https://github.com/rwinch\",\"followers_url\":\"https://api.github.com/users/rwinch/followers\",\"following_url\":\"https://api.github.com/users/rwinch/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/rwinch/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/rwinch/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/rwinch/subscriptions\",\"organizations_url\":\"https://api.github.com/users/rwinch/orgs\",\"repos_url\":\"https://api.github.com/users/rwinch/repos\",\"events_url\":\"https://api.github.com/users/rwinch/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/rwinch/received_events\",\"type\":\"User\",\"site_admin\":false}}";
		assertThat(gitHubSignature.check("sha1=26b4be3b0ddaa9cb2894ff197029e132d716a572", body)).isTrue();
	}

}
