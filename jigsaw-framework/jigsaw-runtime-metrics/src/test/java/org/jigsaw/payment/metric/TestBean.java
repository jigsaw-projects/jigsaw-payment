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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月14日
 */
public class TestBean {
	private static final Logger logger = LoggerFactory.getLogger(TestBean.class);
	  @Timer("class-method")
      public void classMethod() {
      	logger.debug("classMethod called!");
      }

      @Timer(value = "logged-method")
      public void loggerMethod() {
      	logger.debug("loggerMethod called!");
      }

      @Timer("errorMethod")
      public void errorMethod() {
      	logger.debug("errorMethod called!");
          throw new RuntimeException("test exception");
      }

}
