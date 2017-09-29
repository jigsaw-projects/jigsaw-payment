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

/**
 * 用户库，正常情况下，应该使用公司已有的用户库。 
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年9月3日
 */
public interface UserRepository {
	/**
	 * 根据用户id获取用户信息。
	 * @param uid
	 * @return
	 */
	public User getUser(String uid);
}
