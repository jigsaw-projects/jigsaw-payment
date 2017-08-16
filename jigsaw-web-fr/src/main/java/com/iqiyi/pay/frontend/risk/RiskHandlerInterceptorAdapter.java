/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.risk;

import com.alibaba.fastjson.JSONObject;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.iqiyi.pay.common.utils.RequestUtils;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.service.sms.SMSCodeService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年8月3日
 */
public class RiskHandlerInterceptorAdapter extends HandlerInterceptorAdapter {
	
	Logger logger = LoggerFactory.getLogger(RiskHandlerInterceptorAdapter.class);
	
	@Autowired
	private RestTemplate template;
	
	@Value("${withdraw.risk.url}")
	private String riskUrl;
	
	@Autowired
	private SMSCodeService sms;
	
	@Value("${sms.code.template:爱奇艺钱包提现验证码{}}")
	private String smsTemplate;
	
	@Value("${withdraw.risk.sms.tip:交易存在风险，需要验证你的身份}")
	private String riskTip;;
	
	@Value("${withdraw.risk.deny.tip:您的交易存在安全风险，暂时无法办理！}")
	private String riskDenyTip;;

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		HandlerMethod hm = (HandlerMethod)handler;
		RiskFilter risk = hm.getMethodAnnotation(RiskFilter.class);
		if (risk == null) {
			return true;
		}
		
		long userId = Long.valueOf(request.getParameter("auth_user_id"));
		String mobile = request.getParameter("auth_user_mobile");
		long bankCardId = Long.valueOf(request.getParameter("card_id"));
//		System.out.println(request.getParameter("card_id"));
//		BankCardCertification bc = cardService.getBankCard(userId, bankCardId);
//		if (bc == null) {
//			logger.error("bank card id {} not found for user {}", bankCardId, userId);
//			return false;
//		}
		
		JSONObject paras = new JSONObject();
		
		paras.put("uid", userId);
		
//		paras.put("id_card", e(bc.getOwnerIdNum()));
//		paras.put("real_name", e(bc.getOwnerName()));
//		paras.put("nk", e(bc.getCardNumber()));
		
		paras.put("device_id", request.getParameter("device_id"));
		paras.put("fee", request.getParameter("fee"));
		paras.put("user_account", request.getParameter("auth_user_name"));
		paras.put("qc5", request.getParameter("device_id"));
		paras.put("mobile", mobile);
		paras.put("dfp", request.getParameter("dfp"));
		paras.put("envinfo", request.getParameter("envinfo"));
		
		paras.put("user_agent", request.getHeader("user_agent"));
		paras.put("referer", request.getHeader("referer"));
		paras.put("xff_ip", request.getHeader("X-Forwarded-For"));
		paras.put("cip", RequestUtils.getRemoteAddr(request));
		
		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
		form.set("biz_code", "payment");
		form.set("biz_name", "wallet_withdraw");
		form.set("params", paras.toJSONString());
		try {
			logger.info("send risk: {}", form.toString());
			String res = template.postForObject(riskUrl, form, String.class);
			logger.info("return risk: {}", res);
			RiskAssessment ra = RiskAssessmentHandler.handle(res);
			if (ra == null) {
				return true;
			}
			
			if (ra.getLevel() == 0) {
				return true;
			}
			
			if (ra.getLevel() == 1) {
				if (StringUtils.isBlank(request.getParameter("sms_key"))) {
					sendSmsCosde(response, userId, mobile);
					return false;
				}
			}
			
			if (ra.getLevel() >= 2) {
				logger.info("user_id: {}, risk: {}", userId, ra.getReason());
				writeRiskToResponse(response, ra);
				return false;
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return true;
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void writeRiskToResponse(HttpServletResponse response, RiskAssessment ra) {
		PayResult b = PayResultBuilder.create().setResultCode(ResultCode.RISK).setMsg(riskDenyTip).build();
		try {
			response.getWriter().println(b.toJson());
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void sendSmsCosde(HttpServletResponse response, long userId, String mobile) {
		String smsKey = sms.sendSMS(userId, mobile);
		JSONObject data = new JSONObject();
		data.put("ret", 2);
		data.put("sms_key", smsKey);
		data.put("mobile", mobile);
		data.put("sms_template", smsTemplate);
		PayResult b = PayResultBuilder.create().setResultCode(ResultCode.SUCCESS).setMsg(riskTip).build();
		try {
			response.getWriter().println(b.toJson());
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
	
	private String e(String data) {
		HashFunction hf = Hashing.md5();
		HashCode hc = hf.hashString(data, Charset.forName("utf-8"));
		return hc.toString().substring(0, 20);
	}
}
