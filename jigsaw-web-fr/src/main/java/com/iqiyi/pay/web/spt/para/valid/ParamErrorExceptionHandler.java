/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.web.spt.para.valid;

import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年8月16日
 */
@ResponseBody
@ControllerAdvice
public class ParamErrorExceptionHandler {

	@ExceptionHandler({ ParamErrorexception.class })
	public String handleParamError(ParamErrorexception ex) {
		JSONObject json = new JSONObject();
		json.put("code", "ERR00001");
		json.put("msg", "参数 `" + ex.getPara() + "` " + ex.getMessage());
		return json.toJSONString();
	}
	
	@ExceptionHandler({BindException.class})
	public String handleParamError(BindException ex) {
		FieldError oe = (FieldError)ex.getAllErrors().get(0);
		JSONObject json = new JSONObject();
		json.put("code", "ERR00001");
		json.put("msg", "参数 `" + oe.getField() + "` " +  oe.getDefaultMessage());
		return json.toJSONString();
	}
}
