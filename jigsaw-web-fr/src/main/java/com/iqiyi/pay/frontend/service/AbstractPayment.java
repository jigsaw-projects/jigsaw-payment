package com.iqiyi.pay.frontend.service;

import com.iqiyi.kiwi.utils.HttpClientConnection;
import com.iqiyi.pay.common.utils.JsonBinder;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.utils.MapWapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by leishengbao on 7/19/16.
 */
public abstract class AbstractPayment implements Payment{


    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractPayment.class);


    protected Map<String, String> configs = new MapWapper<>();


    protected Map<String, String> bindConfigs = new MapWapper<>();


    protected Map<String, String> unbindConfigs = new MapWapper<>();


    protected Map<String, String> refundConfigs = new MapWapper<>();

    @Override
    public void configureBind(Map<String, String> params) {

    }

    @Override
    public void configureUnBind(Map<String, String> params) {

    }

    @Override
    public void configureRefund(Map<String, String> params) {

    }

    @Override
    public PayResult<Map<String, Object>> refundRequest(String paymentCode) {
        return null;
    }

    @Override
    public PayResult<Map<String, Object>> bindRequest(String paymentCode) {
        return null;
    }

    @Override
    public PayResult<Map<String, Object>> unbindRequest(String paymentCode) {
        return null;
    }

    @Override
    public PayResult<Map<String, Object>> queryRequest(String paymentCode) {
        return null;
    }


    protected PayResult<Map<String, Object>> getResultFromChannel(String url , Map<String, String> params){
        HttpClientConnection hcc = new HttpClientConnection(url, HttpClientConnection.POST_METHOD);
        hcc.setSoTimeout(15000);
        PayResultBuilder builder = PayResultBuilder.create();
        try {
            hcc.setReqBody(JsonBinder.buildNonDefaultBinder().toJson(params));
            hcc.setReqContentType("application/json");
            hcc.connect();
            String body = hcc.getBody();
            LOGGER.debug("[userId:{}][orderCode:{}][body:{}]", params.get("user_id"), params.get("order_code"), body);
            Map<String, Object> result = JsonBinder.buildNonDefaultBinder().fromJson(body, Map.class);
            builder.setCode(result.remove("code").toString()).setMsg(result.remove("msg").toString()).setData(result);
            return builder.build();
        }catch (Exception e){
            builder.setResultCode(ResultCode.ERROR_OF_CHANNEL_FAILED);
            LOGGER.error("[url:{}]", url, e);
        }
        return builder.build();
    }
}
