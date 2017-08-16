package com.iqiyi.pay.frontend.service;

import com.google.common.collect.Maps;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.service.bank.BankCardInfoService;
import com.iqiyi.pay.frontend.service.order.OrderPayService;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.frontend.utils.Constants;
import com.iqiyi.pay.sdk.BankCardCertification;
import com.iqiyi.pay.sdk.PayUserCard;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * Created by leishengbao on 11/15/16.
 */
@Scope("prototype")
@Component("CARDDUTPAY")
public class CardDutPayment extends AbstractPayment{


    public static final Logger logger = LoggerFactory.getLogger(CardDutPayment.class);

    @Autowired
    private BankCardInfoService bankCardService;
    @Autowired
    private BankChannelPayment bankChannelPayment;
    @Autowired
    private OrderPayService orderService;
    @Autowired
    private PayTradeService payTradeService;

    @Override
    public void configure(Map<String, String> params) {
        configs.put("user_id", params.get("uid"));
        configs.put("subject", params.get("subject"));
        configs.put("order_code", params.get("order_code"));
        configs.put("fee", params.get("fee"));
        configs.put("description", params.get("description"));
        configs.put("extend_params", params.get("extend_params"));
    }

    @Override
    public PayResult<Map<String, Object>> payRequest(String paymentCode) {
        PayResultBuilder builder = PayResultBuilder.create();
        String extendParams = configs.get("extend_params");
        String userId = configs.get("user_id");
        String orderCode = configs.get("order_code");
        long cardId = NumberUtils.toLong(extendParams.substring(Constants.CARD_ID.length()), 0);
        BankCardCertification cardCert = bankCardService.getBankCard(cardId);
        if (cardCert == null) {
            logger.info("card_cert_not _found: card_id: {}", cardId);
            return builder.setResultCode(ResultCode.ERROR_OF_CARD_NOT_EXIT).build();
        }
        PayUserCard card = bankCardService.getPayUserCard(cardId);
        //异常请求
        if (card == null || userId.equals(card.getUserId()) || card.getStatus() != 1) {
            logger.info("card_user_err: uid:{}, card_id: {} card_status:{}", userId, cardId);
            return builder.setResultCode(ResultCode.ERROR_OF_PARAM_INVALID).build();
        }
        //更新支付服务，商户号等信息，Async异步调用
        payTradeService.updatePayTradeByChannelCode(cardId, card.getPayType().toLowerCase(), orderCode, Long.parseLong(userId));
        logger.info("[{}:{type={}, result={}, paltform={}, orderCode={}, serviceCode={}}]", "statistics", "CardDutPay", "enter" , "none" , orderCode, card.getPayType().toLowerCase());
        Map<String, String> params = Maps.newHashMap();
        params.put("uid", userId+"");
        params.put("user_name", cardCert.getOwnerName());
        params.put("card_num", cardCert.getCardNumber());
        params.put("card_type", cardCert.getCardType()+"");
        params.put("card_mobile", cardCert.getBindPhone());
        params.put("cert_type", Constants.ID_CARD_CERT_TYPE);
        params.put("cert_num", cardCert.getOwnerIdNum());
        params.put("order_name", configs.get("subject"));
        params.put("order_description",configs.get("description") == null ? configs.get("subject") : configs.get("description"));
        params.put("subject", configs.get("subject"));
        params.put("description",configs.get("description") == null ? configs.get("subject") : configs.get("description"));
        params.put("order_code", orderCode);
        params.put("fee", configs.get("fee"));
        params.put("contract_id", card.getToken());
        String payServiceCode = card.getPayType();
        bankChannelPayment.configure(params);
        PayResult<Map<String, Object>> ret = bankChannelPayment.payRequest(payServiceCode.toLowerCase());
        logger.info("[{}:{type={}, result={}, paltform={}, orderCode={}, serviceCode={}}]", "statistics", "CardDutPay",
                ret.getCode().equals(Constants.CARD_PAY_IN_PROCESS) ? ResultCode.SUCCESS.getCode() : ret.getCode() , "none" , orderCode, card.getPayType().toLowerCase());
        ret.setData(afterOrderPay(ret, configs.get("order_code")));
        return ret;
    }


    private Map<String, Object> afterOrderPay(PayResult<Map<String, Object>> ret, String orderCode){
        String billNo = Objects.toString(ret.getData().get("trans_seq"));
        orderService.saveBankBillNo(billNo, orderCode);
        Map<String, Object> orderPayedData = Maps.newHashMap();
        orderPayedData.put("order_code", orderCode);
        if (ResultCode.SUCCESS.getCode().equals(ret.getCode())){
            orderService.orderPayed(ret.getData());
        }
        return orderPayedData;
    }
}
