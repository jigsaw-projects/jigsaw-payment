/**
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 **/

package com.iqiyi.pay.frontend.web;

import com.iqiyi.pay.client.TradeService;
import com.iqiyi.pay.common.security.PayUtils;
import com.iqiyi.pay.common.utils.StringUtil;
import com.iqiyi.pay.common.utils.result.IResult;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.param.PayFrontendParam;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.service.CardPayPayment;
import com.iqiyi.pay.frontend.service.Payment;
import com.iqiyi.pay.frontend.service.PaymentFactory;
import com.iqiyi.pay.frontend.service.accesser.PartnerService;
import com.iqiyi.pay.frontend.service.order.OrderLogService;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.frontend.service.passport.PassPortUserInfoService;
import com.iqiyi.pay.sdk.PartnerPayKey;
import com.iqiyi.pay.sdk.PayAccesser;
import com.iqiyi.pay.sdk.PayServiceAccountRoute;
import com.iqiyi.pay.sdk.PayTrade;
import com.iqiyi.pay.sdk.PayTradeTrans;
import com.iqiyi.pay.sdk.PaymentType;
import com.iqiyi.pay.sdk.service.TradeService.CreatePayTradeRequest;
import com.iqiyi.pay.sdk.service.TradeService.CreatePayTradeTransRequest;
import com.iqiyi.pay.sdk.service.TradeService.UpdatePayTradeByOrderCodeRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * v1 frontend gateway
 *
 * @author Li Hengjun<lihengjun@qiyi.com>
 * @version 1.0.0  7/4/16
 **/
@Controller
@RequestMapping("/frontend/")
public class PayFrontendController extends BaseController<PayFrontendParam>{

    @Autowired
    PaymentFactory paymentFactory;
    @Autowired
    TradeService tradeService;
    @Autowired
    PartnerService partnerService;
    @Autowired
    PassPortUserInfoService passPortUserInfoService;
    @Autowired
    CardPayPayment cardPayPayment;
    @Autowired
    PayTradeService payTradeService;
    @Autowired
    private OrderLogService orderLogService;

