
package org.jigsaw.payment.user.mysql;

import org.jigsaw.payment.metric.Timer;
import org.jigsaw.payment.model.User;
import org.jigsaw.payment.mysql.JdbcProtobufTemplate;
import org.jigsaw.payment.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 支持分表分库的MySQL订单管理
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月9日
 */
public class MySQLUserRepository implements UserRepository {	
	
	private JdbcProtobufTemplate<String, User> template;
	
	@Autowired
	public MySQLUserRepository(JdbcProtobufTemplate<String, User> template) {
		this.template = template;
	}

	private static final String SQL_GET_PAY_USER = "select * from entity_user where id = ?";

	@Override
	@Timer("get-user")
	public User get(String uid) {
		return template.get(SQL_GET_PAY_USER, Long.parseLong(uid));
	}


	@Override
	@Timer("create-user")
	@Transactional(rollbackFor=Exception.class) 
	public String create(User user) {
		this.template.insert(user);
		return ""+ user.getId();
	}
	
}
