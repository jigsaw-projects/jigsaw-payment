package com.iqiyi.pay.frontend.web.order;

import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.param.QueryOrderParam;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.frontend.web.BaseController;
import com.iqiyi.pay.sdk.PayTrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by leishengbao on 8/12/16.
 */
@Controller
@RequestMapping("/frontend/")
public class QueryOrderController extends BaseController<QueryOrderParam> {

    @Autowired
    PayTradeService payTradeService;


    @RequestMapping("/query")
    protected PayResult queryOrder(QueryOrderParam param, BindingResult result) {
        return super.commonInvoke(param, result);
    }



    @Override
    protected void generateResultData(PayResultBuilder builder, QueryOrderParam param) {
        PayTrade payTrade = payTradeService.queryOrderByCode(param.getOrder_code());
        builder.setData(payTrade);
    }


}
