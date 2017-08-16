/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年8月19日
 */

@Aspect
@Component
public class TimeTraceLoggerAspect {
	
	private Logger logger = LoggerFactory.getLogger(TimeTraceLoggerAspect.class);
	
	@Around("@annotation(trace)")
	public Object invoke(ProceedingJoinPoint joinPoint, final Trace trace) throws Throwable {
		long start = System.currentTimeMillis();
		
		Object ret = joinPoint.proceed();
		
		long end = System.currentTimeMillis();
		
		logger.info("trace-{}-{} ms", getOpName(joinPoint), end -start);
		
		return ret;
		
	}
	

	
	private String getOpName(JoinPoint joinPoint) {
		String cls = joinPoint.getTarget().getClass().getSimpleName();
		String method = joinPoint.getSignature().getName();
		return cls + "." + method;
	}
	
	
}
