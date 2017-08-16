package com.iqiyi.pay.frontend.web;

import com.google.common.base.Splitter;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.param.ChannelOrderQueryParam;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.service.Payment;
import com.iqiyi.pay.frontend.service.PaymentFactory;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.sdk.PayTrade;
import com.iqiyi.pay.sdk.Trade;
import com.iqiyi.pay.sdk.TradeStatus;
import com.iqiyi.pay.sdk.service.RouteService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Map;

/**
 * Created by liuyanlong on 3/17/17.
 * 对账查询渠道订单处理类
 */
@Controller
@RequestMapping("/frontend/")
public class QueryChannelOrderController extends BaseController<ChannelOrderQueryParam> {

    @Autowired
    private PayTradeService payTradeService;

    @Autowired
    private com.iqiyi.pay.client.RouteService routeService;

    @Autowired
    private PaymentFactory paymentFactory;

    @Value("${wechat.partner.id}")
    private String wechatPartnerId;

    @Value("${alipay.partner.id}")
    private String alipayPartnerId;

    @ResponseBody
    @RequestMapping(value = "/channelQuery",method = {RequestMethod.POST,RequestMethod.GET})
    protected PayResult queryOrder(@Valid ChannelOrderQueryParam param, BindingResult result) {
        return super.commonInvoke(param, result);
    }

    @Override
    protected void generateResultData(PayResultBuilder builder, ChannelOrderQueryParam param) {
        PayTrade payTrade = payTradeService.queryOrderByCode(param.getOrder_code());

        if(payTrade == null){
            LOGGER.info("order not exists:{} resultCode:{}",param.getOrder_code(),ResultCode.ERROR_OF_ORDER_PAYED);
            builder.setResultCode(ResultCode.ERROR_OF_ORDER_NOT_EXIT);
            return;
        }
        //查询支付中心订单状态，如果已支付，直接返回
        if(payTrade.getStatus() != TradeStatus.UNPAID_VALUE){
            LOGGER.info("order status illegal orderCode:{} resultCode:{}",param.getOrder_code(),ResultCode.ERROR_OF_ORDER_STATUS);
            builder.setResultCode(ResultCode.ERROR_OF_ORDER_STATUS);
            return;
        }
        //查询支付大类
        RouteService.QueryPaymentServiceByIdRequest.Builder request = RouteService.QueryPaymentServiceByIdRequest.newBuilder();
        request.setPaymentServiceId(payTrade.getDestPayType());
        RouteService.QueryPaymentServiceByIdResponse response =  routeService.queryPaymentServiceById(request.build());
        if(response.getPaymentService() == null){
            LOGGER.info("can not find payService orderCode:{} resultCode:{}",param.getOrder_code(),ResultCode.ERROR_OF_PAY_SERVICE_NOT_EXIT);
            builder.setResultCode(ResultCode.ERROR_OF_PAY_SERVICE_NOT_EXIT);
            return;
        }
        long partnerId =  response.getPaymentService().getPartnerId();
        //转换支付大类ID为对应pay_code
        String payCode = convertPayCode(partnerId);
        if(StringUtils.isBlank(payCode)){
            LOGGER.info("pay service not support orderCode:{} resultCode:{}",param.getOrder_code(),ResultCode.ERROR_OF_PAY_SERVICE_NOT_SUPPORT);
            builder.setResultCode(ResultCode.ERROR_OF_PAY_SERVICE_NOT_SUPPORT);
            return;
        }
        Map<String,String> params = new HashedMap();
        params.put("orig_order_code",payTrade.getOrderCode());
        params.put("orig_trans_type","1");
        Payment payment = paymentFactory.getPayment("CHANNELQUERY");
        payment.configure(params);
        //请求支付渠道查询渠道订单状态
        PayResult<Map<String, Object>> result = payment.queryRequest(payCode);
        LOGGER.info("channel order query result:{} orderCode;{}",result,param.getOrder_code());
        //超时或者异常
        if(MapUtils.isEmpty(result.getData())){
            LOGGER.info("channel return null orderCode:{} resultCode:{}",param.getOrder_code(),ResultCode.ERROR_OF_CHANNEL_RETURN_NULL);
            builder.setResultCode(ResultCode.ERROR_OF_CHANNEL_RETURN_NULL);
            return;
        }
        //查询失败
        if(!result.getCode().equals("10000")){
            LOGGER.info("channel process failed orderCode:{}",param.getOrder_code());
            builder.setCode(result.getCode()).setMsg(result.getMsg());
            return;
        }
        //渠道订单为 未支付
        if(!"1".equals(String.valueOf(result.getData().get("orig_order_status")))){
            LOGGER.info("channel status not paid orderCode:{} resultCode:{}",param.getOrder_code(),ResultCode.ERROR_OF_ORDER_IS_UNPAYED);
            builder.setResultCode(ResultCode.ERROR_OF_ORDER_IS_UNPAYED);
            return;
        }
        //准备更新订单数据
        long thridPayTime = System.currentTimeMillis();
        PayTrade.Builder trade =PayTrade.newBuilder();
        trade.setOrderCode(param.getOrder_code());
        trade.setThirdTradeCode(String.valueOf(result.getData().get("orig_trans_seq")));
        trade.setPayTime(System.currentTimeMillis());
        trade.setThirdPayTime(thridPayTime);
        trade.setThirdCreateTime(thridPayTime);
        trade.setStatus(TradeStatus.PAID.getNumber());

        //订单更新 失败提示调用方重试 成功直接返回成功
        if(payTradeService.updateTrade(trade.build())){
            LOGGER.info("channel query process success orderCode:{} resultCode:{}",param.getOrder_code(),ResultCode.SUCCESS);
            builder.setResultCode(ResultCode.SUCCESS);
            return;
        }else {
            LOGGER.info("order status update failed orderCode:{} resultCode:{}",param.getOrder_code(),ResultCode.ERROR_OF_CHANNEL_QUERY_SUCCESS_UPDATE_FAILED);
            builder.setResultCode(ResultCode.ERROR_OF_CHANNEL_QUERY_SUCCESS_UPDATE_FAILED);
            return;
        }
    }

    /**
     * partnerId转换为对应到code，请求渠道使用
     * @param partnerId
     * @return
     */
    private String convertPayCode(long partnerId){

        if(Splitter.on(",").splitToList(wechatPartnerId).contains(String.valueOf(partnerId))){
            return "wechat";
        }
        if(Splitter.on(",").splitToList(alipayPartnerId).contains(String.valueOf(partnerId))){
            return "alipay";
        }
        return null;
    }
}
