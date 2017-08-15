/**
 * 
 */
package org.jigsaw.payment.order;

import org.jigsaw.payment.model.PayOrder;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月9日
 */
public interface PayOrderRepository {
	/**
	 * 根据交易主体和订单号获取给定的订单
	 * @param subId 交易主体的ID
	 * @param code 订单号
	 * @return 符合条件的订单，如果没有找到，返回空。 
	 */
	public PayOrder get(long subId, String code);
	/**
	 * 创建订单
	 * @param order 带插入的订单
	 * @return 订单ID。 
	 */
	public long create(PayOrder order) ;
}
