package com.iqiyi.pay.frontend.service;

import com.iqiyi.pay.common.utils.result.PayResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by liuyanlong on 3/17/17.
 */
@Component
@Scope("prototype")
public class ChannelQueryPayment extends AbstractPayment {

    @Value("${third.query.url}")
    private String THIRD_CHANNEL_URL;

    @Override
    public void configure(Map<String, String> params) {
        configs = params;
    }

    @Override
    public PayResult<Map<String, Object>> payRequest(String paymentCode) {
        return null;
    }

    @Override
    public PayResult<Map<String, Object>> queryRequest(String paymentCode) {
        return this.getResultFromChannel(THIRD_CHANNEL_URL + paymentCode.toLowerCase() + "-common/queryOrder", configs);
    }
}
