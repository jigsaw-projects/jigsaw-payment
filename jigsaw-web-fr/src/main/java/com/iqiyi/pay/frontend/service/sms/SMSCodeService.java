/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */

package com.iqiyi.pay.frontend.service.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iqiyi.pay.common.security.PayUtils;
import com.iqiyi.pay.frontend.exception.SmsCodeErrorException;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年6月12日
 */
@Service
public class SMSCodeService {
	
	@Value("${sms.code.valid.url:http://bj.wallet.qiyi.domain/pay-service-sms/service/sms/validate}")
	private String smsValidUrl;
	
	@Value("${sms.code.send.url:http://bj.wallet.qiyi.domain/pay-service-sms/service/sms/send}")
	private String smsSendUrl;
	
	@Value("${sms.code.sign_key:rr238537yueridfsh78487jyuincsffd}")
	private String smsSignKey;
	
	@Value("${sms.code.template:爱奇艺支付验证码{}}")
	private String smsTemplate;
	
	@Autowired
	private RestTemplate template;
	
	public void validSmsCode(long userId, String smsKey, String smsCode) throws SmsCodeErrorException {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.set("uid", userId+"");
		map.set("sms_key", smsKey);
		map.set("sms_code", smsCode);
		map.set("sign", PayUtils.signMessageRequest(map.toSingleValueMap(), smsSignKey));
		String res = template.postForObject(smsValidUrl, map, String.class);
		JSONObject json = JSON.parseObject(res);
		if ("SUC00000".equals(json.getString("code"))) {
			return;
		}
		
		if ("ERR00004".equals(json.getString("code"))) {
			throw new SmsCodeErrorException();
		}
		
		throw new RuntimeException(json.getString("msg"));
	}

	public String sendSMS(long userId, String mobile) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.set("uid", userId+"");
		map.set("mobile", mobile);
		map.set("sms_template", smsTemplate);
		map.set("sms_code_length", 6+"");
		map.set("sign", PayUtils.signMessageRequest(map.toSingleValueMap(), smsSignKey));
		String res = template.postForObject(smsSendUrl, map, String.class);
		JSONObject json = JSON.parseObject(res);
		if ("SUC00000".equals(json.getString("code"))) {
			return json.getJSONObject("data").getString("sms_key");
		}
		throw new RuntimeException(json.getString("msg"));
	}
	
}
