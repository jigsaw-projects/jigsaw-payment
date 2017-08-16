package com.iqiyi.pay.frontend.web.intercept;

import com.iqiyi.pay.common.security.PayUtils;
import com.iqiyi.pay.frontend.aspect.Refund;
import com.iqiyi.pay.frontend.service.accesser.PartnerService;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.sdk.PayTrade;
import com.iqiyi.pay.sdk.PaymentType;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by leishengbao on 12/26/16.
 */
public class RefundInterceptor extends HandlerInterceptorAdapter {

    @Value("${pay.refund.url}")
    private String refundUrl;
    @Value("${frontend.refund.payType}")
    private String frontednRefundPayTypes;
    @Autowired
    PayTradeService payTradeService;
    @Autowired
    private PartnerService partnerService;
    @Resource(name = "payApiRefundOkHttpClient")
    private OkHttpClient okHttpClient;

    @Override
    public boolean preHandle(HttpServletRequest servletRequest, HttpServletResponse servletResponse, Object handler) throws Exception {
        HandlerMethod hm = (HandlerMethod) handler;
        if (hm.getMethodAnnotation(Refund.class) == null) {
            return true;
        }
        String payType = getOrderPayType(servletRequest);
        if (payType != null && !frontednRefundPayTypes.contains(payType)){
            Response response = invokeOriginRefund(servletRequest);
            for (String key : response.headers().names()){
                List<String> values = response.headers().values(key);
                for (String value : values){
                    if (key.equals("Transfer-Encoding")){
                        continue;
                    }
                    servletResponse.addHeader(key, value);
                }
            }
            servletResponse.getWriter().write(response.body().string());
            servletResponse.flushBuffer();
            return false;
        }
        return true;
    }



    private String getOrderPayType(HttpServletRequest request){
        String orderCode = request.getParameter("order_code");
        if (orderCode == null){
            return null;
        }
        PayTrade trade = payTradeService.queryOrderByCode(orderCode);
        if (trade == null){
            return null;
        }
        PaymentType paymentType = partnerService.queryPaymentTypeById(trade.getSourcePayType());

        if (paymentType == null){
            return null;
        }
        return paymentType.getPayCode();
    }



    private Response invokeOriginRefund(HttpServletRequest servletRequest){
        Map<String, String> params = PayUtils.genMapByRequestParas(servletRequest.getParameterMap());
        FormEncodingBuilder builder =  new FormEncodingBuilder();
        for (String key : params.keySet()){
            builder.add(key, params.get(key));
        }
        Request request = new Request.Builder()
                .url(refundUrl)
                .post(builder.build())
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
