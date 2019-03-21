/*
 * Copyright 2016 the original author or authors.
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
package io.pivotal.cla.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * @author Mark Paluch
 * @author Rob Winch
 */

@Configuration
@Profile(GitHubClaProfiles.LOCAL)
class LocalDatabaseConfig {

	@Bean
	public DataSource dataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setJdbcUrl("jdbc:h2:mem:pivotalcla;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}
}

@Configuration
@Profile(GitHubClaProfiles.LOCAL_MYSQL)
class LocalMysqlDatabaseConfig {

	@Bean
	public DataSource dataSource() {
		HikariDataSource dataSource = new HikariDataSource();

		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/pivotalcla");
		dataSource.setUsername("spring");
		dataSource.setPassword("password");
		return dataSource;
	}
}

@Configuration
@Profile(GitHubClaProfiles.CLOUDFOUNDRY)
class CloudFoundryDatabaseConfig {

	@Bean
	public Cloud cloud() {
		return new CloudFactory().getCloud();
	}

	@Bean
	public DataSource dataSource() {
		return cloud().getSingletonServiceConnector(DataSource.class, null);
	}
}