    @RequestMapping(value = "pay", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String frontend(@Valid PayFrontendParam param, BindingResult result){
        PayResult payResult = super.commonInvoke(param, result);
        return payResult.toJson();
    }


    /**
     * 1. check partner , get paykey and check sign
     * 2. find routers
     * 3. create and save order
     * 4. invoke payment channel
     * @param builder
     * @param param
     */
    @Override
    protected void generateResultData(PayResultBuilder builder, PayFrontendParam param) {
        if (!validateRequest(builder, param)){
            return;
        }
        Future<PartnerPayKey> partnerPayKeyFuture = partnerService.getPartnerPayKey(param.getPartner());
        PayAccesser accesser  = partnerService.getAccesser(param.getPartner());
        if (accesser == null){
            builder.setResultCode(ResultCode.ERROR_OF_ACCESSER_NOT_EXIST);
            return;
        }
        PaymentType paymentType = partnerService.getPaymentType(param.getPay_type());
        if (paymentType == null){
            builder.setResultCode(ResultCode.ERROR_OF_PAYTYPE_NOT_EXIST);
            return;
        }
        if (!partnerService.hasAccesserPayment(param.getPartner(), paymentType.getId())){
            builder.setResultCode(ResultCode.ERROR_OF_ACCESSER_PAYTYPE_NOT_SUPPORT);
            return;
        }
        PayServiceAccountRoute serviceAccountRoute = partnerService.getPayRoute(paymentType.getId(), param.getPartner());
        if (serviceAccountRoute == null){
            builder.setResultCode(ResultCode.ERROR_OF_ROUTE_NOT_EXIST);
            return;
        }
        PayTrade payTrade = payTradeService.queryPayTadeByPartnerOrderNo(param.getPartner_order_no(), accesser.getId());
        if (payTrade != null && payTrade.getStatus() == 1){
            builder.setResultCode(ResultCode.ERROR_OF_ORDER_PAYED);
            return;
        }
        PartnerPayKey partnerPayKey = null;
        try {
            partnerPayKey = partnerPayKeyFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String orderCode = payTrade != null ? payTrade.getOrderCode() : StringUtil.createUniqueCode();
        backUpOrderTrans(payTrade, serviceAccountRoute);//备份原支付渠道信息，解决切换支付方式的问题
        createOrUpdateOrder(payTrade == null, param, orderCode, accesser.getId(),
                serviceAccountRoute.getAccountId(), serviceAccountRoute.getSignCorpId(),paymentType.getId(), serviceAccountRoute.getServiceId(), partnerPayKey.getId());
        String paymentCode = serviceAccountRoute.getPaymentTypePayCode();
        Payment payment = paymentFactory.getPayment(paymentCode);

        Map<String, String> params = PayUtils.genMapByRequestParas(getRequest().getParameterMap());
        params.put("order_code", orderCode);
        params.put("pay_type", String.valueOf(paymentType.getId()));
        params.put("service_id", String.valueOf(serviceAccountRoute.getServiceId()));
        params.put("subject", param.getSubject());
        params.put("fee", String.valueOf(param.getFee()));
        params.put("partner", params.get("partner"));
        params.put("extend_params", param.getExtend_params());
        params.put("mobile", param.getMobile());
        params.put("create_time", DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        payment.configure(params);
        PayResult<Map<String, Object>> result = payment.payRequest(paymentCode);
        builder.setData(result.getData())
                .setResultCode(new IResult() {
                    @Override
                    public String getCode() {
                        return result.getCode();
                    }

                    @Override
                    public String getMsg() {
                        return result.getMsg();
                    }
                });
    }

    /**
     * 备份原支付渠道信息，解决切换支付方式的问题
     * @param payTrade
     * @param serviceAccountRoute
     */
    private void backUpOrderTrans(PayTrade payTrade , PayServiceAccountRoute serviceAccountRoute){
        if (payTrade == null){
            return;
        }
        if (payTrade.getDestPayType() == serviceAccountRoute.getServiceId()){
            return;
        }
        PayTradeTrans.Builder builder = PayTradeTrans.newBuilder().setOrderCode(payTrade.getOrderCode())
                .setSourcePayType(payTrade.getSourcePayType()).setDestPayType(payTrade.getDestPayType())
                .setCreateTime(System.currentTimeMillis()).setUpdateTime(System.currentTimeMillis());
        if (payTrade.hasAccountId()){
            builder.setAccountId(payTrade.getAccountId());
        }
        if (payTrade.hasSignCorpId()){
            builder.setSignCorpId(payTrade.getSignCorpId());
        }
        CreatePayTradeTransRequest ctr = CreatePayTradeTransRequest.newBuilder()
                .setPayTradeTrans(builder.build()).build();
        tradeService.createPayTradeTrans(ctr);
    }





    public void createOrUpdateOrder(boolean create, PayFrontendParam frontendParam, String orderCode, Long partnerId, Long accountId, Long signCorpId, Long sourcePayType, Long destPayType, Long payKey){
        PayTrade.Builder payTradeBuilder = PayTrade.newBuilder();
        payTradeBuilder.setOrderCode(orderCode);
        payTradeBuilder.setCreateTime(System.currentTimeMillis());
        payTradeBuilder.setFee(frontendParam.getFee());
        payTradeBuilder.setSubFee(frontendParam.getFee());
        payTradeBuilder.setSubRealFee(frontendParam.getFee());
        payTradeBuilder.setRealFee(frontendParam.getFee());
        payTradeBuilder.setFeeUnit(frontendParam.getFee_unit().intValue());
        payTradeBuilder.setNotifyUrl(frontendParam.getNotify_url());
        payTradeBuilder.setPartnerOrderNo(frontendParam.getPartner_order_no());
        payTradeBuilder.setPartnerId(partnerId);
        payTradeBuilder.setRealFee(frontendParam.getFee());
        payTradeBuilder.setSignCorpId(signCorpId);
        payTradeBuilder.setAccountId(accountId);
        payTradeBuilder.setSubject(frontendParam.getSubject());
        payTradeBuilder.setStatus(2);
        payTradeBuilder.setUpdateTime(System.currentTimeMillis());
        payTradeBuilder.setUserId(frontendParam.getUid().toString());
        payTradeBuilder.setSourcePayType(sourcePayType);
        payTradeBuilder.setDestPayType(destPayType);
        payTradeBuilder.setCurrentKey(payKey);
        if (StringUtils.isNotBlank(frontendParam.getExtend_params())){
            payTradeBuilder.setExtendParams(frontendParam.getExtend_params());
        }
        if (frontendParam.getExtra_common_param() != null){
            payTradeBuilder.setExtraCommonParam(frontendParam.getExtra_common_param());
        }
        if (frontendParam.getCip() != null){
            payTradeBuilder.setIp(frontendParam.getCip());
        }
        if (frontendParam.getMobile() != null){
            payTradeBuilder.setMobile(frontendParam.getMobile());
        }
        if (frontendParam.getReturn_url() != null){
            payTradeBuilder.setReturnUrl(frontendParam.getReturn_url());
        }
        if (frontendParam.getExpire_time() != null){
            payTradeBuilder.setExpireTime(frontendParam.getExpire_time());
        }
        if (frontendParam.getDescription() != null){
            payTradeBuilder.setDescription(frontendParam.getDescription());
        }
        if (StringUtils.isNotBlank(frontendParam.getClient_code())){

            payTradeBuilder.setClientCode(frontendParam.getClient_code());
        }
        if (StringUtils.isNotBlank(PayFrontendParam.getGatewayType(frontendParam.getService()))){
            payTradeBuilder.setGateWay(Integer.parseInt(PayFrontendParam.getGatewayType(frontendParam.getService())));
        }
        if (create){
            CreatePayTradeRequest.Builder request = CreatePayTradeRequest.newBuilder();
            request.setPayTrade(payTradeBuilder.build());
            tradeService.createPayTrade(request.build());
        }else {
            UpdatePayTradeByOrderCodeRequest.Builder updateRequest = UpdatePayTradeByOrderCodeRequest.newBuilder();
            updateRequest.setPayTrade(payTradeBuilder.build());
            tradeService.updatePayTradeByOrderCode(updateRequest.build());
        }
        orderLogService.createOrderLog(payTradeBuilder.build());
    }





    protected boolean checkSign(PayFrontendParam param){
        Future<PartnerPayKey> partnerPayKeyFuture = partnerService.getPartnerPayKey(param.getPartner());
        PartnerPayKey partnerPayKey = null;
        try {
            partnerPayKey = partnerPayKeyFuture.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String signType = partnerPayKey.getSignType();
        String key = partnerPayKey.getPayPublickey();
        if("MD5".equals(signType)) {
            key = partnerPayKey.getValue();
        }else if("RSA".equals(signType)||"DSA".equals(signType)){
            key = partnerPayKey.getPayPublickey();
        }
        Map<String, String> params = PayUtils.genMapByRequestParas(getRequest().getParameterMap());
        if(PayUtils.doCheckMessageRequest(params,key)||PayUtils.doCheckMessageRequestNoIp(params,key)){
            return true;
        }
        return false;
    }

    private boolean validateRequest(PayResultBuilder builder, PayFrontendParam param){
        return true;
    }


    private void renderJson(PayResult payResult){
        try {
            getResponse().getWriter().write(payResult.toJson());
            getResponse().flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void redirectUrl(PayResult payResult){
        try {
            getResponse().sendRedirect("url");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
