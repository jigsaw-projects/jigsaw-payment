package com.iqiyi.pay.frontend.service.order;

import com.google.common.collect.Maps;
import com.iqiyi.kiwi.utils.DateHelper;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.service.accesser.PartnerService;
import com.iqiyi.pay.sdk.PaymentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;

/**
 * Created by leishengbao on 10/21/16.
 */
@Component
public class OrderCloseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCloseService.class);

    public static final String TRADE_NOT_EXIST = "CARD00034";

    @Value("${pay.channel.url}")
    private String channelUrl;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private RestTemplate restTemplate;



    public boolean closeOrder(String partner, Long payType, String orderCode){
        Map<String, String> params = Maps.newHashMap();
        String serviceCode = getServiceCode(partner, payType);
        if (serviceCode == null){
            return true;
        }
        params.put("pay_type", serviceCode);
        params.put("order_code", orderCode);
        params.put("trans_time", DateHelper.getFormatDate(new Date(), "yyyyMMddHHmmss"));
        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(channelUrl+serviceCode+"-common/closeOrder", params, Map.class);
        Map<String, Object> result = responseEntity.getBody();
        LOGGER.debug("[closeOrder][orderCode:{}][result:{}]", orderCode, result);
        return result != null && (ResultCode.SUCCESS.getCode().equals(result.get("code"))
        || TRADE_NOT_EXIST.equals(result.get("code")));
    }


    public String getServiceCode(String partner, Long payType){
        PaymentType paymentType = partnerService.getAccesserPayment(partner, payType);
        if (paymentType.getPayCode().contains("ALI")){
            return "alipay";
        }else if (paymentType.getPayCode().contains("WECHAT")){
            return "wechat";
        }
        return null;
    }
}
