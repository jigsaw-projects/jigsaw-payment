/**
 * 
 */
package org.jigsaw.payment.user;

import org.jigsaw.payment.model.User;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月9日
 */
public interface UserRepository {
	/**
	 * 获取用户信息
	 * @param userid， 用户Id
	 * @return 符合条件的订单，如果没有找到，返回空。 
	 */
	public User get(String userId);
	/**
	 * 创建用户
	 * @param user 用户
	 * @return 用户id。 
	 */
	public String create(User user) ;
}
