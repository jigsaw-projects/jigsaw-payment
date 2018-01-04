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
package org.jigsaw.payment.bus;

import org.apache.thrift.TException;
import org.jigsaw.payment.model.User;
import org.jigsaw.payment.rpc.UserService;
import org.jigsaw.payment.rpc.sharder.RpcServiceClient;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年9月3日
 */
public class RpcUserRepository implements UserRepository {

	//用户Id的最小值，根据实际情况来设定
	private static final long USER_ID_MIN = 10000;
	//用户I的最大值，根据实际情况来设定
	private static final long USER_ID_MAX = Integer.MAX_VALUE;
	
	private RpcServiceClient client;
	
	private String rpcUsername = "username";
	
	private String rpcPassword = "password";
	
	public RpcUserRepository(RpcServiceClient client){
		this.client = client ;
	}
	
	/**
	 * 根据ID获取用户。
	 * @throws TException 
	 */
	@Override
	public User getUser(String uid) {
		this.validUserId(uid);
		UserService.GetUserRequest.Builder request = UserService.GetUserRequest.newBuilder();
		request.setTargetUserId(uid);
		request.setUserName(rpcUsername);
		request.setPassword(rpcPassword);
		UserService.GetUserResponse response = this.client.execute("getUser", request.build(), UserService.GetUserResponse.class);
		return response.getUser();
	}
	
	/**
	 * 首先验证用户ID，避免无效ID攻击。 
	 * @param uid
	 */
	private void validUserId(String uid){
		long userId = Long.parseLong(uid);
		if(userId<USER_ID_MIN || userId > USER_ID_MAX)
			throw new IllegalArgumentException();
	}

}
