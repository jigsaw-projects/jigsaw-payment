package org.jigsaw.payment.core;

import org.jigsaw.payment.mysql.JdbcProtobufTemplate;

import com.google.protobuf.Message;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月10日
 */
public interface PaymentSharder<T extends Message> {

	/**
	 * 根据Uid获取JdbcTemplate
	 * @param uid
	 * @return
	 */
	public JdbcProtobufTemplate<Long, T> getTemplateByUserId(long uid);
	
	/**
	 * 获取对应的表名
	 * @param uid
	 * @return
	 */
	public String getTableByUserId(long uid);
	/**
	 * 根据订单号获取JdbcTemplate
	 * @param code 支付订单号
	 * @return
	 */
	public JdbcProtobufTemplate<Long, T> getTemplateById(long id);
	
	/**
	 * 获取对应的表名
	 * @param code 订单号
	 * @return
	 */
	public String getTableById(long id);
}
