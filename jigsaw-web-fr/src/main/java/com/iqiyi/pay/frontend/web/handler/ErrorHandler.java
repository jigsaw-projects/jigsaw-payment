/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.web.handler;

import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.exception.PwdNotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iqiyi.pay.frontend.exception.PwdErrorException;
import com.iqiyi.pay.frontend.exception.PwdFrozenException;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.utils.Ret;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年8月24日
 */
@ControllerAdvice
@ResponseBody
public class ErrorHandler {

	public static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

	@ExceptionHandler({PwdFrozenException.class})
	public String handleException(PwdFrozenException e) {
		return Ret.toJsonWithMsg(ResultCode.ERROR_OF_PASSWORD_FROZEN, e.getMessage());
	}
	
	@ExceptionHandler({PwdErrorException.class})
	public String handleException(PwdErrorException e) {
		return Ret.toJsonWithMsg(ResultCode.ERROR_OF_PASSWORD_WRONG, e.getMessage());
	}

	@ExceptionHandler({PwdNotExistException.class})
	public String handleException(PwdNotExistException e) {
		return Ret.toJsonWithMsg(ResultCode.ERROR_OF_PASSWORD_NOT_EXIST, e.getMessage());
	}

	@ExceptionHandler(RuntimeException.class)
	public String runTimeExceptionHandler(HttpServletRequest request, Exception e){
		PayResultBuilder<Object> builder = PayResultBuilder.create();
		LOGGER.error(":[uri:{}]", request.getRequestURI(), e);
		builder.setResultCode(ResultCode.ERROR_OF_SYSTEM).setData(e.getMessage());
		return builder.build().toJson();
	}
}
