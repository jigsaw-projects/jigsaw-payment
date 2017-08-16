package com.iqiyi.pay.frontend.service;

import com.iqiyi.kiwi.utils.DateHelper;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.service.bank.BankCardInfoService;
import com.iqiyi.pay.frontend.service.bank.CardBindManager;
import com.iqiyi.pay.frontend.service.bank.CardDutBindManager;
import com.iqiyi.pay.frontend.service.cache.BankInfoCacheManager;
import com.iqiyi.pay.frontend.service.contract.MultiChannelContractService;
import com.iqiyi.pay.frontend.service.order.OrderPayService;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.frontend.service.refund.RefundService;
import com.iqiyi.pay.frontend.utils.Constants;
import com.iqiyi.pay.frontend.utils.MapWapper;
import com.iqiyi.pay.sdk.BankCardCertification;
import com.iqiyi.pay.sdk.BankTrade;
import com.iqiyi.pay.sdk.PayTradeExtend;
import com.iqiyi.pay.sdk.PayUserCard;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;

/**
 * Created by leishengbao on 8/1/16.
 */
@Component("COMMONBANK")
@Scope("prototype")
public class BankChannelPayment extends AbstractPayment implements BankPayment{

    @Autowired
    private OrderPayService orderPayedService;
    @Autowired
    private BankInfoCacheManager bankInfoCacheManager;
    @Autowired
    private CardBindManager cardBindManager;
    @Autowired
    private BankCardInfoService bankCardInfoService;
    @Autowired
    private PayTradeService payTradeService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private CardDutBindManager cardDutBindManager;
    @Autowired
    private MultiChannelContractService cardContractPostService;


    @Value("${pay.channel.url}")
    private String BANK_CHANNEL_URL;

    public Map<String, String> checkIdConfigs = new MapWapper<>();

    public BankChannelPayment(){
    }
    /**
     * @param params
     */
    @Override
    public void configure(Map<String, String> params) {
        configs.put("user_id",              params.get("uid"));
        configs.put("order_name",           params.get("subject"));
        configs.put("order_description",    params.get("description"));
        configs.put("order_code",           params.get("order_code"));
        configs.put("fee",                  params.get("fee"));
        configs.put("trans_time",           transTime());
        configs.put("user_name",            params.get("user_name"));
        configs.put("card_num",             params.get("card_num"));
        configs.put("card_type",            params.get("card_type"));
        configs.put("card_mobile",          params.get("card_mobile"));
        configs.put("cert_type",            Constants.ID_CARD_CERT_TYPE);
        configs.put("cert_num",             params.get("cert_num"));
        configs.put("contract_id",          params.get("contract_id"));
        configs.put("device_ip",            params.get("device_ip"));
    }


    @Override
    public void configureBind(Map<String, String> params) {
        bindConfigs.put("trans_time",       transTime());
        bindConfigs.put("order_code",       params.get("order_code"));
        bindConfigs.put("user_id",          params.get("user_id"));
        bindConfigs.put("user_name",        params.get("user_name"));
        bindConfigs.put("card_num",         params.get("card_num"));
        bindConfigs.put("card_type",        params.get("card_type"));
        bindConfigs.put("card_validity",    params.get("card_validity"));
        bindConfigs.put("card_cvv2",        params.get("card_cvv2"));
        bindConfigs.put("card_mobile",      params.get("card_mobile"));
        bindConfigs.put("cert_type",        Constants.ID_CARD_CERT_TYPE);
        bindConfigs.put("cert_num",         params.get("cert_num"));
        bindConfigs.put("sms_code",         params.get("sms_code"));
        bindConfigs.put("sms_key",          params.get("sms_key"));
    }


    @Override
    public void configureUnBind(Map<String, String> params){
        BankCardCertification bcc = bankCardInfoService.getBankCard(Long.parseLong(params.get("card_id")));
        PayUserCard card = bankCardInfoService.getPayUserCard(Long.parseLong(params.get("card_id")));
        if (bcc == null || card == null){
            return;
        }
        unbindConfigs.put("card_id",        params.get("card_id"));
        unbindConfigs.put("user_id",        params.get("uid"));
        unbindConfigs.put("trans_time",     transTime());
        unbindConfigs.put("user_name",      bcc.getOwnerName());
        unbindConfigs.put("contract_id",    card.getToken());
        unbindConfigs.put("card_num",       bcc.getCardNumber());
        unbindConfigs.put("card_type",      bcc.getCardType()+"");
        unbindConfigs.put("card_mobile",    bcc.getBindPhone());
    }

