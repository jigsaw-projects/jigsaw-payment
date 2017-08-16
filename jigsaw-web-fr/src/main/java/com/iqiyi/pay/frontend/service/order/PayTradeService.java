package com.iqiyi.pay.frontend.service.order;

import com.iqiyi.pay.frontend.service.route.CardRouteService;
import com.iqiyi.pay.sdk.AppStoreOrder;
import com.iqiyi.pay.sdk.BankTrade;
import com.iqiyi.pay.sdk.ChannelAccount;
import com.iqiyi.pay.sdk.KeyValuePair;
import com.iqiyi.pay.sdk.PayTrade;
import com.iqiyi.pay.sdk.PayTradeExtend;
import com.iqiyi.pay.sdk.service.TradeService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Created by leishengbao on 8/25/16.
 */
@Component
public class PayTradeService {

    public static final Logger LOGGER = LoggerFactory.getLogger(PayTradeService.class);

    public static final String EXTRA_KEY_CARDTYPE = "cardType";

    @Autowired
    com.iqiyi.pay.client.TradeService tradeService;
    @Autowired
    com.iqiyi.pay.client.TradingService tradingService;
    @Autowired
    CardRouteService cardRouteService;

    public PayTrade queryOrderByCode(String orderCode){
        TradeService.QueryPayTradeByOrderCodeRequest.Builder builder = TradeService.QueryPayTradeByOrderCodeRequest.newBuilder();
        builder.setOrderCode(orderCode);
        tradeService.queryPayTradeByOrderCode(builder.build());
        TradeService.QueryPayTradeByOrderCodeResponse response = tradeService.queryPayTradeByOrderCode(builder.build());
        if (response.hasPayTrade()){
            return response.getPayTrade();
        }
        return null;
    }

    public boolean updateTrade(PayTrade trade){
        TradeService.UpdatePayTradeByOrderCodeRequest.Builder request = TradeService.UpdatePayTradeByOrderCodeRequest.newBuilder();
        request.setPayTrade(trade);
        TradeService.UpdatePayTradeByOrderCodeResponse response = tradeService.updatePayTradeByOrderCode(request.build());
        if(response == null){
            LOGGER.error("call basic service updateTrade return null，request data：{}", trade);
            return false;
        }else if(1!=response.getEffected()){
            LOGGER.error("call basic service updateTrade faild，request data：{}", trade);
            return false;
        }
        LOGGER.info("finished the process of base service invoke, result : {}",response);
        return true;
    }


    public void updatePayTrade(String orderCode, ChannelAccount channelAccount){
        if (channelAccount == null){
            return;
        }
        PayTrade.Builder payTradeBuilder = PayTrade.newBuilder();
        payTradeBuilder.setOrderCode(orderCode);
        payTradeBuilder.setDestPayType(channelAccount.getPaymentServiceId());
        payTradeBuilder.setAccountId(channelAccount.getId());
        payTradeBuilder.setSignCorpId(channelAccount.getSignCorpId());
        TradeService.UpdatePayTradeByOrderCodeRequest request = TradeService.UpdatePayTradeByOrderCodeRequest.newBuilder().setPayTrade(payTradeBuilder.build()).build();
        TradeService.UpdatePayTradeByOrderCodeResponse response = tradeService.updatePayTradeByOrderCode(request);
    }


    public BankTrade queryBankTrade(String orderCode){
        TradeService.QueryBankTradeRequest request = TradeService.QueryBankTradeRequest.newBuilder()
                .setOrderCode(orderCode).build();
        TradeService.QueryBankTradeResponse response = tradeService.queryBankTrade(request);
        if (response.hasBankTrade()){
            return response.getBankTrade();
        }
        return null;
    }

    public PayTrade queryPayTadeByPartnerOrderNo(String partnerOrderNo, Long partnerId){
        TradeService.QueryPayTradeByPartnerRequest request = TradeService.QueryPayTradeByPartnerRequest.newBuilder().
                setPartnerId(partnerId.intValue()).setPartnerOrderNo(partnerOrderNo).build();
        TradeService.QueryPayTradeByPartnerResponse response = tradeService.queryPayTradeByPartnerId(request);
        if (response.hasPayTrade()){
            return response.getPayTrade();
        }
        return null;
    }

    @Async
    public void updatePayTradeByChannelCode(long cardId, String channelCode, String orderCode, long userId){
        LOGGER.debug("[cardId:{}][channelCode:{}][orderCode:{}]", cardId, channelCode, orderCode);
        ChannelAccount channelAccount = cardRouteService.queryChannelAccount(channelCode);
        this.updatePayTradeExtend(cardId, orderCode, String.valueOf(userId));
        //更新支付服务，商户号等信息
        this.updatePayTrade(orderCode, channelAccount);
    }

