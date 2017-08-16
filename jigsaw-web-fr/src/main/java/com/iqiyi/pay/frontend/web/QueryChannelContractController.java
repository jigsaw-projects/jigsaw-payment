package com.iqiyi.pay.frontend.web;

import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.param.QueryContractParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by leishengbao on 8/19/16.
 */
@RestController
@RequestMapping("/frontend/")
public class QueryChannelContractController extends BaseController<QueryContractParam>{


    @RequestMapping("queryContract")
    public PayResult queryContract(QueryContractParam param, BindingResult result) {
        return super.commonInvoke(param, result);
    }

    @Override
    protected void generateResultData(PayResultBuilder builder, QueryContractParam param) {
        super.generateResultData(builder, param);
    }
}
