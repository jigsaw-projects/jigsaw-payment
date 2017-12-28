/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jigsaw.payment.rpc.sharder;

import org.apache.thrift.TException;

/**
 * 对TException的封装，转换成RuntimeException，以便客户端统一处理。
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年9月6日
 */
public class RpcAccessException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1688589949292604653L;

	public RpcAccessException(String arg0, TException arg1) {
		super(arg0, arg1);
	}

}