    /**
     * 更新订单对应支付的CARDID
     * 1 首次支付发送短信验证码成功后，保存或更新
     * 2 非首次支持，选中银行卡进行支付时，保存或更新
     * @param cardId
     * @param orderCode
     */
    @Async
    public void updatePayTradeExtend(long cardId, String orderCode, String userId){
        LOGGER.info("[updatePayTradeExtend][userId:{}][orderCode:{}][cardId:{}]", userId, orderCode, cardId);
        if (StringUtils.isBlank(orderCode)){
            return;
        }
        PayTradeExtend payTradeExtend = PayTradeExtend.newBuilder()
                .setOrderCode(orderCode)
                .setPayTime(System.currentTimeMillis())
                .setCardId(String.valueOf(cardId))
                .setUid(userId)
                .setVersion(0).build();
        TradeService.UpdatePayTradeExtendByOrderCodeRequest request = TradeService.UpdatePayTradeExtendByOrderCodeRequest.newBuilder()
                .setPayTradeExtend(payTradeExtend).build();
        tradeService.updatePayTradeExtendByOrderCode(request);
    }


    /**
     * 关闭订单
     * @param orderCode
     */
    public void closePayTradeByCode(String orderCode){
        PayTrade.Builder payTradeBuilder = PayTrade.newBuilder();
        payTradeBuilder.setOrderCode(orderCode);
        payTradeBuilder.setStatus(7);
        TradeService.UpdatePayTradeByOrderCodeRequest request = TradeService.UpdatePayTradeByOrderCodeRequest.newBuilder()
                .setPayTrade(payTradeBuilder).build();
        tradeService.updatePayTradeByOrderCode(request);
    }


    public PayTradeExtend queryOrderExtendByOrderCode(String orderCode){
        TradeService.QueryPayTradeExtendByOrderCodeRequest request = TradeService.QueryPayTradeExtendByOrderCodeRequest.newBuilder()
                .setOrderCode(orderCode).build();
        TradeService.QueryPayTradeExtendByOrderCodeResponse response = tradeService.queryPayTradeExtendByOrderCode(request);
        if (response.hasPayTradeExtend()){
            return response.getPayTradeExtend();
        }
        return null;
    }

    public PayTrade preparePay(String orderCode, ChannelAccount channelAccount, String subAccountId,
                               String platform, int cardType){
        TradeService.PreparePayRequest preparePayRequest = TradeService.PreparePayRequest.newBuilder()
                .setDestPayType(new Long(channelAccount.getPaymentServiceId()).intValue())
                .setAccountId(new Long(channelAccount.getId()).intValue())
                .setOrderCode(orderCode)
                .setSubAccountId(subAccountId)
                .setSignCorpId(new Long(channelAccount.getSignCorpId()).intValue())
                .setClientCode(platform)
                .addPairs(KeyValuePair.newBuilder().setKey(EXTRA_KEY_CARDTYPE).setValue(String.valueOf(cardType)))
                .build();
        TradeService.PreparePayResponse response = tradeService.preparePay(preparePayRequest);
        return response.getPayTrade();
    }




    public PayTrade prepayPayByChannelCode(long cardId, String channelCode, String orderCode, long userId,
                                           String subAccountId, String platform, int cardType){
        LOGGER.debug("[cardId:{}][channelCode:{}][orderCode:{}]", cardId, channelCode, orderCode);
        ChannelAccount channelAccount = cardRouteService.queryChannelAccount(channelCode);
        this.updatePayTradeExtend(cardId, orderCode, String.valueOf(userId));
        //更新支付服务，商户号等信息
        return this.preparePay(orderCode, channelAccount, subAccountId, platform, cardType);
    }

    public void saveAppstoreOrder(String transactionId, String orderCode, String receipt, String appType, String uid, String productId, String originalTransactionId) {
        Long originTransactionId = null;
        Long userId=null;
        if (StringUtils.isNotBlank(originalTransactionId)) {
            originTransactionId = Long.parseLong(originalTransactionId);
        }
        if(StringUtils.isNotBlank(uid)){
            userId = Long.parseLong(uid);
        }
        AppStoreOrder appStoreOrder = AppStoreOrder.newBuilder().setOrderCode(orderCode).setTransactionId(Long.parseLong(transactionId)).setReceiptData(receipt).setAppType(appType).setUserId(userId).setProductId(productId).setOriginalTransactionId(originTransactionId).build();
        TradeService.SaveAppStoreOrderRequest request = TradeService.SaveAppStoreOrderRequest.newBuilder().setAppStoreOrder(appStoreOrder).build();
        try {
            tradeService.saveAppStoreOrder(request);
        } catch (Exception e){
            LOGGER.info("ERROR_SAVE_APPLE_ORDER_TO_MONGODB [orderCode:{}] [ERR_MSG:{}] [ERR_CAUSE:{}]", orderCode, e.getMessage(), e.getCause());
        }
    }

    public AppStoreOrder getAppStoreOrder(String orderCode){
        TradeService.GetAppStoreOrderRequest request=TradeService.GetAppStoreOrderRequest.newBuilder().setOrderCode(orderCode).build();
        com.iqiyi.pay.sdk.service.TradeService.GetAppStoreOrderResponse  response =null;
        try {
            response=tradeService.getAppStoreOrder(request);
        } catch (Exception e) {
            LOGGER.info("ERROR_GET_APPLE_ORDER_FROM_MONGODB [orderCode:{}] [ERR_MSG:{}] [ERR_CAUSE:{}]", orderCode, e.getMessage(), e.getCause());
        }
        if(response!=null && response.hasAppStoreOrder()){
            return response.getAppStoreOrder();
        }
        return  null;
    }
}
