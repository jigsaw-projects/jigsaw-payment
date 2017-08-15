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
package com.googlecode.protobuf.format;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Message;


/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月15日
 */
@Configuration
public class ProtobufConfiguration {
	
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer configProtobufSerializer() {
		return new Jackson2ObjectMapperBuilderCustomizer() {

			@Override
			public void customize(
					Jackson2ObjectMapperBuilder builder) {
				builder.serializerByType(Message.class, new JsonSerializer<Message>(){

					@Override
					public void serialize(Message message, JsonGenerator generator,
							SerializerProvider provider) throws IOException {
						if(message == null)
							return;
						JsonJacksonFormat format = new JsonJacksonFormat();
						format.print(message, generator);
					}});
				
			}
		};
	}
}
