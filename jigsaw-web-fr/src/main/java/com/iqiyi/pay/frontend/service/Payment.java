package com.iqiyi.pay.frontend.service;

import com.iqiyi.pay.common.utils.result.PayResult;

import java.util.Map;

/**
 * Created by leishengbao on 7/19/16.
 */
public interface Payment {

    void configure(Map<String, String> params);

    /**
     * 配置绑定参数
     * @param params
     */
    void configureBind(Map<String, String> params);

    /**
     * configure unbind params
     * @param params
     */
    void configureUnBind(Map<String, String> params);


    void configureRefund(Map<String, String> params);

    PayResult<Map<String, Object>> payRequest(String paymentCode);


    PayResult<Map<String, Object>> bindRequest(String paymentCode);


    PayResult<Map<String, Object>> unbindRequest(String paymentCode);


    PayResult<Map<String, Object>> refundRequest(String paymentCode);

    PayResult<Map<String, Object>> queryRequest(String paymentCode);
}
