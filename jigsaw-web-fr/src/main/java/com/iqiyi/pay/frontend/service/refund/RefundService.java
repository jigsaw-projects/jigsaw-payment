package com.iqiyi.pay.frontend.service.refund;

import com.iqiyi.pay.client.TradeService;
import com.iqiyi.pay.sdk.PayRefundTrade;
import com.iqiyi.pay.sdk.service.TradeService.CreateRefundTradeRequest;
import com.iqiyi.pay.sdk.service.TradeService.CreateRefundTradeResponse;
import com.iqiyi.pay.sdk.service.TradeService.NotifyRefundTradeRequest;
import com.iqiyi.pay.sdk.service.TradeService.QueryRefundTradeByOrderCodeRequest;
import com.iqiyi.pay.sdk.service.TradeService.QueryRefundTradeByOrderCodeResponse;
import com.iqiyi.pay.sdk.service.TradeService.QueryRefundTradeByPartnerRefundNoAndPartnerIdRequest;
import com.iqiyi.pay.sdk.service.TradeService.QueryRefundTradeByPartnerRefundNoAndPartnerIdResponse;
import com.iqiyi.pay.sdk.service.TradeService.UpdateRefundTradeByRefundCodeRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by leishengbao on 9/8/16.
 */
@Component
public class RefundService {


    @Autowired
    private TradeService tradeService;


    public PayRefundTrade queryPayRefundTradeByPartnerRefundNo(String partnerRefundNo, long partnerId){
        QueryRefundTradeByPartnerRefundNoAndPartnerIdRequest request = QueryRefundTradeByPartnerRefundNoAndPartnerIdRequest.newBuilder()
                .setPartnerId(partnerId).setPartnerRefundNo(partnerRefundNo).build();
        QueryRefundTradeByPartnerRefundNoAndPartnerIdResponse response = tradeService.queryRefundTradeByParterRefundNoAndPartnerId(request);
        if (response.hasPayRefundTrade()){
            return response.getPayRefundTrade();
        }
        return null;
    }


    public Integer getFeeByOrderCodeAndStatus(String orderCode){
        QueryRefundTradeByOrderCodeRequest request = QueryRefundTradeByOrderCodeRequest.newBuilder().setOrderCode(orderCode).build();
        QueryRefundTradeByOrderCodeResponse response = tradeService.queryRefundTradeByOrderCode(request);
        int sum = 0;
        for (PayRefundTrade payRefundTrade : response.getPayRefundTradeList()){
            if(payRefundTrade.getStatus() != 1){
                continue;
            }
            sum += payRefundTrade.getFee();
        }
        return sum;
    }


    public boolean createPayRefundTrade(PayRefundTrade refundTrade){
        CreateRefundTradeRequest request = CreateRefundTradeRequest.newBuilder().setPayRefundTrade(refundTrade).build();
        CreateRefundTradeResponse response = tradeService.createRefundTrade(request);
        return response.hasEffect();
    }


    public void dealRefundResult(String refundCode, String thirdRefundCode, long fee){
        NotifyRefundTradeRequest.Builder request = NotifyRefundTradeRequest.newBuilder()
                .setFee(fee).setRefundCode(refundCode).setThridRefundCode(thirdRefundCode);
        if (StringUtils.isNotBlank(thirdRefundCode)){
            request.setThridRefundCode(thirdRefundCode);
        }
        tradeService.notifyRefundTrade(request.build());
    }


    public void dealRefundFail(String refundCode, String errorMsg){
        PayRefundTrade.Builder builder = PayRefundTrade.newBuilder();
        builder.setRefundCode(refundCode)
                .setStatus(3)
                .setErrorMsg(errorMsg);
        UpdateRefundTradeByRefundCodeRequest request = UpdateRefundTradeByRefundCodeRequest.newBuilder().setRefundTrade(builder.build()).build();
        tradeService.updateRefundTradeByRefundCode(request);
    }
}