    @Override
    public void configureCheckId(Map<String, String> params) {
        checkIdConfigs.put("user_id",       params.get("uid"));
        checkIdConfigs.put("trans_time",    transTime());
        checkIdConfigs.put("user_name",     params.get("user_name"));
        checkIdConfigs.put("card_num",      params.get("card_num"));
        checkIdConfigs.put("card_type",     params.get("card_type"));
        checkIdConfigs.put("card_validity", params.get("card_validity"));
        checkIdConfigs.put("card_cvv2",     params.get("card_cvv2"));
        checkIdConfigs.put("card_mobile",   params.get("card_mobile"));
        checkIdConfigs.put("cert_type",     Constants.ID_CARD_CERT_TYPE);
        checkIdConfigs.put("cert_num",      params.get("cert_num"));
    }


    @Override
    public void configureRefund(Map<String, String> params) {
        String orderCode = params.get("order_code");
        PayTradeExtend payTradeExtend = payTradeService.queryOrderExtendByOrderCode(orderCode);
        if (payTradeExtend == null || !NumberUtils.isNumber(payTradeExtend.getCardId())){
            LOGGER.error("refund error, orderCode:{}, payTradeExtend:{}", orderCode, payTradeExtend);
            return;
        }
        BankTrade bankTrade = payTradeService.queryBankTrade(orderCode);
        String cardId = payTradeExtend.getCardId();
        BankCardCertification bcc = bankCardInfoService.queryBankCertificationByCardId(Long.parseLong(cardId));
        if (bcc != null){
            refundConfigs.put("card_num",               bcc.getCardNumber());
            refundConfigs.put("user_name",              bcc.getOwnerName());
            refundConfigs.put("card_type",              bcc.getCardType()+"");
        }
        refundConfigs.put("user_id",                    params.get("user_id"));
        refundConfigs.put("trans_time",                 transTime());
        refundConfigs.put("refund_fee",                 params.get("refund_fee"));
        refundConfigs.put("orig_trans_seq",             bankTrade.getBillNo() != null ? bankTrade.getBillNo() : "");
        refundConfigs.put("orig_trans_time",            params.get("orig_trans_time"));
        refundConfigs.put("orig_order_code",            params.get("order_code"));
        refundConfigs.put("refund_code",                params.get("refund_code"));
        refundConfigs.put("orig_fee",                   params.get("orig_fee"));
        refundConfigs.put("orig_channel_trade_code",    params.get("orig_channel_trade_code"));
        refundConfigs.put("orig_channel_trade_time",    params.get("orig_channel_trade_time"));
    }


    @Override
    public PayResult<Map<String, Object>> payRequest(String paymentCode) {
        PayResult<Map<String, Object>> payResult = getResultFromChannel(BANK_CHANNEL_URL + paymentCode + "/pay", configs);
        LOGGER.debug("[payRequest][order_code:{}][result:{}]", configs.get("order_code"), payResult);
        return payResult;
    }

    @Override
    public PayResult<Map<String, Object>> checkIdentity(String paymentCode) {
        PayResult<Map<String, Object>> payResult = getResultFromChannel(BANK_CHANNEL_URL + paymentCode + "/sendCode", checkIdConfigs);
        if (ResultCode.SUCCESS.getCode().equals(payResult.getCode())){
            checkIdConfigs.put("service_code", paymentCode);
            String cacheKey = bankInfoCacheManager.setBankTransInfo(checkIdConfigs);
            payResult.getData().put("cache_key", cacheKey);
        }
        LOGGER.debug("[checkIdentity][user_id:{}][result:{}]", checkIdConfigs.get("user_id"), payResult);
        return payResult;
    }

