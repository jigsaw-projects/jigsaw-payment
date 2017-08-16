package com.iqiyi.pay.frontend.service.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.iqiyi.pay.sdk.PayTrade;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by leishengbao on 12/26/16.
 */
@Component
public class OrderLogService {

    private Logger logger = LoggerFactory.getLogger(OrderLogService.class);


    public void createOrderLog(PayTrade order){
        Map<String, Object> resultMap = Maps.newHashMap();
        Map<String, String> orderMap = Maps.newHashMap();
        try {
            orderMap = BeanUtils.describe(order);
        } catch (Exception e) {
            logger.error("createOrderLog", e);
        }
        orderMap.remove("extendParams");
        resultMap.put("entity_type", "pay_order");
        resultMap.put("entity_id", order.getOrderCode());
        resultMap.put("data", orderMap);
        String json = JSON.toJSONString(resultMap);
        logger.info("entity_log:{}", json);
    }


    public void updateOrderPayed(String orderCode, String channelOrderCode,
                                 Long fee, String channelPayTime){
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        result.put("entity_type", "pay_order");
        result.put("entity_id", orderCode);
        data.put("channelOrderCode", channelOrderCode);
        data.put("channelFee", fee);
        data.put("channelPayTime", channelPayTime);
        result.put("data", data);;
        logger.info("entity_log:{}", result.toJSONString());
    }
}
