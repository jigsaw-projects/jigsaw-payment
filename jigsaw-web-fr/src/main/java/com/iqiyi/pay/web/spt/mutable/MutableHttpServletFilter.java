/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.web.spt.mutable;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年7月8日
 */
public class MutableHttpServletFilter extends OncePerRequestFilter {
	
	private Logger logger = LoggerFactory.getLogger(MutableHttpServletFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		request = new MutableHttpServletRequest(request);
		MutableHttpServletResponse mresponse = new MutableHttpServletResponse(response);
		filterChain.doFilter(request, mresponse);
		String data = mresponse.getResponseData();
		response.setContentLength(data.getBytes("utf-8").length);
		response.getWriter().println(data);
	}

}
