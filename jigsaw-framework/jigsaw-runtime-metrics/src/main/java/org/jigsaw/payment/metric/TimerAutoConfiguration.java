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

import java.text.SimpleDateFormat;

import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月14日
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class TimerAutoConfiguration {

	@Bean
	public TimerAspect timerAspect(GaugeService gaugeService,
			CounterService counterService, ObjectMapper mapper) {
		return new TimerAspect(gaugeService, counterService, mapper);
	}

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer configBuilder() {
		return new Jackson2ObjectMapperBuilderCustomizer() {

			@Override
			public void customize(
					Jackson2ObjectMapperBuilder builder) {
				builder.findModulesViaServiceLoader(true)
						.dateFormat(new SimpleDateFormat("yyyy-MM-dd"));
				
			}

		};
	}

}
