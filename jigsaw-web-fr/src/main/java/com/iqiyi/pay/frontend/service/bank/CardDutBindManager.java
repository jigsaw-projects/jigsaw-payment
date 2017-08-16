package com.iqiyi.pay.frontend.service.bank;

import com.google.common.collect.Maps;
import com.iqiyi.kiwi.utils.HttpClientConnection;
import com.iqiyi.pay.common.security.PayUtils;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.frontend.utils.Constants;
import com.iqiyi.pay.sdk.PayTrade;
import com.iqiyi.pay.sdk.PayTradeExtend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by leishengbao on 11/15/16.
 */
@Component
public class CardDutBindManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(CardDutBindManager.class);

    @Value("${account.dut.qiyi.key}")
    private String dutKey;
    @Value("${account.dut.qiyi.url}")
    private String dutUrl;
    @Value("${account.dedut.qiyi.url}")
    private String dedutUrl;
    @Autowired
    private PayTradeService payTradeService;

    public static final String CARD_BIND_TYPE = "18";

    private void bindCardSign(String userId,  String signCode){
        Map<String, String> params = Maps.newHashMap();
        params.put("uid", userId);
        params.put("buyUserId", userId);
        params.put("signCode", signCode);
        params.put("type", CARD_BIND_TYPE);
        String sign = PayUtils.signMessageRequest(params, dutKey);
        params.put("sign", sign);
        HttpClientConnection hcc = new HttpClientConnection(dutUrl);
        hcc.setReqParams(params);
        try {
            hcc.connect();
            String body = hcc.getBody();
            LOGGER.info("[bindCardSign][userId:{}][signCode:{}][result:{}]", userId, signCode, body);
        }catch (Exception e){
            LOGGER.error("[bindCardSign][userId:{}][signCode:{}][result:{}]", userId, signCode, e);
        }
    }


    /**
     * 处理银行卡代扣逻辑，如果是自动续费用户，保存用户用于支付的cardid到账户签约关系表
     * @param payTrade
     */
    @Async
    public void dealCardDut(PayTrade payTrade){
        String extendParams = payTrade.getExtendParams();
        LOGGER.info("[userId:{}][orderCode:{}][extendParams:{}]", payTrade.getUserId(), payTrade.getOrderCode(), extendParams);
        if (!Constants.CARD_AUTONEW_EXTEND_PARAMS.equals(extendParams)){
            return;
        }
        PayTradeExtend payTradeExtend = payTradeService.queryOrderExtendByOrderCode(payTrade.getOrderCode());
        String cardId = payTradeExtend.getCardId();
        bindCardSign(payTrade.getUserId(), cardId);
    }

    @Async
    public void dedutAccountSign(String userId, String cardId){
        dedutAccountSign(userId, cardId, CARD_BIND_TYPE);
    }



    /**
     * 调用账户中心解除签约关系
     * @param userId
     * @param signCode
     */
    public void dedutAccountSign(String userId, String signCode, String dutType){
        Map<String, String> params = Maps.newHashMap();
        params.put("uid", userId);
        params.put("signCode", signCode);
        params.put("dutType", dutType);
        String sign = PayUtils.signMessageRequest(params, dutKey);
        params.put("sign", sign);
        HttpClientConnection hcc = new HttpClientConnection(dedutUrl);
        hcc.setReqParams(params);
        try {
            hcc.connect();
            String body = hcc.getBody();
            LOGGER.info("[dedutAccountSign][userId:{}][signCode:{}][result:{}]", userId, signCode, body);
        }catch (Exception e){
            LOGGER.error("[dedutAccountSign][userId:{}][signCode:{}][result:{}]", userId, signCode, e);
        }
    }
}