    @Override
    public PayResult<Map<String, Object>> bindRequest(String paymentCode) {
        PayResult<Map<String, Object>> payResult =  getResultFromChannel(BANK_CHANNEL_URL + paymentCode + "/bind", bindConfigs);
        String      userId       =   bindConfigs.get("user_id");
        String      cardNum      =   bindConfigs.get("card_num");
        String      orderCode    =   bindConfigs.get("order_code");
        String      cardMobile   =   bindConfigs.get("card_mobile");
        String      certNum      =   bindConfigs.get("cert_num");
        String      certType     =   bindConfigs.get("cert_type");
        String      userName     =   bindConfigs.get("user_name");
        String      contractId = null;
        if (ResultCode.SUCCESS.getCode().equals(payResult.getCode())){
            contractId = Objects.toString(payResult.getData().get("contract_id"));
        }else if (Constants.BANK_SIGNED_CODE.equals(payResult.getCode())){
            contractId = cardBindManager.queryUserContractId(userId, cardNum, orderCode, paymentCode);
            LOGGER.debug(" replicate bind card [userId:{}][contractId:{}][orderCode:{}]", userId, contractId, orderCode);
            payResult.setCode(ResultCode.SUCCESS.getCode());
        }
        if (StringUtils.isNotBlank(contractId) && ResultCode.SUCCESS.getCode().equals(payResult.getCode())){
            payResult.getData().put("contract_id", contractId);
            long cardId = cardBindManager.saveCardInfoAndContract(orderCode, contractId, userId, userName, cardNum, cardMobile, certType, certNum , paymentCode);
            //处理多通道签约的逻辑
            cardContractPostService.cardBindPost(cardId, paymentCode, bindConfigs);
        }
        LOGGER.debug("[bindRequest][user_id:{}][result:{}]", bindConfigs.get("user_id"), payResult);
        return payResult;
    }

    @Override
    public PayResult<Map<String, Object>> unbindRequest(String paymentCode) {
        String      userId       =   unbindConfigs.get("user_id");
        String      cardNum      =   unbindConfigs.get("card_num");
        Long        cardId       =   Long.parseLong(unbindConfigs.get("card_id"));
        boolean needUnbindBank = cardBindManager.removePayCardContract(cardId, userId, cardNum, paymentCode);
        PayResult<Map<String, Object>> payResult = PayResultBuilder.create().build();
        if (needUnbindBank){
            payResult =   getResultFromChannel(BANK_CHANNEL_URL + paymentCode + "/unbind", unbindConfigs);
        }
        if (Constants.CARD_UNBIND_NOT_SUPPORT.equals(payResult.getCode())){
            payResult.setCode(ResultCode.SUCCESS.getCode());
            payResult.setMsg(ResultCode.SUCCESS.getMsg());
        }
        cardDutBindManager.dedutAccountSign(userId, cardId+"");
        LOGGER.debug("[unbindRequest][user_id:{}][result:{}]", bindConfigs.get("user_id"), payResult);
        return payResult;
    }


    @Override
    public PayResult<Map<String, Object>> refundRequest(String paymentCode) {
        PayResult<Map<String, Object>> refundResult = getResultFromChannel(BANK_CHANNEL_URL + paymentCode + "/refund", refundConfigs);
        LOGGER.debug("[payRequest][order_code:{}][result:{}]", refundConfigs.get("order_code"), refundResult);
        String refundCode = String.valueOf(refundConfigs.get("refund_code"));
        LOGGER.info("[{}:{type={}, result={}, paltform={}, refundCode={}, serviceCode={}}]", "statistics", "CardRefund", "enter" , "none" , refundCode, paymentCode);
        if (ResultCode.SUCCESS.getCode().equals(refundResult.getCode())){
            Map<String, Object> result = refundResult.getData();
            String fee = String.valueOf(result.get("refund_fee"));
            String thirdRefundCode = String.valueOf(result.get("channel_refund_code"));
            refundService.dealRefundResult(refundCode, thirdRefundCode, Long.parseLong(fee));
        } else if (!Constants.CARD_PAY_IN_PROCESS.equals(refundResult.getCode()) && refundResult.getData() != null){
            String errorMsg = refundResult.getData().get("channel_resp_msg")+"";
            refundService.dealRefundFail(refundCode, errorMsg);
        }
        LOGGER.info("[{}:{type={}, result={}, paltform={}, refundCode={}, serviceCode={}}]", "statistics", "CardRefund", refundResult.getCode() , "none" , refundCode, paymentCode);
        return refundResult;
    }




    private String transTime(){
        return DateHelper.getDateStringByPattern(new Timestamp(System.currentTimeMillis()), "yyyyMMddHHmmss");
    }

}
