/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.web.spt.aspect;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 打印执行时间
 * 
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年7月5日
 */
public class ExeTimeRecordFilter extends OncePerRequestFilter {
	
	Logger logger = LoggerFactory.getLogger(ExeTimeRecordFilter.class);
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		long start = System.currentTimeMillis();
		try {
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			long end = System.currentTimeMillis();
			logger.error("{}:{}", request.getRequestURI(), end-start);
			throw e;
		}
		long end = System.currentTimeMillis();
		logger.info("{}:{}", request.getRequestURI(), end-start);
	}
}
