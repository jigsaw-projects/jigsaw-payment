package com.iqiyi.pay.frontend.web;

import com.iqiyi.pay.common.security.PayUtils;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.param.BindParam;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.service.Payment;
import com.iqiyi.pay.frontend.service.PaymentFactory;
import com.iqiyi.pay.frontend.service.bank.BankCardInfoService;
import com.iqiyi.pay.frontend.service.route.CardRouteService;
import com.iqiyi.pay.sdk.CardBankMap;
import com.iqiyi.pay.sdk.ChannelAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * Created by leishengbao on 8/8/16.
 */
@Controller
@RequestMapping("/frontend/")
public class ChannelBindController extends BaseController<BindParam>{

    @Autowired
    PaymentFactory paymentFactory;
    @Autowired
    BankCardInfoService bankCardInfoService;
    @Autowired
    CardRouteService cardRouteService;

    @RequestMapping("/bind")
    public PayResult channelBind(BindParam param, BindingResult result) {
        return super.commonInvoke(param, result);
    }

    @Override
    protected void generateResultData(PayResultBuilder builder, BindParam param) {
        String serviceCode = null;
        ChannelAccount channelAccount = queryChannelAccount(param);
        if (channelAccount == null){
            builder.setResultCode(ResultCode.ERROR_OF_ROUTE_NOT_EXIST);
            return;
        }
        serviceCode = channelAccount.getChannelCode();
        Payment payment = paymentFactory.getPayment(serviceCode);
        Map<String, String> params = PayUtils.genMapByRequestParas(getRequest().getParameterMap());
        payment.configureBind(params);
        payment.bindRequest(serviceCode);
    }




    private ChannelAccount queryChannelAccount(BindParam param){
        CardBankMap cardBankMap = bankCardInfoService.queryBankInfo(param.getCard_num());
        ChannelAccount channelAccount = cardRouteService.queryBankRoute(cardBankMap.getBankCode(), cardBankMap.getCardType(), param.getPlatform());
        return channelAccount;
    }


}
