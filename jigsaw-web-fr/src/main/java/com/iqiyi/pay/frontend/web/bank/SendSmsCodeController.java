package com.iqiyi.pay.frontend.web.bank;

import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.param.SendSmsCodeParam;
import com.iqiyi.pay.frontend.service.BankPayment;
import com.iqiyi.pay.frontend.service.Payment;
import com.iqiyi.pay.frontend.service.PaymentFactory;
import com.iqiyi.pay.frontend.service.cache.BankInfoCacheManager;
import com.iqiyi.pay.frontend.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

/**
 * Created by leishengbao on 8/30/16.
 */
@RestController
@RequestMapping("/bank/")
public class SendSmsCodeController extends BaseController<SendSmsCodeParam> {

    @Autowired
    BankInfoCacheManager bankInfoCacheManager;
    @Autowired
    PaymentFactory paymentFactory;

    @RequestMapping("sendsms")
    protected PayResult sendSmsCode(@Valid SendSmsCodeParam param, BindingResult result) {
        return super.commonInvoke(param, result);
    }

    @Override
    protected void generateResultData(PayResultBuilder builder, SendSmsCodeParam param) {
        Map<String, String> cardInfo = bankInfoCacheManager.getBankTransInfo(param.getCache_key());
        if (cardInfo == null){
            builder.setResultCode(ResultCode.ERROR_OF_SMS_INFO_EXPIRE);
            return;
        }
        String serviceCode = cardInfo.get("service_code");
        Payment payment = paymentFactory.getPayment(serviceCode);
        ((BankPayment)payment).configureCheckId(cardInfo);
        PayResult<Map<String, Object>> checkResult = ((BankPayment) payment).checkIdentity(serviceCode);
        if (!ResultCode.SUCCESS.getCode().equals(checkResult.getCode())){
            builder.setResultCode(ResultCode.ERROR_OF_CARD_INFO_VALID);
            return;
        }
        if (checkResult.getData() != null){
            checkResult.getData().put("order_code", param.getOrder_code());
            checkResult.getData().put("cache_key", param.getCache_key());
        }
        if (ResultCode.SUCCESS.getCode().equals(checkResult.getCode())){
            builder.setCode(checkResult.getCode()).setMsg(checkResult.getMsg()).setData(checkResult.getData());
        }
    }
}
