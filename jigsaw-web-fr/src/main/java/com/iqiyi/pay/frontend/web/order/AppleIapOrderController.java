/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.web.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.iqiyi.pay.client.TradeService;
import com.iqiyi.pay.client.TradingService;
import com.iqiyi.pay.common.security.PayUtils;
import com.iqiyi.pay.common.utils.StringUtil;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.dto.AppleOrderQueryResponse;
import com.iqiyi.pay.frontend.dto.IapOrderResponse;
import com.iqiyi.pay.frontend.param.IapOrderParam;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.service.accesser.PartnerService;
import com.iqiyi.pay.frontend.service.bank.CardDutBindManager;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.frontend.service.order.PayTradingService;
import com.iqiyi.pay.frontend.web.BaseController;
import com.iqiyi.pay.sdk.AppStoreOrder;
import com.iqiyi.pay.sdk.PartnerPayKey;
import com.iqiyi.pay.sdk.PayAccesser;
import com.iqiyi.pay.sdk.PayTrade;
import com.iqiyi.pay.sdk.PaymentType;
import com.iqiyi.pay.sdk.Trade;
import com.iqiyi.pay.sdk.TradeStatus;
import com.iqiyi.pay.sdk.service.TradeService.CreatePayTradeRequest;
import com.iqiyi.pay.sdk.service.TradeService.GetAppStoreOrderByTrsIdRequest;
import com.iqiyi.pay.sdk.service.TradeService.GetAppStoreOrderByTrsIdResponse;
import com.iqiyi.pay.sdk.service.TradeService.GetAppStoreOrderRequest;
import com.iqiyi.pay.sdk.service.TradeService.GetAppStoreOrderResponse;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by leishengbao on 1/12/17.
 */
@RestController
@RequestMapping("/apple/")
public class AppleIapOrderController extends BaseController<IapOrderParam>{

    @Value("${apple.paymentService.id}")
    private int applePaymentServiceTypeId;
    @Value("${dutUser.update.url}")
    private String updateDutUserUrl;
    @Value("${account.dut.qiyi.key}")
    private String dutKey;
    @Value("${apple.order.url}")
    private String appleOrderUrl;
    @Value("${vip.order.url}")
    private String vipAppleOrderUrl;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private TradingService tradingService;
    @Autowired
    private PayTradeService payTradeService;
    @Autowired
    private CardDutBindManager cardDutBindManager;
    @Autowired
    private PayTradingService payTradingService;
    @Value("${apple.order.afterType.beforeDays}")
    private int afterTypeBeforeDays;
    @Value("${apple.order.beforeType.beforeDays}")
    private int beforeTypeBeforeDays;
    @Value("${apple.order.afterDays}")
    private int afterDays;

    public static final String VIP_PARTNER = "qiyue";

    public static final String APPLE_IAP_PAY_TYPE = "APPLEIAPDUT";

    @RequestMapping("/order/query")
    public PayResult<IapOrderResponse> iapOrder(@Valid IapOrderParam param, BindingResult result){
        return super.commonInvoke(param, result);
    }

    @Override
    protected void generateResultData(PayResultBuilder builder, IapOrderParam param) {
        PayTrade trade = payTradeService.queryOrderByCode(param.getOrder_code());
        String extendParams = null;
        if (trade == null){
            Trade esTrade = payTradingService.getTradeByCode(param.getOrder_code());
            extendParams = esTrade.getExtraParams();
            if (StringUtils.isBlank(extendParams)) {
                LOGGER.warn("[appleOrderQuery][es extendParams null][orderCode:{}][param:{}]", param.getOrder_code(), param);
                return;
            }
        }else {
            extendParams = trade.getExtendParams();
        }
        Map<String, String> exParams = Splitter.on("&").withKeyValueSeparator('=').split(extendParams);
        String receipt = getReceiptData(param.getOrder_code(), extendParams);//real receipt, no encode
        String appType = "";
        if(exParams != null && exParams.containsKey("subParam_appType")) {
            appType = exParams.get("subParam_appType");
        }
        AppleOrderQueryResponse queryResult = queryAppleOrder(receipt, exParams.get("subParam_appType"), param);
        if (queryResult == null || !queryResult.validResponse()){
            return;
        }
        if (queryResult.isAutoNewed(param, afterDays, afterTypeBeforeDays, beforeTypeBeforeDays)){//
            boolean createResult =createPayTrade(param.getUser_id(), queryResult.getProduct_id(),
                    queryResult.getTransaction_id(), queryResult.getOriginal_transaction_id(), receipt, appType);
            if (createResult){
                updateAccountDutUser(param.getUser_id(), queryResult.getExpires_date(), param.getDut_type());
            }
        }else if (param.needToDeDut()){
            cardDutBindManager.dedutAccountSign(param.getUser_id(), param.getSign_code(), param.getDut_type());
        }
    }

