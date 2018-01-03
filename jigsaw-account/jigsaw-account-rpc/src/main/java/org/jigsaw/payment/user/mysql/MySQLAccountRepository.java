
package org.jigsaw.payment.user.mysql;

import org.jigsaw.payment.metric.Timer;
import org.jigsaw.payment.model.Account;
import org.jigsaw.payment.model.User;
import org.jigsaw.payment.mysql.JdbcProtobufTemplate;
import org.jigsaw.payment.user.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 支持分表分库的MySQL订单管理
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月9日
 */
public class MySQLAccountRepository implements AccountRepository {	
	
	private JdbcProtobufTemplate<String, Account> template;
	
	@Autowired
	public MySQLAccountRepository(JdbcProtobufTemplate<String, Account> template) {
		this.template = template;
	}

	private static final String SQL_GET_ACCOUNT = "select * from entity_account where id = ?";

	@Override
	@Timer("get-account")
	public Account get(String uid) {
		return template.get(SQL_GET_ACCOUNT, Long.parseLong(uid));
	}


	@Override
	@Timer("create-account")
	@Transactional(rollbackFor=Exception.class) 
	public String create(Account account) {
		this.template.insert(account);
		return ""+ account.getId();
	}
	
}
