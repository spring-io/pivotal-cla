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
package io.pivotal.cla.data.repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import io.pivotal.cla.data.CorporateSignature;

public interface CorporateSignatureRepository extends CrudRepository<CorporateSignature, Long> {

	/**
	 * Works around https://hibernate.atlassian.net/browse/HHH-8091
	 */
	public static final List<String> EMPTY_LIST_FOR_QUERY = Collections.singletonList("");

	default CorporateSignature findOne(Long id) {
		return findById(id).orElse(null);
	}

	default CorporateSignature findSignature(String claName, Collection<String> organizations, Collection<String> emails) {
		PageRequest pageable = new PageRequest(0, 1);
		List<String> emailDomains = emails == null || emails.isEmpty() ? EMPTY_LIST_FOR_QUERY : emails.stream().map( e-> e.substring(e.lastIndexOf("@") + 1)).collect(Collectors.toList());
		organizations = organizations.isEmpty() ? EMPTY_LIST_FOR_QUERY : organizations;
		List<CorporateSignature> results = findSignatures(pageable, claName, organizations, emailDomains);
		return results.isEmpty() ? null : results.get(0);
	}

	default List<CorporateSignature> findSignatures(PageRequest pageable, Collection<String> organizations, Collection<String> emails) {
		List<String> emailDomains = emails == null || emails.isEmpty() ? EMPTY_LIST_FOR_QUERY : emails.stream().map( e-> e.substring(e.lastIndexOf("@") + 1)).collect(Collectors.toList());
		organizations = organizations.isEmpty() ? EMPTY_LIST_FOR_QUERY : organizations;
		return findSignaturesByOrganizationsAndEmailDomains(pageable, organizations, emailDomains);
	}

	@Query("select s from CorporateSignature s where (s.cla.name = :claName or s.cla.name in (select distinct c.supersedingCla.name from ContributorLicenseAgreement c where c.name = :#{#claName})) and (s.gitHubOrganization in (:organizations) or s.emailDomain in (:emailDomains))")
	List<CorporateSignature> findSignatures(Pageable pageable, @Param("claName") String claName, @Param("organizations") Collection<String> organizations, @Param("emailDomains") Collection<String> emailDomains);

	@Query("select s from CorporateSignature s where (s.gitHubOrganization in (:organizations) or s.emailDomain in (:emailDomains))")
	List<CorporateSignature> findSignaturesByOrganizationsAndEmailDomains(Pageable pageable, @Param("organizations") Collection<String> organizations, @Param("emailDomains") Collection<String> emailDomains);

	// part of github organization
	// has email that ends with @domain
}
