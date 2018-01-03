
package org.jigsaw.payment.order.mysql;

import java.text.MessageFormat;

import org.jigsaw.payment.metric.Timer;
import org.jigsaw.payment.model.IllegalParameterException;
import org.jigsaw.payment.model.PayOrder;
import org.jigsaw.payment.model.StatusCode;
import org.jigsaw.payment.mysql.JdbcProtobufTemplate;
import org.jigsaw.payment.order.JdbcSharder;
import org.jigsaw.payment.order.PayOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 支持分表分库的MySQL订单管理
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月9日
 */
public class MySQLShardingPayOrderRepository implements PayOrderRepository {	
	
	private JdbcSharder sharder;
	
	@Autowired
	public MySQLShardingPayOrderRepository(JdbcSharder sharder) {
		this.sharder = sharder;
	}

	private static final String SQL_GET_PAY_ORDER = "select * from {0} where sub_id=? AND code = ?";

	@Override
	@Timer("get-order")
	public PayOrder get(long subId, String code) {
		JdbcProtobufTemplate<Long, PayOrder> template = this.sharder.getTemplateByUserId(subId);
		String tableName = this.sharder.getTableByUserId(subId);
		String sql = MessageFormat.format(SQL_GET_PAY_ORDER, tableName);
		return template.get(sql, subId, code);
	}

	/**
	 *验证数据有效性
	 * @param order
	 */
	private void validate(PayOrder order){
		if(!order.hasSubId())
			throw new IllegalParameterException(StatusCode.DATA_REQUIRED_VALUE, "error.pay.order.require.sub_id");
		if(!order.hasCreateTime())
			throw new IllegalParameterException(StatusCode.DATA_REQUIRED_VALUE, "error.pay.order.require.create_time");		
		
	}

	@Override
	@Timer("create-order")
	public long create(PayOrder order) {
		this.validate(order);
		String tableName = this.sharder.getTableByUserId(order.getSubId());
		this.sharder.getTemplateByUserId(order.getSubId()).insert(order, tableName);
		return order.getId();
	}
	
}
