/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */

package com.iqiyi.pay.web.spt.sign;

import com.alibaba.fastjson.JSONObject;
import com.iqiyi.pay.web.spt.annotation.Para;
import com.iqiyi.pay.web.spt.annotation.Sign;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * 实现参数验签
 * 
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * 2016年6月3日
 */
public class ParameterSignHandlerInterceptor extends HandlerInterceptorAdapter {
	
	private Logger logger = LoggerFactory.getLogger("params.sign.logger");

	


	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		HandlerMethod hm = (HandlerMethod)handler;
		Sign sign = hm.getMethodAnnotation(Sign.class);
		if (sign == null) {
			return true;
		}
		
		Map<String, String> map = new HashMap<>();
		for (MethodParameter mp : hm.getMethodParameters()) {
			Para p = mp.getParameterAnnotation(Para.class);
			if (p == null) {
				continue;
			}
			String paraName = p.name();
			map.put(paraName, request.getParameter(paraName));
		}
		
		if (!map.containsKey("sign")) {
			map.put("sign", request.getParameter("sign"));
		}
		String signKey = request.getParameter("authcookie");
		if (!doCheckSign(map, signKey)) {
			writeToResponse(response);
			return false;
		}
		return true;
	}

	private void writeToResponse(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		json.put("code", "ERR00003");
		json.put("msg", "sign not match");
		response.setContentType("application/json; charset=utf-8");
		response.getWriter().println(json.toJSONString());
	}
	

	
	
	public boolean doCheckSign(Map<String, String> params, String key) {
		String sign = params.get("sign");
		logger.debug("request sign: {}", sign);
		String curSign = md5Signature(params, key);
		logger.debug("compute sign: {}", curSign);
		return curSign.equals(sign);
	}
	
	private  String md5Signature(Map<String, String> params, String secret) {
		Set<String> set = params.keySet();
		String[] keys = new String[set.size()];
		set.toArray(keys);
		Arrays.sort(keys);

		StringBuffer buffer = new StringBuffer();
		for (String key : keys) {
			String val = params.get(key);
			if (StringUtils.isBlank(val) || "sign".equals(key)) {
				continue;
			}
			buffer.append(key).append("=").append(val).append("&");
		}
		if (buffer.length() > 0 && buffer.charAt(buffer.length() - 1) == '&') {
			buffer.deleteCharAt(buffer.length() - 1);
		}
		buffer.append(secret);
		try {
			String linkedString = buffer.toString();
			logger.debug("linked params: {}", linkedString);
			return DigestUtils.md5Hex(linkedString.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		return null;
	}
}
