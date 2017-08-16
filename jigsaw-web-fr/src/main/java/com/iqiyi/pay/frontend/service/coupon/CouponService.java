package com.iqiyi.pay.frontend.service.coupon;

import com.google.common.collect.Maps;
import com.iqiyi.kiwi.utils.HttpClientConnection;
import com.iqiyi.pay.common.security.PayUtils;
import com.iqiyi.pay.common.utils.JsonBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by leishengbao on 10/21/16.
 */
@Component
public class CouponService {

    public static final Logger LOGGER = LoggerFactory.getLogger(CouponService.class);

    @Value("${coupon.unfreeze.url}")
    private String unFreezeCouponUrl;
    @Value("${unFreezeCoupon.sign.key}")
    private String key;

    public boolean unFrozenCoupon(String partnerOrderNo){
        Map<String, String> params = Maps.newTreeMap();
        params.put("order_code", partnerOrderNo);
        params.put("version", "1.0.0");
        String sign = PayUtils.signMessageRequest(params, key);
        params.put("sign", sign);
        String body = null;
        try{
            HttpClientConnection hcc = new HttpClientConnection(unFreezeCouponUrl, HttpClientConnection.POST_METHOD);
            hcc.setReqParams(params);
            hcc.connect();
            body = hcc.getBody();
        }catch (Exception e){
            LOGGER.error("[partnerOrderNo:{}]", partnerOrderNo, e);
        }
        LOGGER.debug("params : {}, result : {}", params, body);
        if (body != null){
            Map<String, String> result = JsonBinder.buildNonDefaultBinder().fromJson(body, Map.class);
            return "10000".equals(result.get("code"));
        }
        return false;
    }
}