    private void updateAccountDutUser(String userId, String expireTime, String dutType){
        OkHttpClient client = new OkHttpClient();
        Map<String, String> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("dutType", dutType);
        params.put("expireTime", expireTime);
        String sign = PayUtils.signMessageRequest(params, dutKey);
        params.put("sign", sign);
        Request request = new Request.Builder()
                .url(new StringBuffer(updateDutUserUrl).append("?").append(Joiner.on("&").withKeyValueSeparator("=").join(params)).toString())
                .get()
                .build();
        try {
            String body = client.newCall(request).execute().body().string();
            LOGGER.info("signCode:{}, result:{}", userId, body);
        } catch (IOException e) {
            LOGGER.error("signCode:{}", userId);
        }
    }



    private boolean createPayTrade(String uid, String productId, String transactionId, String originalTradeNo, String receipt,String appType){
        AppStoreOrder appStoreOrder = queryAppStoreOrderByTxId(transactionId);
        if (appStoreOrder != null){
            LOGGER.info("[transactionId has used, transaction_id:{}, order_code:{}]", transactionId, appStoreOrder.getOrderCode());
            return false;
        }
        PartnerPayKey payKey = partnerService.syncGetPartnerPayKey(VIP_PARTNER);
        PayAccesser accesser  = partnerService.getAccesser(VIP_PARTNER);
        PaymentType paymentType = partnerService.getPaymentType(APPLE_IAP_PAY_TYPE);

        String orderCode = StringUtil.createUniqueCode();

        String lastVipOrderCode = getLastVipOrderCode(uid, originalTradeNo);
        VipOrderInfo vipOrderInfo = getVipOrder(lastVipOrderCode, orderCode, productId, transactionId, uid, originalTradeNo);
        if (vipOrderInfo == null){
            LOGGER.info("[vip order info failed] [uid:{}][transactionId:{}]", uid, transactionId);
            return false;
        }
        if ("A00001".equals(vipOrderInfo.code)){
            LOGGER.info("[vip order info A00001] [uid:{}][transactionId:{}]", uid, transactionId);
            return false;
        }
        long fee = vipOrderInfo.fee;
        String partnerOrderNo = vipOrderInfo.partnerOrderNo;
        String subject = vipOrderInfo.subject;


        PayTrade.Builder payTradeBuilder = PayTrade.newBuilder();
        payTradeBuilder.setOrderCode(orderCode);
        payTradeBuilder.setThirdTradeCode(transactionId);
        payTradeBuilder.setCreateTime(System.currentTimeMillis());
        payTradeBuilder.setFee(fee);
        payTradeBuilder.setSubFee(fee);
        payTradeBuilder.setSubRealFee(fee);
        payTradeBuilder.setRealFee(fee);
        payTradeBuilder.setFeeUnit(1);
        payTradeBuilder.setPartnerOrderNo(partnerOrderNo);
        payTradeBuilder.setPartnerId(accesser.getId());
        payTradeBuilder.setSubject(subject);
        payTradeBuilder.setStatus(1);
        payTradeBuilder.setUpdateTime(System.currentTimeMillis());
        payTradeBuilder.setPayTime(System.currentTimeMillis());
        payTradeBuilder.setUserId(uid);
        payTradeBuilder.setSourcePayType(paymentType.getId());
        payTradeBuilder.setDestPayType(applePaymentServiceTypeId);
        payTradeBuilder.setCurrentKey(payKey.getId());
        payTradeBuilder.setExtraCommonParam("originalTransactionId="+originalTradeNo);
        CreatePayTradeRequest.Builder request = CreatePayTradeRequest.newBuilder();
        request.setPayTrade(payTradeBuilder.build());
        tradeService.createPayTrade(request.build());
        payTradeService.saveAppstoreOrder(transactionId, orderCode, receipt, appType, uid, productId, originalTradeNo);
        return true;
    }


    private VipOrderInfo getVipOrder(String lastVipOrderCode, String orderCode, String productId, String transactionId, String uid, String originalTradeNo){
        if (lastVipOrderCode == null){
            LOGGER.info("[getLastVipOrderCode failed][uid:{}][originalTradeNo:{}]", uid, originalTradeNo);
            return null;
        }
        OkHttpClient client = new OkHttpClient();
        Map<String, String> params = Maps.newHashMap();
        params.put("app_id", productId);
        params.put("trade_no", transactionId);
        params.put("payCenterOrderCode", orderCode);
        params.put("uid", uid);
        params.put("original_trade_no", originalTradeNo);
        params.put("orderCode", lastVipOrderCode);
        Request request = new Request.Builder()
                .url(new StringBuffer(vipAppleOrderUrl).append("?").append(Joiner.on("&").withKeyValueSeparator("=").join(params)).toString())
                .get()
                .build();
        try {
            LOGGER.info("[vipOrderUrl:{}][params:{}]", vipAppleOrderUrl, params);
            String body = client.newCall(request).execute().body().string();
            LOGGER.info("[orderCode:{}][lastVipOrderCode:{}][getVipOrder body:{}]", orderCode, lastVipOrderCode, body);
            JSONObject jo = JSON.parseObject(body);
            if (!jo.get("code").equals(ResultCode.SUCCESS.getCode())){
                return null;
            }
            JSONObject data = (JSONObject) jo.get("data");

            VipOrderInfo vipOrderInfo = new VipOrderInfo();
            vipOrderInfo.code = jo.get("code").toString();
            vipOrderInfo.partnerOrderNo = data.get("orderCode").toString();
            vipOrderInfo.subject = data.get("subject").toString();
            vipOrderInfo.fee = Long.parseLong(data.get("fee").toString());
            return vipOrderInfo;
        } catch (IOException e) {
            LOGGER.error("[getVipOrderInfo failed][uid:{}][transactionId:{}]", uid, transactionId);
        }
        return null;
    }

