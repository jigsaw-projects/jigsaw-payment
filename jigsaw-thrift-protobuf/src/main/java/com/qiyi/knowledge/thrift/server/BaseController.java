/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * Author: Hu Xia<xiahu@qiyi.com>
 *****************************************************
 */
package com.qiyi.knowledge.thrift.server;

import com.google.protobuf.Message;

import com.iqiyi.pay.sdk.service.ErrorCode;
import com.iqiyi.pay.sdk.service.NotFoundException;
import com.iqiyi.pay.sdk.service.SystemException;
import com.iqiyi.pay.sdk.service.UserException;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 * Base Controller, every service must extend this acl.
 * 
 * @author HuXia<xiahu@qiyi.com>
 */
public abstract class BaseController<Request extends Message, Response extends Message>
		implements Controller<Request, Response> {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

	@Override
	public Response process(Request request) throws TException {
		long beginTime = System.currentTimeMillis();
		try {
			Response response = doProcess(request);
			LOGGER.debug("request {}, process cost time is {}", request.getClass(),
					System.currentTimeMillis() - beginTime);
			return response;
		} catch(EmptyResultDataAccessException ex) {
			LOGGER.info("result is null",ex);
			throw new NotFoundException();
		} catch (IllegalArgumentException ex) {
			LOGGER.error("illegal argument", ex);
			throw new UserException(ErrorCode.BAD_DATA_FORMAT).setMessage(ex.getMessage());
		}catch (NotFoundException|UserException|SystemException e){
			LOGGER.warn("Thrift exception", e);
			throw e;
		}catch (TException e){
			LOGGER.error("Thrift exception", e);
			throw e;
		} catch (Exception ex) {
			LOGGER.error("do process error", ex);
			throw new SystemException(ErrorCode.INTERNAL_ERROR).setMessage(ex.getMessage());
		}
	}

	/**
	 * every service must implement this method
	 * 
	 * @param request
	 * @return response
	 * @throws Exception
	 */
	protected abstract Response doProcess(Request request) throws Exception;
}
