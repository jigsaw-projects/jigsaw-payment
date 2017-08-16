package com.iqiyi.pay.frontend.service;

import com.google.common.collect.Maps;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.frontend.utils.MapWapper;

import java.util.Map;

/**
 * Created by leishengbao on 8/12/16.
 */
public interface BankPayment {


    /**
     * configure id params
     * @param params
     */
    void configureCheckId(Map<String, String> params);


    PayResult<Map<String, Object>> checkIdentity(String paymentCode);
}
