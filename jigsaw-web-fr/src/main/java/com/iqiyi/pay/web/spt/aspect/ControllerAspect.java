/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.web.spt.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 打印controller输入输出
 * 
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年7月5日
 */
@Aspect
@Component
public class ControllerAspect {
	
	private Logger logger = LoggerFactory.getLogger("controller.invoke.logger");

	@Pointcut("@annotation(com.iqiyi.pay.web.spt.annotation.ParamValid)")
	public void requestMapping(){}
	
	@Before("requestMapping()")
	public void logArgs(JoinPoint joinPoint) {
		if (logger.isDebugEnabled()) {
			String controller = getControllerMethodName(joinPoint);
			logger.debug("controller {} invoke:{}", controller, "args...");
		}
	}
	
	@AfterReturning(pointcut="requestMapping()", returning="retVal")
	public void logReturnVal(JoinPoint joinPoint, Object retVal) {
		if (logger.isDebugEnabled()) {
			String controller = getControllerMethodName(joinPoint);
			String retStr = retVal.toString();
			retStr = retStr.length() > 128? retStr.substring(0, 128)+"..." : retStr;
			logger.debug("controller {} return:{}", controller, retStr);
		}
	}
	
	@AfterThrowing(pointcut="requestMapping()", throwing="ex")
	public void logThrowing(JoinPoint joinPoint, Throwable ex) {
		if (logger.isDebugEnabled()) {
			String controller = getControllerMethodName(joinPoint);
//			logger.debug("controller {} throw:{}", controller, ex);
		}
	}
	
	private String getControllerMethodName(JoinPoint joinPoint) {
		String cls = joinPoint.getTarget().getClass().getSimpleName();
		String method = joinPoint.getSignature().getName();
		return cls + "." + method;
	}
	
}
