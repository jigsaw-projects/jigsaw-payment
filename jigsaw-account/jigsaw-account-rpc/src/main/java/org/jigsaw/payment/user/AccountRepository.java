/**
 * 
 */
package org.jigsaw.payment.user;

import org.jigsaw.payment.model.Account;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月9日
 */
public interface AccountRepository {
	/**
	 * 获取用户信息
	 * @param accountId， 账户Id
	 * @return 符合条件的账户，如果没有找到，返回空。 
	 */
	public Account get(String accountId);
	/**
	 * 创建账户
	 * @param account 账户
	 * @return 账户号。 
	 */
	public String create(Account account) ;
}
