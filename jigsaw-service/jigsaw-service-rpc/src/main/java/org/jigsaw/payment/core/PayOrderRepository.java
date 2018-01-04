/**
 * 
 */
package org.jigsaw.payment.core;

import org.jigsaw.payment.model.PayOrder;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月9日
 */
public interface PayOrderRepository {
	/**
	 * 根据交易主体和订单号获取给定的订单
	 * @param code 订单号
	 * @return 符合条件的订单，如果没有找到，返回空。 
	 */
	public PayOrder get(long code);
	/**
	 * 创建订单
	 * @param order 待插入的订单。注意，这里订单的id和订单号code,必须先通过IdService来申请。 将订单id和code设置和插入订单到数据库中的操作分离，也是为了提升系统处理性能。
	 * @return 订单号。 
	 */
	public long create(PayOrder order) ;
}
