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
package org.jigsaw.payment.core;

import org.jigsaw.payment.model.Account;
/**
 * 账户库，提供账户访问功能。
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月17日
 */
public interface AccountRepository {
	/**
	 * 创建账户。
	 * @param account，待创建的账户。 注意，为了提升性能，account的id和code必须先通过IdService来申请。 
	 * @return 订单号。 
	 */
	public long create(Account account);


	/**
	 * 获取账户信息。
	 * @param id, 账户号。 
	 * @return 账户信息。如果不存在，返回空值。 
	 */
	public Account get(long id);

}
