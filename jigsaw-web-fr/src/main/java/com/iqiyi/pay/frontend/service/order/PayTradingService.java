package com.iqiyi.pay.frontend.service.order;

import com.iqiyi.pay.client.TradingService;
import com.iqiyi.pay.sdk.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by leishengbao on 4/17/17.
 */
@Component
public class PayTradingService {

    @Autowired
    private TradingService tradingService;

    public Trade getTradeByCode(String orderCode){
        com.iqiyi.pay.sdk.service.TradingService.GetTradeRequest request = com.iqiyi.pay.sdk.service.TradingService.GetTradeRequest.newBuilder()
                .setCode(orderCode).build();
        com.iqiyi.pay.sdk.service.TradingService.GetTradeResponse response = tradingService.getTrade(request);
        if (response.hasTrade()){
            return response.getTrade();
        }
        return null;
    }

    public Trade getTradeByThirdTradeCode(Long userId,String transactionId) {
        com.iqiyi.pay.sdk.service.TradingService.QueryTradeListRequest request = com.iqiyi.pay.sdk.service.TradingService.QueryTradeListRequest.newBuilder().setThirdTradeCode(transactionId).setOffset(0).setLimit(50).build();
        com.iqiyi.pay.sdk.service.TradingService.QueryTradeListResponse response = tradingService.queryTradeList(request);
        List<Trade> trades = response.getTradeList();
        if(trades!=null){
            Trade trade = trades.stream().filter(td ->td.getSubId()==userId).findAny().get();
            return trade;
        }else{
            return null;
        }
    }
}