/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.web.spt.para.valid;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.iqiyi.pay.web.spt.annotation.Para;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年8月12日
 */
@Aspect
@Component
public class ParameterValidAspect {

	private Logger logger = LoggerFactory.getLogger(ParameterValidAspect.class);

	@Autowired
	private Validator validator;

	@Autowired
	HttpServletResponse response;

	@Around("@annotation(com.iqiyi.pay.web.spt.annotation.ParamValid)")
	public Object valid(ProceedingJoinPoint point) throws Throwable {
		MethodSignature ms = (MethodSignature) point.getSignature();
		Method m = ms.getMethod();
		Parameter[] parameters = m.getParameters();
		Object[] args = point.getArgs();
		
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			if (arg instanceof TypeMismatchException) {
				TypeMismatchException ex = (TypeMismatchException)arg;
				Para para = parameters[i].getAnnotation(Para.class);
				if (para != null) {
					para = AnnotationUtils.getAnnotation(para, Para.class);
					throw new ParamErrorexception(para.name(),ex.getMessage());
				}
			}
		}
		
		LocalValidatorFactoryBean validatorFactoryBean = (LocalValidatorFactoryBean) validator;
		ValidatorImpl validatorImpl = (ValidatorImpl) validatorFactoryBean
				.getValidator();
		Class<?>[] groups = new Class<?>[0];

		Set<ConstraintViolation<Object>> violations = validatorImpl
				.validateParameters(point.getTarget(), m, args, groups);
		if (violations.isEmpty()) {
			return point.proceed();
		}

		
		ConstraintViolation<Object> violation = violations.iterator().next();
		String pathString = violation.getPropertyPath().toString();
		String subPath = pathString.split("\\.")[1];
		Para para = null;
		for (Parameter p : parameters) {
			if (subPath.equals(p.getName())) {
				para = p.getAnnotation(Para.class);
				break;
			}
		}
		
		if (para != null) {
			para = AnnotationUtils.getAnnotation(para, Para.class);
			throw new ParamErrorexception(para.name(), violation.getMessage());
		}
		
		throw new RuntimeException();
	}

}
