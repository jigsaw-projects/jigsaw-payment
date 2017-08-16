package com.iqiyi.pay.frontend.service.accesser;

import com.iqiyi.pay.frontend.aspect.Trace;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.frontend.utils.Constants;
import com.iqiyi.pay.sdk.*;
import com.iqiyi.pay.sdk.service.RouteService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * Created by leishengbao on 9/7/16.
 */
@Component
public class PartnerService {

    public static final Logger LOGGER = LoggerFactory.getLogger(PartnerService.class);

    @Autowired
    com.iqiyi.pay.client.RouteService routeService;
    @Autowired
    PayTradeService payTradeService;

    @Value("${wallet.partner.id}")
    private Integer walletPartnerId;


    public PayAccesser getAccesser(String partner){
        RouteService.QueryAccesserRequest.Builder requestBuilder = RouteService.QueryAccesserRequest.newBuilder();
        requestBuilder.setPartner(partner);
        RouteService.QueryAccesserResponse queryAccesserResponse = routeService.queryAccesser(requestBuilder.build());
        if (queryAccesserResponse.hasAccesser()){
            return queryAccesserResponse.getAccesser();
        }
        return null;
    }

    @Async
    public Future<PartnerPayKey> getPartnerPayKey(String partner){
        RouteService.QueryPartnerPayKeyRequest.Builder builder = RouteService.QueryPartnerPayKeyRequest.newBuilder();
        builder.setPartner(partner);
        RouteService.QueryPartnerPayKeyResponse response = routeService.queryPartnerPayKey(builder.build());
        if (response.hasPartnerPayKey()){
            return AsyncResult.forValue(response.getPartnerPayKey());
        }
        return AsyncResult.forValue(null);
    }


    public PartnerPayKey syncGetPartnerPayKey(String partner){
        RouteService.QueryPartnerPayKeyRequest.Builder builder = RouteService.QueryPartnerPayKeyRequest.newBuilder();
        builder.setPartner(partner);
        RouteService.QueryPartnerPayKeyResponse response = routeService.queryPartnerPayKey(builder.build());
        if (response.hasPartnerPayKey()){
            return response.getPartnerPayKey();
        }
        return null;
    }



    public PaymentType getPaymentType(String payType){
        RouteService.QueryPaymentTypeRequest.Builder builder = RouteService.QueryPaymentTypeRequest.newBuilder();
        builder.setPayCode(payType);
        RouteService.QueryPaymentTypeResponse response = routeService.queryPaymentType(builder.build());
        if (response.hasPaymentType()){
            return response.getPaymentType();
        }
        return null;
    }


    public PayServiceAccountRoute getPayRoute(Long payType, String partner){
        RouteService.QueryPayRouteRequest.Builder builder = RouteService.QueryPayRouteRequest.newBuilder();
        builder.setPayType(payType.intValue());
        builder.setPartner(partner);
        RouteService.QueryPayRouteResponse response = routeService.queryPayRoute(builder.build());
        if (response.hasPayServiceAccountRoute()){
            return response.getPayServiceAccountRoute();
        }
        return null;
    }


    public boolean hasAccesserPayment(String partner, Long payType){
        return getAccesserPayment(partner, payType) != null;
    }


    public PaymentType getAccesserPayment(String partner, Long payType){
        RouteService.QueryAccesserPaymentRequest.Builder builder = RouteService.QueryAccesserPaymentRequest.newBuilder();
        builder.setPartner(partner).setPayType(payType.intValue());
        RouteService.QueryAccesserPaymentResponse response =  routeService.queryAccesserPayment(builder.build());
        if (response.hasAccesserPayment()){
            return response.getAccesserPayment();
        }
        return null;
    }


    public boolean notSupportsCredit(String orderCode){
        if (StringUtils.isBlank(orderCode)){
            return false;
        }
        //零钱充值，不支持信用卡，排除钱包绑卡
        PayTrade payTrade = payTradeService.queryOrderByCode(orderCode);
        if (payTrade == null){
            return false;
        }
        LOGGER.debug("[partnerId:{}][orderCode:{}][user_id:{}]", payTrade.getPartnerId(), payTrade.getOrderCode(), payTrade.getUserId());
        return payTrade.getPartnerId() == walletPartnerId && !Constants.WSC_SUBJECT.equals(payTrade.getSubject());
    }

    public boolean notSupportsCredit(PayTrade payTrade){
        if (payTrade == null){
            return false;
        }
        LOGGER.debug("[partnerId:{}][orderCode:{}][user_id:{}]", payTrade.getPartnerId(), payTrade.getOrderCode(), payTrade.getUserId());
        //零钱充值，不支持信用卡，排除钱包绑卡
        return payTrade.getPartnerId() == walletPartnerId && !Constants.WSC_SUBJECT.equals(payTrade.getSubject());
    }




    public PaymentType queryPaymentTypeById(long paymentTypeId){
        RouteService.QueryPaymentTypeByIdRequest.Builder requestBuilder = RouteService.QueryPaymentTypeByIdRequest.newBuilder();
        requestBuilder.setPaymentTypeId(paymentTypeId);
        RouteService.QueryPaymentTypeByIdResponse response = routeService.queryPaymentTypeById(requestBuilder.build());
        return response.getPaymentType();
    }


    public PaymentService queryPaymentServiceById(long paymentServiceId){
        RouteService.QueryPaymentServiceByIdRequest.Builder requestBuilder = RouteService.QueryPaymentServiceByIdRequest.newBuilder();
        requestBuilder.setPaymentServiceId(paymentServiceId);
        RouteService.QueryPaymentServiceByIdResponse response = routeService.queryPaymentServiceById(requestBuilder.build());
        return response.getPaymentService();
    }
}
