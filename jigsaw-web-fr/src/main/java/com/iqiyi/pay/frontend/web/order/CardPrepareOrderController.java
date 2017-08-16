/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.web.order;

import com.iqiyi.kiwi.utils.StringUitl;
import com.iqiyi.pay.common.utils.StringUtil;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.dto.PrepareOrderResponse;
import com.iqiyi.pay.frontend.param.CardPrepareOrderParam;
import com.iqiyi.pay.frontend.service.accesser.PartnerService;
import com.iqiyi.pay.frontend.web.BaseController;
import com.iqiyi.pay.sdk.PartnerPayKey;
import com.iqiyi.pay.sdk.PayAccesser;
import com.iqiyi.pay.sdk.PayTrade;
import com.iqiyi.pay.sdk.PaymentType;
import com.iqiyi.pay.sdk.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Created by leishengbao on 3/3/17.
 */
@RestController
@RequestMapping("/card")
public class CardPrepareOrderController extends BaseController<CardPrepareOrderParam>{

    @Autowired
    PartnerService partnerService;
    @Autowired
    com.iqiyi.pay.client.TradeService tradeService;

    public final static String WSC_SUBJECT = "wallet_sign_consume";

    public final static String PAY_TYPE = "CARDPAY";

    public final static String PARTNER = "wallet";

    @Override
    @RequestMapping("/prepareOrder")
    protected PayResult<PrepareOrderResponse> commonInvoke(@Valid CardPrepareOrderParam param, BindingResult result) {
        return super.commonInvoke(param, result);
    }

    @Override
    protected void generateResultData(PayResultBuilder builder, CardPrepareOrderParam param) {
        PayTrade trade = createOrder(param);
        PrepareOrderResponse response = new PrepareOrderResponse();
        response.setOrder_code(trade.getOrderCode());
        builder.setData(response);
    }



    private PayTrade createOrder(CardPrepareOrderParam param){
        PayTrade.Builder payTradeBuilder = PayTrade.newBuilder();
        PaymentType paymentType = partnerService.getPaymentType(PAY_TYPE);
        PayAccesser accesser = partnerService.getAccesser(PARTNER);
        PartnerPayKey payKey = partnerService.syncGetPartnerPayKey(PARTNER);
        payTradeBuilder.setOrderCode(StringUtil.createUniqueCode());
        payTradeBuilder.setCreateTime(System.currentTimeMillis());
        payTradeBuilder.setFee(1);
        payTradeBuilder.setSubFee(1);
        payTradeBuilder.setSubRealFee(1);
        payTradeBuilder.setRealFee(1);
        payTradeBuilder.setFeeUnit(1);
        payTradeBuilder.setPartnerOrderNo("W"+ StringUitl.createUniqueCode());
        payTradeBuilder.setPartnerId(accesser.getId());
        payTradeBuilder.setSubject(WSC_SUBJECT);
        payTradeBuilder.setReturnUrl(param.getReturn_url());
        payTradeBuilder.setStatus(2);
        payTradeBuilder.setUpdateTime(System.currentTimeMillis());
        payTradeBuilder.setUserId(param.getUser_id());
        payTradeBuilder.setSourcePayType(paymentType.getId());
        payTradeBuilder.setDestPayType(paymentType.getServiceId());
        payTradeBuilder.setCurrentKey(payKey.getId());
        TradeService.CreatePayTradeRequest.Builder request = TradeService.CreatePayTradeRequest.newBuilder();
        request.setPayTrade(payTradeBuilder.build());
        tradeService.createPayTrade(request.build());
        return payTradeBuilder.build();
    }
}
