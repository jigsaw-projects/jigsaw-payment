package org.jigsaw.payment.order.rpc;

import org.jigsaw.payment.rpc.PayOrderService.GetPayOrderRequest;
import org.jigsaw.payment.rpc.PayOrderService.GetPayOrderResponse;
import org.jigsaw.payment.rpc.server.BaseController;
import org.springframework.stereotype.Service;

/**
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月9日
 */
@Service("getPayOrder")
public class GetTradeController extends
		BaseController<GetPayOrderRequest, GetPayOrderResponse> {

	@Override
	protected GetPayOrderResponse doProcess(GetPayOrderRequest request)
			throws Exception {
	
		return null;
	}

}