    private AppleOrderQueryResponse queryAppleOrder(String reciept, String appType, IapOrderParam param){
        OkHttpClient client = new OkHttpClient();
        JSONObject jo = new JSONObject();
        try {
            String encodeReceipt = URLEncoder.encode(reciept, "UTF-8");
            jo.put("receipt", encodeReceipt);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("[encode error, order_code:{}]", param.getOrder_code());
        }
        jo.put("original_transaction_id", param.getSign_code());
        jo.put("app_type", appType);
        jo.put("request_type", "SUBSCRIBE_LATEST");
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jo.toJSONString());
        Request request = new Request.Builder()
                .url(appleOrderUrl)
                .post(requestBody)
                .build();
        try {
            String body = client.newCall(request).execute().body().string();
            LOGGER.debug("[module:queryAppleOrder][body:{}]", body);
            return JSONObject.parseObject(body, AppleOrderQueryResponse.class);
        } catch (IOException e) {
            LOGGER.error("queryAppleOrder failed, [iapOrderParam:{}]", param, e);
        }
        return null;
    }


    private String getLastVipOrderCode(String uid, String orginalTradeNo){
        com.iqiyi.pay.sdk.service.TradingService.QueryTradeListBySubjectRequest request = com.iqiyi.pay.sdk.service.TradingService.QueryTradeListBySubjectRequest.newBuilder()
                .setSubId(Long.parseLong(uid)).setStatus(TradeStatus.PAID).setOffset(0).setLimit(100).addAllDestPayType(Lists.newArrayList(applePaymentServiceTypeId)).build();
        com.iqiyi.pay.sdk.service.TradingService.QueryTradeListBySubjectResponse response = tradingService.queryTradeListBySubject(request);
        List<Trade> trades = response.getTradeList();
        try{
            Trade trade = trades.stream().filter(td -> td.getExtraCommonParam() != null && td.getExtraCommonParam().contains(orginalTradeNo)).findAny().get();
            return trade != null ? trade.getPartnerOrderNo() : null;
        }catch (NoSuchElementException e){
            return null;
        }
    }


    /**
     * 获取到的是真实的，未做encode的receipt
     * @param
     * @return
     */
    private String getReceiptData(String orderCode, String extendParams){
        GetAppStoreOrderRequest request = GetAppStoreOrderRequest.newBuilder()
                .setOrderCode(orderCode).build();
        GetAppStoreOrderResponse response = tradeService.getAppStoreOrder(request);
        if (response.hasAppStoreOrder() && StringUtils.isNotBlank(response.getAppStoreOrder().getReceiptData())){
            return response.getAppStoreOrder().getReceiptData();
        }
        Map<String, String> exParams = Splitter.on("&").withKeyValueSeparator('=').split(extendParams);
        try {
            return URLDecoder.decode(exParams.get("subParam_receipt"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("[decode error, order_code:{}]", orderCode);
        }
        return exParams.get("subParam_receipt");
    }


    private String queryReceiptData(String orderCode){
        GetAppStoreOrderRequest request = GetAppStoreOrderRequest.newBuilder()
                .setOrderCode(orderCode).build();
        GetAppStoreOrderResponse response = tradeService.getAppStoreOrder(request);
        if (!response.hasAppStoreOrder()){
            return null;
        }
        return response.getAppStoreOrder().getReceiptData();
    }


    private AppStoreOrder queryAppStoreOrderByTxId(String transactionId){
        GetAppStoreOrderByTrsIdRequest request = GetAppStoreOrderByTrsIdRequest.newBuilder()
                .setTransactionId(Long.parseLong(transactionId)).build();
        GetAppStoreOrderByTrsIdResponse response=null;
        try{
            response = tradeService.getAppStoreOrderByTrsId(request);
        }catch (Exception e){
            LOGGER.info("ERROR_GET_APPLE_ORDER_FROM_MONGODB [transactionId:{}] [ERR_MSG:{}] [ERR_CAUSE:{}]", transactionId, e.getMessage(), e.getCause());
        }
        if (response!=null && response.hasAppStoreOrder()){
            return response.getAppStoreOrder();
        }
        return null;
    }
    @Override
    protected boolean checkSign(IapOrderParam param) {
        return true;
    }

    class VipOrderInfo{
        String code;
        String partnerOrderNo;
        String subject;
        long fee;
    }

}
