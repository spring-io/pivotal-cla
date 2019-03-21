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
package io.pivotal.cla.mvc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import io.pivotal.cla.data.User;
import io.pivotal.cla.mvc.ClaRequest;
import io.pivotal.cla.mvc.support.ImportedSignaturesSessionAttr;
import io.pivotal.cla.service.ClaPullRequestStatusRequest;
import io.pivotal.cla.service.ClaService;

@ControllerAdvice
public class UserControllerAdvice {

	@Autowired
	ClaService claService;

	@ModelAttribute("currentUser")
	User currentUser(@AuthenticationPrincipal User currentUser) {
		return currentUser;
	}

	@ModelAttribute("isAdmin")
	boolean isAdmin(@AuthenticationPrincipal User currentUser) {
		return currentUser == null ? false : currentUser.isAdmin();
	}

	@ModelAttribute("isClaAuthor")
	boolean isClaAuthor(@AuthenticationPrincipal User currentUser) {
		return currentUser == null ? false : currentUser.isClaAuthor();
	}

	@ModelAttribute("importedSignature")
	boolean importedSignature(@AuthenticationPrincipal User currentUser, ImportedSignaturesSessionAttr importedSignaturesSessionAttr, @ModelAttribute ClaRequest claRequest) throws Exception {
		if(!importedSignaturesSessionAttr.getValue()) {
			return false;
		}
		ClaPullRequestStatusRequest updatePullRequest = claRequest.createUpdatePullRequestStatus(currentUser.getGitHubLogin());
		if(updatePullRequest != null) {
			claService.savePullRequestStatus(updatePullRequest);
		}
		importedSignaturesSessionAttr.setValue(false);
		return true;
	}
}
