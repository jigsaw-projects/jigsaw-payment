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

import org.jigsaw.payment.model.ContractAccount;

/**
 * 签约账户的管理
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月24日
 */
public interface ContractAccountRepository {
	/**
	 * 创建第三方账户
	 * @param contract, 待创建的签约账户。注意，为了提升性能，account的id和code必须先通过IdService来申请。 
	 * @return 订单号。 
	 */
	public long create(ContractAccount contract);


	/**
	 * 获取签约账户
	 * @param id， 账户ID。 
	 * @return 对应的签约账户
	 */
	public ContractAccount get(long id);
}
