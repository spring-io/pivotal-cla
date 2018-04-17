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

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * @author Mark Paluch
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class SessionConfig {

	@Profile(GitHubClaProfiles.CLOUDFOUNDRY)
	@Bean
	public RedisConnectionFactory cloudRedisConnectionFactory() {
		CloudFactory cloudFactory = new CloudFactory();
		Cloud cloud = cloudFactory.getCloud();
		RedisConnectionFactory connectionFactory = cloud.getSingletonServiceConnector(RedisConnectionFactory.class, null);
		return connectionFactory;
	}

	@Bean
	@ConditionalOnMissingBean(RedisConnectionFactory.class)
	public RedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {

		LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();
		connectionFactory.setPort(redisProperties.getPort());
		connectionFactory.setHostName(redisProperties.getHost());
		connectionFactory.setPassword(redisProperties.getPassword());
		connectionFactory.setShutdownTimeout(0);

		return connectionFactory;
	}
}
