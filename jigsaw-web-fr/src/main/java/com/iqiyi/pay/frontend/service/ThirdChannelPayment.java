package com.iqiyi.pay.frontend.service;

import com.iqiyi.pay.common.utils.result.PayResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by leishengbao on 9/9/16.
 */
@Component
@Scope("prototype")
public class ThirdChannelPayment extends AbstractPayment{


    @Value("${third.channel.url}")
    private String THIRD_CHANNEL_URL;

    @Override
    public void configure(Map<String, String> params) {
        configs.put("order_code", params.get("order_code"));
        configs.put("fee", params.get("fee"));
        configs.put("currency", "USD");
        configs.put("order_description", params.get("subject"));
        configs.put("mobile", params.get("mobile"));
        configs.put("extend_params", params.get("extend_params"));
        configs.put("expire_time", params.get("expire_time"));
        configs.put("trans_time", params.get("create_time"));
    }

    @Override
    public PayResult<Map<String, Object>> payRequest(String paymentCode) {
        PayResult<Map<String, Object>> result = this.getResultFromChannel(THIRD_CHANNEL_URL + paymentCode.toLowerCase() + "/pay", configs);
        result.getData().put("order_code", configs.get("order_code"));
        if (result.getData() != null){
            Map<String, Object> channelData = (Map<String, Object>) result.getData().get("channel_data");
            if (channelData != null){
                result.getData().putAll(channelData);
            }
        }
        return result;
    }


    @Override
    public void configureRefund(Map<String, String> params) {
        super.configureRefund(params);
    }

    @Override
    public PayResult<Map<String, Object>> refundRequest(String paymentCode) {
        return super.refundRequest(paymentCode);
    }
}
