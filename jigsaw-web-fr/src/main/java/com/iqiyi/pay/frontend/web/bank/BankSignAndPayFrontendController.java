package com.iqiyi.pay.frontend.web.bank;

import com.google.common.collect.Maps;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.param.SignAndPayParam;
import com.iqiyi.pay.frontend.service.Payment;
import com.iqiyi.pay.frontend.service.PaymentFactory;
import com.iqiyi.pay.frontend.service.bank.BankCardInfoService;
import com.iqiyi.pay.frontend.service.bank.CardDutBindManager;
import com.iqiyi.pay.frontend.service.cache.BankInfoCacheManager;
import com.iqiyi.pay.frontend.service.order.OrderPayService;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.frontend.service.pwd.PasswordService;
import com.iqiyi.pay.frontend.utils.Constants;
import com.iqiyi.pay.frontend.web.BaseController;
import com.iqiyi.pay.sdk.BankCardCertification;
import com.iqiyi.pay.sdk.CardBankMap;
import com.iqiyi.pay.sdk.PayTrade;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;

/**
 * Created by leishengbao on 7/20/16.
 */
@RestController
@RequestMapping("/bank/")
public class BankSignAndPayFrontendController extends BaseController<SignAndPayParam> {

    @Autowired
    BankInfoCacheManager bankInfoCacheManager;
    @Autowired
    PaymentFactory paymentFactory;
    @Autowired
    OrderPayService orderPayService;
    @Autowired
    PayTradeService payTradeService;
    @Autowired
    PasswordService passwordService;
    @Autowired
    BankCardInfoService bankCardInfoService;
    @Autowired
    private CardDutBindManager cardDutBindManager;

    @RequestMapping("signAndPay")
    public PayResult signAndPay(@Valid SignAndPayParam signAndPayParam, BindingResult result){
        return super.commonInvoke(signAndPayParam, result);
    }


