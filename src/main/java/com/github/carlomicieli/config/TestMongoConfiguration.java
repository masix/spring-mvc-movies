/*
Copyright [2012] [Carlo P. Micieli]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.github.carlomicieli.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.mock.web.MockServletContext;

import com.mongodb.Mongo;

/**
 * Configuration class to inject the mongo instance for testing.
 * 
 * @author Carlo P. Micieli
 * @since 1.0.0
 */
@Configuration
@Profile("test")
@ImportResource("classpath:/META-INF/spring/properties-config.xml")
public class TestMongoConfiguration {
	private @Value("${test.mongo.databaseName}") String databaseName;
	private @Value("${test.mongo.hostName}") String hostName;
	private @Value("${test.mongo.portNumber}") int portNumber;

	public @Bean MongoDbFactory mongoDbFactory() throws Exception {
		return new SimpleMongoDbFactory(new Mongo(hostName, portNumber), databaseName);
	}
	
	public @Bean MockServletContext mockServletContext() {
		//need for the spring junit runner.
		return new MockServletContext();
	}
}
