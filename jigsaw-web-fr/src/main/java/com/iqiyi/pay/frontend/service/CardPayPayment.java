package com.iqiyi.pay.frontend.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.iqiyi.pay.client.ConfigService;
import com.iqiyi.pay.client.EntityService;
import com.iqiyi.pay.client.RouteService;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.service.accesser.PartnerService;
import com.iqiyi.pay.frontend.service.passport.PassPortUserInfoService;
import com.iqiyi.pay.sdk.PayUserCard;
import com.iqiyi.pay.sdk.service.EntityService.UserPwdQueryByUidRequest;
import com.iqiyi.pay.sdk.service.EntityService.UserPwdQueryByUidResponse;
import com.iqiyi.pay.sdk.service.RouteService.QueryPayCardListByUidAndStatusRequest;
import com.iqiyi.pay.sdk.service.RouteService.QueryPayCardListByUidAndStatusResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by leishengbao on 8/19/16.
 */
@Component("CARDPAY")
@Scope("prototype")
public class CardPayPayment extends AbstractPayment{

    @Autowired
    EntityService entityService;
    @Autowired
    RouteService routeService;
    @Autowired
    ConfigService configService;
    @Autowired
    PartnerService partnerService;
    @Autowired
    private PassPortUserInfoService passPortUserInfoService;

    @Override
    public void configure(Map<String, String> params) {
        configs.put("uid", params.get("uid"));
        configs.put("order_code", params.get("order_code"));
        configs.put("partner_order_no", params.get("partner_order_no"));
        configs.put("subject", params.get("subject"));
        configs.put("service_id", params.get("service_id"));
        configs.put("pay_type", params.get("pay_type"));
        configs.put("fee", params.get("fee"));
        configs.put("partner", params.get("partner"));
    }



    @Override
    public PayResult<Map<String, Object>> payRequest(String paymentCode) {
        PayResultBuilder<Map<String, Object>> builder = PayResultBuilder.create();
        Long uid = Long.parseLong(configs.get("uid"));
        Map<String, Object> data = Maps.newHashMap();
        UserPwdQueryByUidRequest request = UserPwdQueryByUidRequest.newBuilder().setUid(uid).build();
        UserPwdQueryByUidResponse response = entityService.queryUserPwdByUid(request);
        data.put("is_wallet_pwd_set", response.hasUser()  ? "1" : "0");
        List<Map<String, String>> cards = queryCardList(uid, configs.get("partner"));
        data.put("cards", cards);
        data.put("is_mobile_set", StringUtils.isBlank(passPortUserInfoService.getUserInfoByUserId(String.valueOf(uid)).getPhone()) ? "0" : "1");
        data.put("order_status", "2");
        data.put("fee", configs.get("fee"));
        data.putAll(configs);
        return builder.setResultCode(ResultCode.SUCCESS).setData(data).build();
    }


    public Map<String, Object> noMibleResult(Long uid){
        Map<String, Object> data = Maps.newHashMap();
        UserPwdQueryByUidRequest request = UserPwdQueryByUidRequest.newBuilder().setUid(uid).build();
        UserPwdQueryByUidResponse response = entityService.queryUserPwdByUid(request);
        data.put("is_wallet_pwd_set", response.hasUser()  ? "1" : "0");
        List<Map<String, String>> cards = Lists.newArrayList();
        data.put("cards", cards);
        data.put("is_mobile_set", "0");
        data.put("order_status", "2");
        return data;
    }


    private List<Map<String, String>> queryCardList(Long uid, String partner){
        QueryPayCardListByUidAndStatusRequest request = QueryPayCardListByUidAndStatusRequest.newBuilder().setUserId(uid).setStatus(1).build();
        QueryPayCardListByUidAndStatusResponse response = routeService.queryPayCardListByUidAndStatus(request);
        List<PayUserCard> cards = response.getPayUserCardList();
        List<Map<String, String>> result = Lists.newArrayList();
        if (cards == null || cards.size() == 0){
            return result;
        }
        List<PayUserCard> newCards = Lists.newArrayList(cards);
        newCards.sort(new Comparator<PayUserCard>() {
            @Override
            public int compare(PayUserCard p1, PayUserCard p2) {
                return p1.getUpdateTime() < p2.getUpdateTime() ? 1 : -1;
            }
        });
        newCards.forEach(card -> {
            if ("wallet".equals(partner) && card.getCardType() == 2){
                return;
            }
            Map<String, String> cardInfo = Maps.newHashMap();
            cardInfo.put("card_id", String.valueOf(card.getId()));
            cardInfo.put("card_num_last", card.getCardNumLast());
            cardInfo.put("card_type", String.valueOf(card.getCardType()));
            cardInfo.put("bank_code", card.getBankCode());
            String cardTypeString = "银行卡";
            String cardTypeCode = "UNKNOWN";
            if (card.hasCardType() && card.getCardType() == 1){
                cardTypeCode = "DEBIT";
                cardTypeString = "借记卡";
            } else if (card.hasCardType() && card.getCardType() == 2){
                cardTypeCode = "DEBIT";
                cardTypeString = "信用卡";
            }
            cardInfo.put("card_type_code", cardTypeCode);
            cardInfo.put("card_type", cardTypeString);
            cardInfo.put("bank_name", card.getBankName());
            cardInfo.put("bank_icon_url", card.getBankIconUrl());
            cardInfo.put("bank_icon", card.getBankIconUrl());
            result.add(cardInfo);
        });
        return result;
    }
}
