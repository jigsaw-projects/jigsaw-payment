/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.web.spt.whitebox;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.iqiyi.pay.web.spt.annotation.WhiteBox;



/**
 * 白盒加密安全拦截器 （防止客户端绕过白盒）
 * 
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年7月5日
 */
public class WhiteBoxInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		HandlerMethod hm = (HandlerMethod)handler;
		if (hm.getMethodAnnotation(WhiteBox.class) == null) {
			return true;
		}
		
		return  request.getParameter("w_h") != null && request.getParameter("content") != null;
	}
	
	
}
