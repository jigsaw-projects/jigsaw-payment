/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */

package com.iqiyi.pay.web.spt.whitebox;

import java.io.File;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iqiyi.pay.web.spt.mutable.MutableHttpServletRequest;
import com.iqiyi.pay.web.spt.mutable.MutableHttpServletResponse;
import com.iqiyi.security.whitebox.CryptoToolbox;
import com.iqiyi.security.whitebox.WhiteBoxException;

/**
 * 实现白盒解密
 * 
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年6月20日
 */
public class WhilteBoxFilter extends OncePerRequestFilter {
	
	private Logger logger = LoggerFactory.getLogger(WhilteBoxFilter.class);
	
	@Autowired
	WhiteboxService wbs;
	
	@Value("${spring.profiles.active:test}")
	private String profile;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String content = request.getParameter("content");//白盒加密内容
		String whiteBoxVersion = request.getParameter("w_h");// 白盒加密版本
		String platform = request.getParameter("platform");
		if (whiteBoxVersion == null || content == null) {
			chain.doFilter(request, response);
			return;
		}
		
		if (!profile.contains("prod")) {
			StringBuffer sb = new StringBuffer();
			sb.append(request.getRequestURL()).append("?");
			sb.append("content").append("=").append(content).append("&");
			sb.append("w_h").append("=").append(whiteBoxVersion).append("&");
			if (platform != null) {
				sb.append("platform").append("=").append(platform).append("&");
			}
			sb.deleteCharAt(sb.length()-1);
			logger.info("white_box_in: {}", sb.toString());
		}
		String key = wbs.getAESDecryptKey(whiteBoxVersion, platform);// 获取对应的key
		File rsaServerPri = wbs.getRsaServerPriFile(whiteBoxVersion, platform);// 获取证书
		String plain = "";
		try {
			plain = CryptoToolbox.decryptData(content, key, rsaServerPri);
		} catch (WhiteBoxException e) {
			logger.error("", e);
			writeToResponse(response);
			return;
		}
		if (CryptoToolbox.ERROR_SIGNATURE_NOT_MATCH.equals(plain)) {
			writeToResponse(response);
			return;
		}
		MutableHttpServletRequest req = (MutableHttpServletRequest)request;
		req.setParameter("w_platform", req.getParameter("platform"));
		req.deleteParameter("platform");
		JSONObject json = JSON.parseObject(plain);
		for (String k : json.keySet()) {
			String v = json.getString(k.toString());
			if (v.startsWith("{") && v.endsWith("}")) {
				v = decryptPwd(v, key);
			}
			req.setParameter(k, v);
		}
		
		printParameters(request);
		chain.doFilter(req, response);
		String encResponse = req.getParameter("enc_response");
		if (encResponse != null && Boolean.parseBoolean(encResponse)) {
			MutableHttpServletResponse mres = (MutableHttpServletResponse)response;
			String resStr = mres.getResponseData();
			if (resStr != null) {
				String enKey = wbs.getAESEncryptKey(whiteBoxVersion, platform);// 获取对应的key
				resStr = CryptoToolbox.aesEncryptData(resStr, enKey);
				mres.setResponseData(resStr);
			}
		}
	}
	
	
	 private String decryptPwd(String content, String key) {
		 	content = content.replace("{", "").replace("}", "");
	    	String[] chars = content.split(",");
	    	StringBuffer sb = new StringBuffer();
	    	for (String c : chars) {
	    		c = CryptoToolbox.aesDecryptData(c, key);
	    		sb.append(c.split("#")[0]);
	    	}
			return sb.toString();
		}
	
	private void writeToResponse(HttpServletResponse response) throws IOException {
		JSONObject json = new JSONObject();
		json.put("code", "ERR00000");
		json.put("msg", "请求无效");
		response.setContentType("application/json; charset=utf-8");
		response.getWriter().println(json.toJSONString());
	}
	
	private void printParameters(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		sb.append("url:").append(request.getRequestURI()).append(", paras:");
		for (String k : request.getParameterMap().keySet()) {
			if (k.equals("w_h") || k.equals("content")) {
				continue;
			}
			if (profile.contains("prod") & 
					(k.equals("card_id") || k.equals("password") || k.equals("real_name"))) {
				sb.append(k).append("=").append("***").append("&");
			} else {
				sb.append(k).append("=").append(request.getParameter(k)).append("&");
			}
		}
		if (request.getParameterMap().size() > 0) {
			sb.deleteCharAt(sb.length()-1);
		}
		logger.info("whitebox_parse_paras:[{}]", sb.toString());
	}
}
