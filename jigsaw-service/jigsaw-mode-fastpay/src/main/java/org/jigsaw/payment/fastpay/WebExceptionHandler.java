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
package org.jigsaw.payment.fastpay;

import java.util.stream.Collectors;

import org.jigsaw.payment.model.StatusCode;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年9月1日
 */
@ControllerAdvice
public class WebExceptionHandler {
	@ExceptionHandler(BindException.class)
	@ResponseBody
	public String processValidationError(BindException ex) {

		StringBuffer result = new StringBuffer("{status:");
		result.append(StatusCode.BAD_DATA_FORMAT_VALUE).append("; errors:[");
		ex.getBindingResult().getFieldErrors().stream().forEach(error -> {
			result.append("{field:'").append(error.getField()).append("'; message:'").append(error.getDefaultMessage())
					.append("'};");
		});
		result.append("]}");
		return result.toString();
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseBody
	public String processArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		return ex.getMessage();
	}
}
