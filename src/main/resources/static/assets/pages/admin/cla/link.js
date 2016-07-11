/*
 * Copyright 2016 the original author or authors.
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

var adminClaLink = (function () {
	var adminClaLink = {};

	function initialize() {

		if (sessionStorage.getItem("repositories")) {
			$("#repositories").select2({
				data: JSON.parse(sessionStorage.getItem("repositories")),
				tokenSeparators: [',']
			});
		}
		else {
			reloadRepositoryList();
		}

		$('#reload-repo-list').click(reloadRepositoryList);
		$('#open-hook-urls').click(openDataUrls);
		$('#contributing-md-urls').click(openDataUrls);
		$('#contributing-adoc-urls').click(openDataUrls);
	};

	function openDataUrls() {
		var urls = $(this).data("urls").split(',');
		$.each(urls, function (i, u) {
			window.open(u);
		});
		return false;
	}

	function reloadRepositoryList() {
		$('#repositories').prop('disabled', true);
		$.getJSON('./link/repositories.json')
			.done(function (data) {
				$("#repositories").select2({
					data: data,
					tokenSeparators: [',']
				});
				sessionStorage.setItem("repositories", JSON.stringify(data));
				$('#repositories').removeProp('disabled');
			});
		return false;
	}

	// exports
	adminClaLink.initialize = initialize;
	adminClaLink.reloadRepositoryList = reloadRepositoryList;

	return adminClaLink;
})
();

adminClaLink.initialize();
