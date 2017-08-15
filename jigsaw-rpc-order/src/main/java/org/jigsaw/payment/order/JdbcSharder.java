package org.jigsaw.payment.order;

import org.jigsaw.payment.model.PayOrder;
import org.jigsaw.payment.mysql.JdbcProtobufTemplate;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月10日
 */
public interface JdbcSharder {

	/**
	 * 根据Uid获取JdbcTemplate
	 * @param uid
	 * @return
	 */
	public JdbcProtobufTemplate<Long, PayOrder> getTemplateByUserId(long uid);
	
	/**
	 * 获取对应的表名
	 * @param uid
	 * @return
	 */
	public String getTableByUserId(long uid);
}
