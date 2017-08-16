package com.iqiyi.pay.frontend.web;

import com.iqiyi.pay.common.security.PayUtils;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.param.UnbindParam;
import com.iqiyi.pay.frontend.service.Payment;
import com.iqiyi.pay.frontend.service.PaymentFactory;
import com.iqiyi.pay.frontend.service.bank.BankCardInfoService;
import com.iqiyi.pay.sdk.BankCardCertification;
import com.iqiyi.pay.sdk.PayUserCard;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created by leishengbao on 8/11/16.
 */
@RestController
@RequestMapping("/frontend/")
public class ChannelUnbindController extends BaseController<UnbindParam>{

    @Autowired
    PaymentFactory paymentFactory;
    @Autowired
    BankCardInfoService bankCardInfoService;

    @RequestMapping("unbind")
    protected PayResult unbind(@Valid UnbindParam param, BindingResult result) {
        return super.commonInvoke(param, result);
    }



    @Override
    protected void generateResultData(PayResultBuilder builder, UnbindParam param) {
        String serviceCode = "sprd";
        PayUserCard card = bankCardInfoService.getPayUserCard(getBankCardId(param));
        if (card == null || card.getUserId() != param.getUid()){
            builder.setResultCode(ResultCode.ERROR_OF_CARD_NOT_EXIT);
            return;
        }
        serviceCode = card.getPayType();
        Payment payment = paymentFactory.getPayment(serviceCode);
        payment.configureUnBind(PayUtils.genMapByRequestParas(getRequest().getParameterMap()));
        payment.unbindRequest(serviceCode.toLowerCase());
        builder.setResultCode(ResultCode.SUCCESS);
    }



    private Long getBankCardId(UnbindParam param){
        return param.getCard_id();
    }


    private String getBankCardIdByCache(UnbindParam param){
        if (StringUtils.isBlank(param.getCard_num())){
            BankCardCertification bcc = bankCardInfoService.
                    queryBankCertifycation(String.valueOf(param.getUid()), param.getCard_num());
            if (bcc != null){
                return String.valueOf(bcc.getId());
            }
        }
        return null;
    }


}
