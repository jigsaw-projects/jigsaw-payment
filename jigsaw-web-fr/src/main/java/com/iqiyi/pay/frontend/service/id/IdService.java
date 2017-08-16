/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.service.id;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.iqiyi.pay.common.security.PayUtils;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年7月14日
 */
@Service
public class IdService {
	
	private Logger logger = LoggerFactory.getLogger(IdService.class);
	
	@Autowired
	private RestTemplate template;
	
	@Value("${id.certify.url:http://bj.wallet.qiyi.domain/cert/id/bank_verify}")
	private String idcerUrl;
	
	@Value("${id.get.url:http://bj.wallet.qiyi.domain/cert/id/get}")
	private String idcgetUrl;
	
	@Value("${id.certify.sign.key:1234567890}")
	private String signKey;

	@Async
	public void writeCertification(long userId, String idName, String idNo) throws RuntimeException{
		MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
		vars.set("partner", "frontend");
		vars.set("user_id", userId+"");
		vars.set("id_name", idName);
		vars.set("id_no", idNo);
		vars.set("version",  "1.0");
		vars.set("sign", PayUtils.signMessageRequest(vars.toSingleValueMap(), signKey));
		template.postForObject(idcerUrl, vars, JSONObject.class);
	}
	
	
	public IdCard getIdInfo(long userId) throws RuntimeException{
		MultiValueMap<String, String> vars = new LinkedMultiValueMap<String, String>();
		vars.put("partner", Arrays.asList("frontend"));
		vars.put("user_id", Arrays.asList(userId+""));
		vars.put("version",  Arrays.asList("1.0"));
		vars.put("sign",  Arrays.asList(
				PayUtils.signMessageRequest(vars.toSingleValueMap(), signKey)));
		JSONObject json = template.postForObject(idcgetUrl, vars, JSONObject.class);
		JSONObject ret = json.getJSONObject("data");
		IdCard id = new IdCard();
		if(ret == null){
			logger.info("getCertInfo by userId:{} return:{}",userId,json);
			return id;
		}
		if (ret.getIntValue("bind") == 1) {
			id.idName = ret.getString("id_name");
			id.idNo = ret.getString("id_no");
			id.bind = ret.getIntValue("bind");
		}
		return id;
	}
	
	public static class IdCard {
		public String idName="";
		public String idNo="";
		public int bind;
	}
}
