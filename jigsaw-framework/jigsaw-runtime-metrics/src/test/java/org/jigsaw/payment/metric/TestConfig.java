/*
 * Copyright 2012-2017 the original author or authors.
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
package org.jigsaw.payment.metric;

import org.mockito.Mockito;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月14日
 */
@Configuration
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class TestConfig {
	@Bean
	public TestBean testBean() {
		return new TestBean();
	}

	@Bean
	public TestInterface testBeanWithInterface() {
		return new TestBeanWithInterface();
	}

	@Bean
	@Primary
	public CounterService counterService() {
		return Mockito.mock(CounterService.class);
	}

}
