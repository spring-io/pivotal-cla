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
package io.pivotal.cla.data.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.mysema.query.types.expr.BooleanExpression;

import io.pivotal.cla.data.IndividualSignature;
import io.pivotal.cla.data.QIndividualSignature;
import io.pivotal.cla.data.User;

public interface IndividualSignatureRepository extends CrudRepository<IndividualSignature, Long>, QueryDslPredicateExecutor<IndividualSignature> {

	default IndividualSignature findSignaturesFor(User user,  String claName) {
		BooleanExpression eqClaName = QIndividualSignature.individualSignature.cla.name.eq(claName);
		BooleanExpression hasEmail = QIndividualSignature.individualSignature.email.in(user.getEmails());
		BooleanExpression hasGithubLogin = QIndividualSignature.individualSignature.githubLogin.eq(user.getGithubLogin());

		PageRequest limit = new PageRequest(0, 1);
		Page<IndividualSignature> results = findAll(eqClaName.and(hasEmail.or(hasGithubLogin)), limit);
		return results.getSize() == 0 ? null : results.getContent().get(0);
	}

	IndividualSignature findFirstByClaNameAndEmailInOrderByDateOfSignature(String claName, Set<String> emails);

	IndividualSignature findFirstByClaNameAndGithubLoginOrderByDateOfSignature(String claName, String githubLogin);

	List<IndividualSignature> findByEmailIn(Set<String> email);
}
