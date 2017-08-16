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
import com.google.common.collect.Maps;
import com.iqiyi.pay.common.security.PayUtils;
import com.iqiyi.pay.common.utils.StringUtil;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.dto.AppleOrderQueryResponse;
import com.iqiyi.pay.frontend.dto.IapOrderResponse;
import com.iqiyi.pay.frontend.param.IapQueryOrderRefundParam;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.frontend.service.order.PayTradingService;
import com.iqiyi.pay.frontend.service.refund.RefundService;
import com.iqiyi.pay.frontend.web.BaseController;
import com.iqiyi.pay.sdk.AppStoreOrder;
import com.iqiyi.pay.sdk.PayRefundTrade;
import com.iqiyi.pay.sdk.Trade;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

/**
 * Created by leishengbao on 2/10/17.
 */
@RestController
@RequestMapping("/order/")
public class AppleIapQueryOrderRefundController extends BaseController<IapQueryOrderRefundParam>{

    @Value("${apple.order.url}")
    private String appleOrderUrl;
    @Autowired
    private RefundService refundService;
    @Autowired
    private PayTradingService payTradingService;

    @Autowired
    private PayTradeService payTradeService;

    @Value("${vip.refund.url}")
    private String vipRefundUrl;

    @Value("${vip.apple.refund.key}")
    private String vipRefundKey;

    @RequestMapping("/refund/apple")
    protected PayResult<IapOrderResponse> commonInvoke(@Valid IapQueryOrderRefundParam param, BindingResult result) {
        return super.commonInvoke(param, result);
    }

    @Override
    protected void generateResultData(PayResultBuilder builder, IapQueryOrderRefundParam param) {
        Trade payTrade = payTradingService.getTradeByCode(param.getOrder_code());
        if (payTrade == null){
            builder.setResultCode(ResultCode.ERROR_OF_ORDER_NOT_EXIT);
            return;
        }
        AppleOrderQueryResponse appleOrderQueryResponse = queryAppleOrder(payTrade,param);
        if(appleOrderQueryResponse != null && appleOrderQueryResponse.isRefunded()) {
            createRefundOrder(payTrade, appleOrderQueryResponse);
        }
    }


    /**
     * 查询IAP订单信息
     * @param payTrade
     * @return
     */
    private AppleOrderQueryResponse queryAppleOrder(Trade payTrade,IapQueryOrderRefundParam param){
        AppStoreOrder appStoreOrder = payTradeService.getAppStoreOrder(payTrade.getCode());
        String appType=appStoreOrder.getAppType();
        if(StringUtils.isBlank(appType)){
            appType= getAppType(appStoreOrder, payTrade);
            if(StringUtils.isBlank(appType)) {
                LOGGER.error("[module:queryAppleOrder][orderCode:{}][message:{}]", appStoreOrder.getOrderCode(), "app_type is  empty,But it can't be empty here！");
                return null;
            }
        }
        OkHttpClient client = new OkHttpClient();
        JSONObject jo = new JSONObject();
        try {
            String receipt = appStoreOrder.getReceiptData();
            String encodeReceipt = URLEncoder.encode(receipt, "UTF-8");
            jo.put("receipt", encodeReceipt);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("[encode error, order_code:{}]", param.getOrder_code());
            return null;
        }
        jo.put("transaction_id", payTrade.getThirdTradeCode());
        jo.put("app_type", appType);

        if(StringUtils.isNotBlank(param.getType())&&"1".equals(param.getType())){
            jo.put("request_type", "SUBSCRIBE");
        }else {
            jo.put("request_type", "PAY");
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jo.toJSONString());
        Request request = new Request.Builder()
                .url(appleOrderUrl)
                .post(requestBody)
                .build();
        try {
            String body = client.newCall(request).execute().body().string();
            LOGGER.debug("[module:queryAppleRefundOrder][orderCode:{}][body:{}]", payTrade.getCode(), body);
            return JSONObject.parseObject(body, AppleOrderQueryResponse.class);
        } catch (IOException e) {
            LOGGER.error("queryAppleRefundOrder failed, [param:{}]",  e);
        }
        return null;
    }

