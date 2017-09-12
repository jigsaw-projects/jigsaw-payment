package org.jigsaw.payment.core.mysql;

import java.text.MessageFormat;

import org.jigsaw.payment.core.PayOrderRepository;
import org.jigsaw.payment.core.PaymentSharder;
import org.jigsaw.payment.metric.Timer;
import org.jigsaw.payment.model.IllegalParameterException;
import org.jigsaw.payment.model.PayOrder;
import org.jigsaw.payment.model.StatusCode;
import org.jigsaw.payment.mysql.JdbcProtobufTemplate;

/**
 * 支持分表分库的MySQL订单管理
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月9日
 */
public class MySQLShardingPayOrderRepository implements PayOrderRepository {

	private PaymentSharder<PayOrder> sharder;

	
	public MySQLShardingPayOrderRepository(PaymentSharder<PayOrder> sharder) {
		this.sharder = sharder;
	}

	private static final String SQL_GET_PAY_ORDER = "select * from {0} where id = ?";

	@Override
	@Timer("get-order")
	public PayOrder get(long id) {
		JdbcProtobufTemplate<Long, PayOrder> template = this.sharder
				.getTemplateById(id);
		String tableName = this.sharder.getTableById(id);
		String sql = MessageFormat.format(SQL_GET_PAY_ORDER, tableName);
		return template.get(sql, id);
	}

	/**
	 * 验证数据有效性
	 * 
	 * @param order
	 */
	private void validate(PayOrder order) {
		if (!order.hasSubId())
			throw new IllegalParameterException(StatusCode.DATA_REQUIRED_VALUE,
					"error.pay.order.require.sub_id");
		if (!order.hasId())
			throw new IllegalParameterException(StatusCode.DATA_REQUIRED_VALUE,
					"error.pay.order.require.id");
		if (!order.hasKey())
			throw new IllegalParameterException(StatusCode.DATA_REQUIRED_VALUE,
					"error.pay.order.require.key");
		if (!order.hasCreateTime())
			throw new IllegalParameterException(StatusCode.DATA_REQUIRED_VALUE,
					"error.pay.order.require.create_time");

	}

	@Override
	@Timer("create-order")
	public long create(PayOrder order) {
		this.validate(order);
		String tableName = this.sharder.getTableByUserId(order.getSubId());
		this.sharder.getTemplateByUserId(order.getSubId()).insert(order,
				tableName);
		return order.getId();
	}

}
