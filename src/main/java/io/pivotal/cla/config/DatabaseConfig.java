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
package io.pivotal.cla.config;

import javax.sql.DataSource;

import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.Assert;

/**
 * @author Mark Paluch
 */
public abstract class DatabaseConfig {

	@Bean
	public abstract DataSource dataSource();

	protected void configureDataSource(org.apache.tomcat.jdbc.pool.DataSource dataSource) {
		dataSource.setMaxActive(20);
		dataSource.setMaxIdle(8);
		dataSource.setMinIdle(8);
		dataSource.setTestOnBorrow(true);
		dataSource.setTestOnReturn(false);
	}
}

@Configuration
@Profile(GitHubClaProfiles.LOCAL)
class LocalDatabaseConfig extends DatabaseConfig {

	@Bean
	public DataSource dataSource() {
		org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();

		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:mem:pivotalcla;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		dataSource.setValidationQuery("SELECT 1");

		configureDataSource(dataSource);

		return dataSource;
	}
}

@Configuration
@Profile(GitHubClaProfiles.LOCAL_MYSQL)
class LocalMysqlDatabaseConfig extends DatabaseConfig {

	@Bean
	public DataSource dataSource() {
		org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();

		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/pivotalcla");
		dataSource.setUsername("spring");
		dataSource.setPassword("password");
		dataSource.setValidationQuery("SELECT 1");

		configureDataSource(dataSource);

		return dataSource;
	}
}

@Configuration
@Profile(GitHubClaProfiles.CLOUDFOUNDRY)
class CloudFoundryDatabaseConfig extends DatabaseConfig {

	@Bean
	public Cloud cloud() {
		return new CloudFactory().getCloud();
	}

	@Bean
	public DataSource dataSource() {

		DataSource service = cloud().getSingletonServiceConnector(DataSource.class, null);
		Assert.isInstanceOf(org.apache.tomcat.jdbc.pool.DataSource.class, service);
		org.apache.tomcat.jdbc.pool.DataSource dataSource = (org.apache.tomcat.jdbc.pool.DataSource) service;

		dataSource.setValidationQuery("/* PING */ SELECT 1");
		configureDataSource(dataSource);
		return dataSource;
	}
}
