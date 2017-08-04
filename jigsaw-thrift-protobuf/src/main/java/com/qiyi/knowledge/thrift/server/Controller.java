/**
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 **/
package com.qiyi.knowledge.thrift.server;

import org.apache.thrift.TException;

import com.google.protobuf.Message;

/**
 * RPC Controller. 各服务应该实现这个接口。
 * @author Li Hengjun<lihengjun@qiyi.com>
 * @version 1.0.0  5/16/16
 **/
public interface Controller <Request extends Message, Response extends Message>{
	/**
	 * 
	 * @param request
	 * @return response
	 * @throws TException
	 */
	public Response process(Request request) throws TException;
}