    /**
     * 创建退款订单
     * @param payTrade
     * @param response
     */
    private void createRefundOrder(Trade payTrade, AppleOrderQueryResponse response){
        PayRefundTrade.Builder builder = PayRefundTrade.newBuilder();
        long fee = payTrade.getFee();
        String refundCode = StringUtil.createUniqueCode();
        String partnerRefundNo = queryVipRefundCode(payTrade.getPartnerOrderNo(), response, refundCode);
        if (partnerRefundNo == null){
            return;
        }
        Date refundDate = null;
        try {
             refundDate = DateUtils.parseDate(response.getCancellation_date(), "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            LOGGER.error("[orderCode:{}]", payTrade.getCode(), e);
        }
        builder.setCreateTime(System.currentTimeMillis())
                .setUpdateTime(System.currentTimeMillis())
                .setRefundTime(refundDate != null ? refundDate.getTime() : System.currentTimeMillis())
                .setRefundCode(refundCode)
                .setSignCorpId(payTrade.getSignCorpId())
                .setDestPayType(payTrade.getDestPayType())
                .setFee(fee)
                .setOrderCode(payTrade.getCode())
                .setPartnerId(payTrade.getPartnerId())
                .setPartnerOrderNo(payTrade.getPartnerOrderNo())
                .setPartnerRefundNo(partnerRefundNo)
                .setSubject(payTrade.getOrderTitle())
                .setStatus(1)
                .setUserId(payTrade.getSubId()+"")
                .setThirdTradeCode(payTrade.getThirdTradeCode());
        if (payTrade.hasPartnerAccountId()){
            builder.setAccountId(payTrade.getPartnerAccountId());
        }
        refundService.createPayRefundTrade(builder.build());
    }

    /**
     * 查询会员到退款单号
     * @param partnerOrderNo
     * @param response
     * @return
     */
    private String queryVipRefundCode(String partnerOrderNo, AppleOrderQueryResponse response, String refundCode){
        OkHttpClient client = new OkHttpClient();
        Map<String, String> params = Maps.newHashMap();
        params.put("appId", response.getProduct_id());
        params.put("tradeNo", response.getTransaction_id());
        params.put("orderCode", partnerOrderNo);
        params.put("refundCode", refundCode);
        params.put("refundTime", response.getCancellation_date());
        params.put("reason", "iap-query-refund");
        params.put("refundPerson", "appleiap");
        String sign = PayUtils.signMessageRequest(params, vipRefundKey);
        params.put("sign", sign);
        Request request = new Request.Builder()
                .url(new StringBuffer(vipRefundUrl).append("?").append(Joiner.on("&").withKeyValueSeparator("=").join(params)).toString())
                .get()
                .build();
        try {
            LOGGER.info("[vipOrderUrl:{}][params:{}]", vipRefundUrl, params);
            String body = client.newCall(request).execute().body().string();
            LOGGER.info("[partnerOrderNo:{}][getVipOrder body:{}]", partnerOrderNo, body);
            JSONObject jo = JSON.parseObject(body);
            if (!jo.get("code").equals(ResultCode.SUCCESS.getCode())){
                return null;
            }
            JSONObject data = (JSONObject) jo.get("data");
            return data.get("refund_code").toString();
        }catch (Exception e){
            LOGGER.error("[partnerOrderNo:{}]", partnerOrderNo, e);
        }
        return null;
    }

    private String getAppType(AppStoreOrder appStoreOrder, Trade payTrade) {
        String appType=getAppTypeByExtraParams(payTrade.getExtraParams());
        if(StringUtils.isBlank(appType)) {
            LOGGER.info("[getAppType] get app_type from original transaction.");
            String orgTransactionId =getOriginalTransactionIdByTrade(payTrade);
            if(StringUtils.isNotBlank(orgTransactionId)) {
                LOGGER.info("[getAppType] get app_type from original_transaction={}.",orgTransactionId);
                appType=getAppTypeByOriginTransaction(payTrade.getSubId(),orgTransactionId);
            }
        }
        LOGGER.info("[getAppType] transactionId:{},app_type:{}", appStoreOrder.getTransactionId(), appType);
        return appType;
    }

    //从扩展参数字段中获取app_type
    private String getAppTypeByExtraParams(String extraParams) {
        String appType = "";
        try {
            Map<String, String> exParams = Splitter.on("&").withKeyValueSeparator('=').split(extraParams);
            if (exParams != null && exParams.containsKey("subParam_appType")) {
                appType = exParams.get("subParam_appType");
            }
        } catch (Exception e) {
            LOGGER.error("[getAppTypeFromExtraParams][extendParams:{} errMsg:{}]", extraParams, e);
        } finally {
            return appType;
        }
    }

    //从支付订单中获取原始交易流水即签约号
    private String getOriginalTransactionIdByTrade(Trade trade) {
        String orgTransactionId = "";
        String extendParams = trade.getExtraParams();
        try {
            Map<String, String> exParams = Splitter.on("&").withKeyValueSeparator('=').split(extendParams);
            if (exParams != null && exParams.containsKey("originalTransactionId")) {
                orgTransactionId = exParams.get("originalTransactionId");
                return orgTransactionId;
            } else {
                extendParams = trade.getExtraCommonParam();
                exParams = Splitter.on("&").withKeyValueSeparator('=').split(extendParams);
                if (exParams != null && exParams.containsKey("originalTransactionId")) {
                    orgTransactionId = exParams.get("originalTransactionId");
                    return orgTransactionId;
                }
            }
        } catch (Exception e) {
            LOGGER.error("[getOriginalTransactionIdByTrade][Params:{} errMsg:{}]", extendParams, e);
        } finally {
            return orgTransactionId;
        }
    }

    //根据签约号获取app_type
    private String getAppTypeByOriginTransaction(Long userId,String originTransactionId) {
        Trade trade = payTradingService.getTradeByThirdTradeCode(userId, originTransactionId);
        if (trade != null) {
            return getAppTypeByExtraParams(trade.getExtraParams());
        } else {
            return null;
        }
    }

    @Override
    protected boolean checkSign(IapQueryOrderRefundParam param) {
        return true;
    }
}
