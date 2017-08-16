/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.web.spt.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.iqiyi.kiwi.utils.lock.LockUtil;
import com.iqiyi.pay.web.spt.annotation.DistributedLock;

/**
 * 实现分布式锁
 * 
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年6月30日
 */
@Aspect
@Component
public class DistributedLockAspect {
	
	private Logger logger = LoggerFactory.getLogger(DistributedLockAspect.class);
	
	@Around("@annotation(distributedLock)")
	public Object lockExe(ProceedingJoinPoint joinPoint, final DistributedLock distributedLock)
			throws Throwable {
		
		String lock = distributedLock.value();
		try {
			LockUtil.accquireLock(lock);
			return joinPoint.proceed();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			LockUtil.releaseLock(lock);
		}
		return null;
	}
}
