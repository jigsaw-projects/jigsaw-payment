/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.web.spt.passport;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson.JSONObject;
import com.iqiyi.pay.web.spt.mutable.MutableHttpServletRequest;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年7月8日
 */
public class PassportFilter extends OncePerRequestFilter {
	
	private Logger logger = LoggerFactory.getLogger(PassportFilter.class);
	
	private String url = "http://passport.qiyi.domain/apis/user/info.action";
	
	@Autowired
	RestTemplate template;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		logger.debug("do filter");
		MutableHttpServletRequest req = (MutableHttpServletRequest)request;
		String authcookie = request.getParameter("authcookie");
		if (authcookie == null) {
			if (req.getParameter("auth_user_id") != null) {
				return;
			}
			filterChain.doFilter(request, response);
			return;
		}
		try {
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.put("authcookie", Arrays.asList(authcookie));
			ResponseEntity<String> res = template.postForEntity(url, map, String.class);
			logger.info("passport return: {}", res.getBody());
			JSONObject json = JSONObject.parseObject(res.getBody());
			if ("A00000".equals(json.getString("code"))) {
				JSONObject userInfo = json.getJSONObject("data").getJSONObject("userinfo");
				String uid =  userInfo.getString("uid");
				String mobile = userInfo.getString("phone");
				String name = userInfo.getString("user_name");
				req.setParameter("auth_user_id", uid == null? "": uid);
				req.setParameter("auth_user_mobile", mobile == null? "" : mobile);
				req.setParameter("auth_user_name", name == null? "" : name);
				filterChain.doFilter(request, response);
			} else {
				writeToResponse(response, "passport" + json.getString("msg"));
			}
		} catch (Exception e) {
			logger.error("", e);
			writeSysError(response);
		}
	}
	

	private void writeToResponse(HttpServletResponse response, String msg) throws IOException {
		JSONObject json = new JSONObject();
		json.put("code", "ERR_PASSORT");
		json.put("msg", msg);
		response.setContentType("application/json; charset=utf-8");
		response.getWriter().println(json.toJSONString());
	}

	private void writeSysError(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		json.put("code", "ERR_SYSTEM");
		json.put("msg", "system error");
		response.setContentType("application/json; charset=utf-8");
		response.getWriter().println(json.toJSONString());
	}

}
