/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.iqiyi.pay.frontend.param.ResultCode;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年7月4日
 */
public class Ret {
	
	static {
		JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteMapNullValue.getMask();
	}
	
	public static String toJson(ResultCode code) {
		JSONObject json = new JSONObject();
		json.put("code", code.getCode());
		json.put("msg", code.getMsg());
		json.put("data", "");
		return json.toJSONString();
	}
	
	public static String toJson(ResultCode code, Object data) {
		JSONObject json = new JSONObject();
		json.put("code", code.getCode());
		json.put("msg", code.getMsg());
		json.put("data", JSON.toJSON(data));
		return json.toJSONString();
	}
	
	public static String toJsonWithMsg(ResultCode code, String msg) {
		JSONObject json = new JSONObject();
		json.put("code", code.getCode());
		json.put("msg", msg);
		return json.toJSONString();
	}
}