    /**
     * */
    @Override
    protected void generateResultData(PayResultBuilder builder, SignAndPayParam param) {
        PayTrade payTrade = null;
        if (!isOnlyContract(param)){
            payTrade = payTradeService.queryOrderByCode(param.getOrder_code());
            if (payTrade == null){
                LOGGER.error("[order not exit][orderCode:{}]", param.getOrder_code());
                builder.setResultCode(ResultCode.ERROR_OF_ORDER_NOT_EXIT);
                return;
            }
            if (!payTrade.getUserId().equals(String.valueOf(param.getUid()))){
                builder.setResultCode(ResultCode.ERROR_OF_PARAM_INVALID);
                return ;
            }
            if (payTrade.getStatus() == 1){
                builder.setResultCode(ResultCode.ERROR_OF_ORDER_PAYED);
                return;
            }
        }

        String cacheKey = param.getCache_key();
        Map<String, String> cardInfo = bankInfoCacheManager.getBankTransInfo(cacheKey);
        if (cardInfo == null){
            builder.setResultCode(ResultCode.ERROR_OF_SMS_INFO_EXPIRE);
            return;
        }
        if (!String.valueOf(param.getUid()).equals(cardInfo.get("user_id"))){
            builder.setResultCode(ResultCode.ERROR_OF_PARAM_INVALID);
            return;
        }
        CardBankMap card = bankCardInfoService.queryBankInfo(cardInfo.get("card_num"));
        String serviceCode = cardInfo.get("service_code");
        Payment payment =  paymentFactory.getPayment(serviceCode);
        Map<String, String> bindconfigs = new HashedMap(cardInfo);
        bindconfigs.put("sms_key", param.getSms_key());
        bindconfigs.put("sms_code", param.getSms_code());
        bindconfigs.put("order_code", param.getOrder_code());
        payment.configureBind(bindconfigs);
        PayResult<Map<String, Object>> bindResult = payment.bindRequest(serviceCode);
        Map<String, Object> data = bindResult.getData();
        boolean isContract = isOnlyContract(param);
        String contractId = "";
        LOGGER.info("[{}:{type={}, partnerId={}, result={}, paltform={}, orderCode={}, isContract={}, bankCode={}, serviceCode={}}]", "statistics", "CardSign", "474",
                bindResult.getCode() , param.getPlatform() , param.getOrder_code(), isContract, card.getBankCode(), serviceCode);
        if (ResultCode.SUCCESS.getCode().equals(bindResult.getCode())){
            contractId = Objects.toString(data.get("contract_id"));
        }else if (Constants.CARD_SMS_DOCE_ERROR.equals(bindResult.getCode())
                || Constants.SMS_ERROR_NEW.equals(bindResult.getCode())){
            builder.setResultCode(ResultCode.ERROR_OF_SMSCODE_WRONG);
            return;
        }else {
            builder.setResultCode(ResultCode.ERROR_OF_BANK_SIGN_ERROR);
            return;
        }
        boolean hasPwd = passwordService.isPasswordSet(param.getUid());
        Map<String, String> contractResult = Maps.newHashMap();
        contractResult.put("order_code", param.getOrder_code());
        contractResult.put("has_pwd", hasPwd ? "1" : "0");
        contractResult.put("is_contract", "0");
        contractResult.put("card_id", getCardId(cardInfo.get("user_id"), cardInfo.get("card_num"))+"");
        if (isContract){//纯签约
            contractResult.put("is_contract", "1");
            builder.setResultCode(ResultCode.SUCCESS).setData(contractResult);
            return;
        }




        LOGGER.debug("[order pay][orderCode:{}][userId:{}]", param.getOrder_code(), param.getUid());

        payment.configure(configePayParams(payTrade, cardInfo, contractId));
        PayResult<Map<String, Object>> payResult = payment.payRequest(serviceCode);
        LOGGER.info("[{}:{type={}, partnerId={}, result={}, paltform={}, orderCode={}, isContract={}, bankCode={}, serviceCode={}}]", "statistics", "CardSignAndPay", payTrade.getPartnerId(),
                payResult.getCode() , param.getPlatform() , param.getOrder_code(), isContract, card.getBankCode(), serviceCode);
        if (ResultCode.SUCCESS.getCode().equals(payResult.getCode())){
            orderPayService.orderPayedByServiceCode(payResult.getData(), serviceCode);
            cardDutBindManager.dealCardDut(payTrade);//处理银行卡签约代扣逻辑
            contractResult.put("order_status", "1");
            contractResult.put("fee", payTrade.getRealFee()+"");
        }else if (Constants.PAY_IN_PROCESS.equals(payResult.getCode())){
            cardDutBindManager.dealCardDut(payTrade);//处理银行卡签约代扣逻辑
            contractResult.put("order_status", "1");
            contractResult.put("fee", payTrade.getRealFee()+"");
        }else if (Constants.CARD_BANLANCE_NOT_ENOUGH.equals(payResult.getCode())
                || Constants.BALANCE_NOT_ENOUGH_NEW.equals(payResult.getCode())){
            builder.setResultCode(ResultCode.ERROR_OF_CARD_BANLANCE_NOT_ENOUGH);
            return;
        } else if (Constants.CARD_PAY_IN_PROCESS.equals(payResult.getCode())){
            contractResult.put("fee", payTrade.getRealFee()+"");
            cardDutBindManager.dealCardDut(payTrade);//处理银行卡签约代扣逻辑
            builder.setResultCode(ResultCode.ERROR_OF_CARD_PAY_IN_PROCESS).setData(contractResult);
            return;
        }
        else{
            builder.setResultCode(ResultCode.ERROR_OF_ORDER_UNPAYED);
            return;
        }
        String billNo = Objects.toString(payResult.getData().get("trans_seq"));
        orderPayService.saveBankBillNo(billNo, param.getOrder_code());
        builder.setData(contractResult);
    }



    private Map<String, String> configePayParams(PayTrade payTrade, Map<String, String> cardInfo, String contractId){
        Map<String, String> params = Maps.newHashMap();
        params.putAll(cardInfo);
        params.put("contract_id", contractId);
        params.put("subject", payTrade.getSubject());
        params.put("description", payTrade.getDescription());
        params.put("order_code", payTrade.getOrderCode());
        params.put("uid", payTrade.getUserId());
        params.put("fee", String.valueOf(payTrade.getRealFee()));
        params.put("device_ip", getRequest().getRemoteAddr());
        return params;
    }





    private boolean isOnlyContract(SignAndPayParam param){
        String isContract = bankInfoCacheManager.getOrderContractInfo(param.getOrder_code());
        if (isContract != null && Constants.CONTRACT_TAG.equals(isContract)){
            return true;
        }
        return false;
    }


    private Long getCardId(String userId, String cardNum){
        BankCardCertification bcc = bankCardInfoService.queryBankCertifycation(userId, cardNum);
        if (bcc != null){
            return bcc.getId();
        }
        return null;
    }
}
