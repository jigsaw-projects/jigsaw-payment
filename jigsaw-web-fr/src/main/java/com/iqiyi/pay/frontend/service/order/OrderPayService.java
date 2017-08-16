package com.iqiyi.pay.frontend.service.order;

import com.iqiyi.pay.frontend.service.accesser.PartnerService;
import com.iqiyi.pay.frontend.service.route.CardRouteService;
import com.iqiyi.pay.sdk.BankTrade;
import com.iqiyi.pay.sdk.ChannelAccount;
import com.iqiyi.pay.sdk.PayTrade;
import com.iqiyi.pay.sdk.PaymentType;
import com.iqiyi.pay.sdk.service.TradeService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * Created by leishengbao on 8/23/16.
 */
@Component
public class OrderPayService {

    public static final Logger LOGGER = LoggerFactory.getLogger(OrderPayService.class);

    @Autowired
    com.iqiyi.pay.client.TradeService tradeService;
    @Autowired
    com.iqiyi.pay.client.RouteService routeService;
    @Autowired
    PayTradeService payTradeService;
    @Autowired
    private OrderLogService orderLogService;
    @Autowired
    private CardRouteService cardRouteService;
    @Autowired
    private PartnerService partnerService;

    public void orderPayedByServiceCode(Map<String, Object> data, String serviceCode){
        String orderCode = Objects.toString(data.get("order_code"));
        String channelOrderCode = Objects.toString(data.get("channel_trade_code"));
        String channelPayTime = Objects.toString(data.get("channel_trade_time"));
        Long fee = NumberUtils.createLong(Objects.toString(data.get("fee")));
        finishPay(orderCode, channelOrderCode, fee, channelPayTime, serviceCode);
    }


    public void orderPayed(Map<String, Object> data){
        orderPayedByServiceCode(data,  null);
    }


    /**
     * 1. order payed update order status = 1
     * 2. send the async notify
     */
    protected boolean finishPay(String orderCode, String channelOrderCode,
                                Long fee, String channelPayTime, String serviceCode){
        LOGGER.info("update order payed [orderCode:{}][channelOrderCode:{}][fee:{}][channelPayTime:{}]", orderCode, channelOrderCode, fee, channelPayTime);
        PayTrade payTrade = payTradeService.queryOrderByCode(orderCode);
        if (payTrade == null){
            LOGGER.debug("[order not exit][orderCode:{}]", orderCode);
            return false;
        }
        if (payTrade.getStatus() == 1){//order has payed
            return true;
        }
        if (payTrade.getRealFee() != fee){
            LOGGER.info("[order fee unEqual failed][orderCode:{}][fee:{}]", orderCode, fee);
            return false;
        }
        if (serviceCode != null){
            ChannelAccount channelAccount = cardRouteService.queryChannelAccount(serviceCode);
            if (channelAccount.getPaymentServiceId() != payTrade.getDestPayType()){
                LOGGER.info("[destPayType not equlas][destPayType:{}][serviceCode:{}]", payTrade.getDestPayType(), serviceCode);
                PaymentType paymentType = partnerService.getPaymentType("CARDPAY");
                PayTrade.Builder payTradeBuilder = PayTrade.newBuilder();
                payTradeBuilder.setOrderCode(orderCode);
                payTradeBuilder.setSourcePayType(paymentType.getId());
                payTradeBuilder.setDestPayType(channelAccount.getPaymentServiceId());
                payTradeBuilder.setAccountId(channelAccount.getId());
                payTradeBuilder.setSignCorpId(channelAccount.getSignCorpId());
                TradeService.UpdatePayTradeByOrderCodeRequest request = TradeService.UpdatePayTradeByOrderCodeRequest.newBuilder().setPayTrade(payTradeBuilder.build()).build();
                tradeService.updatePayTradeByOrderCode(request);
            }
        }

        TradeService.FinishPayRequest.Builder builder = TradeService.FinishPayRequest.newBuilder();
        builder.setThirdPayTime(System.currentTimeMillis());
        builder.setOrderCode(orderCode);
        builder.setRealFee(payTrade.getRealFee());
        if (StringUtils.isNotBlank(channelOrderCode)){
            builder.setThirdOrderCode(channelOrderCode);
        }
        TradeService.FinishPayResponse response = tradeService.finishPay(builder.build());
        boolean effected = response.getPayTrade().getStatus() == 1;
        orderLogService.updateOrderPayed(orderCode, channelOrderCode, fee, channelPayTime);
        return effected;
    }

    @Async
    public void saveBankBillNo(String billNo, String orderCode){
        LOGGER.info("[billNo:{}][orderCode:{}]", billNo, orderCode);
        if (billNo == null){
            return;
        }
        BankTrade bankTrade = BankTrade.newBuilder()
                .setBillNo(billNo)
                .setCreateTime(System.currentTimeMillis())
                .setOrderCode(orderCode)
                .setOrderDate(DateFormatUtils.format(new Date(), "yyyyMMdd"))
                .build();
        TradeService.CreateBankTradeRequest request = TradeService.CreateBankTradeRequest.newBuilder().setBankTrade(bankTrade).build();
        tradeService.createBankTrade(request);
    }
}
