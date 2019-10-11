package io.pivotal.cla.egit.github.core.service;

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_USER;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.client.PagedRequest;
import org.eclipse.egit.github.core.service.RepositoryService;

import com.google.gson.reflect.TypeToken;

import io.pivotal.cla.egit.github.core.WithPermissionsRepository;

public class WithPermissionsRepositoryService extends RepositoryService {

	public WithPermissionsRepositoryService() {
		super();
	}

	public WithPermissionsRepositoryService(GitHubClient client) {
		super(client);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<WithPermissionsRepository> getPermissionRepositories() {
		try {
			List result = getRepositories();
			return result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Page repositories for currently authenticated user
	 *
	 * @param filterData
	 * @param start
	 * @param size
	 * @return iterator over pages of repositories
	 */
	public PageIterator<Repository> pageRepositories(
			Map<String, String> filterData, int start, int size) {
		PagedRequest<Repository> request = createPagedRequest(start, size);
		request.setUri(SEGMENT_USER + SEGMENT_REPOS);
		request.setParams(filterData);
		request.setType(new TypeToken<List<WithPermissionsRepository>>() {
		}.getType());
		return createPageIterator(request);
	}
}
