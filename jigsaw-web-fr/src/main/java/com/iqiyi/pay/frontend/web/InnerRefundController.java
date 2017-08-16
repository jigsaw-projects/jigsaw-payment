package com.iqiyi.pay.frontend.web;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.iqiyi.kiwi.utils.DateHelper;
import com.iqiyi.pay.common.utils.StringUtil;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.param.InnerRefundParam;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.service.Payment;
import com.iqiyi.pay.frontend.service.PaymentFactory;
import com.iqiyi.pay.frontend.service.accesser.PartnerService;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.frontend.service.refund.RefundService;
import com.iqiyi.pay.frontend.utils.Constants;
import com.iqiyi.pay.sdk.*;
import com.iqiyi.pay.sdk.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by liuyanlong on 3/20/17.
 * 对账差异需要退款的订单，请求这个处理类
 */
@RestController
@RequestMapping("/frontend/")
public class InnerRefundController extends BaseController<InnerRefundParam>{

    @Autowired
    PayTradeService payTradeService;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private PaymentFactory paymentFactory;
    @Autowired
    private com.iqiyi.pay.client.RouteService routeService;

    @Value("${wechat.partner.id}")
    private String wechatPartnerId;

    @Value("${alipay.partner.id}")
    private String alipayPartnerId;



    @RequestMapping(value = "innerRefund", method = {RequestMethod.POST, RequestMethod.GET})
    public Map<String, String> refund(@Valid InnerRefundParam param, BindingResult result){
        PayResult<Map<String, String>> refundResult = super.commonInvoke(param, result);
        Map<String, String> ret = Maps.newHashMap();
        ret.put("code", refundResult.getCode());
        ret.put("msg", refundResult.getMsg());
        if (refundResult.getData() != null){
            ret.putAll(refundResult.getData());
        }
        return ret;
    }

    @Override
    protected void generateResultData(PayResultBuilder builder, InnerRefundParam param) {
        PayTrade payTrade = payTradeService.queryOrderByCode(param.getOrder_code());
        if (!validateRequest(builder, param, payTrade)){
            return;
        }
        PayRefundTrade payRefundTrade = createRefundOrder(param, payTrade);
        Map<String, String> params = Maps.newHashMap();
        params.put("order_code", param.getOrder_code());
        params.put("refund_code", payRefundTrade.getRefundCode());
        params.put("refund_fee", String.valueOf(param.getFee()));
        params.put("orig_trans_time", DateHelper.getDateStringByPattern(new Timestamp(payTrade.getPayTime()), "yyyyMMddHHmmss"));
        params.put("orig_fee", String.valueOf(payTrade.getFee()));
        params.put("orig_channel_trade_code", payTrade.getThirdTradeCode());
        params.put("orig_channel_trade_time", DateHelper.getDateStringByPattern(new Timestamp(payTrade.getPayTime()), "yyyyMMddHHmmss"));
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
        if(org.apache.commons.lang3.StringUtils.isBlank(payCode)){
            LOGGER.info("pay service not support orderCode:{} resultCode:{}",param.getOrder_code(),ResultCode.ERROR_OF_PAY_SERVICE_NOT_SUPPORT);
            builder.setResultCode(ResultCode.ERROR_OF_PAY_SERVICE_NOT_SUPPORT);
            return;
        }
        Payment payment = paymentFactory.getPayment("INNERREFUND");
        payment.configureRefund(params);
        PayResult<Map<String, Object>> result = payment.refundRequest(payCode);
        if (Constants.CHANNEL_REFUND_SUCCESS.equals(result.getCode())){
            builder.setResultCode(ResultCode.SUCCESS);
        }else if(Constants.CHANNEL_REFUND_IN_PROCESS.equals(result.getCode())) {
            builder.setResultCode(ResultCode.ERROR_OF_REFUND_IN_PROCCESS);
        } else {
            builder.setResultCode(ResultCode.ERROR_OF_REFUND_FAIL);
        }
        PayAccesser payAccesser = partnerService.getAccesser(param.getPartner());
        payRefundTrade = refundService.queryPayRefundTradeByPartnerRefundNo(param.getPartner_refund_no(), payAccesser.getId());
        builder.setData(getRefundMap(payRefundTrade, param));
    }

    public boolean validateRequest(PayResultBuilder builder, InnerRefundParam param, PayTrade payTrade){
        if (payTrade == null){
            builder.setResultCode(ResultCode.ERROR_OF_ORDER_NOT_EXIT);
            return false;
        }
        if (payTrade.getStatus() != TradeStatus.CLOSE_PAYED_VALUE){
            builder.setResultCode(ResultCode.ERROR_OF_ORDER_STATUS);
            return false;
        }
        if (!payTrade.hasDestPayType()){
            builder.setResultCode(ResultCode.ERROR_OF_PAYTYPE_NOT_EXIST);
            return false;
        }
        if (payTrade.getFee() < param.getFee()){
            builder.setResultCode(ResultCode.ERROR_OF_FEE);
            return false;
        }
        PayAccesser payAccesser = partnerService.getAccesser(param.getPartner());
        if (payAccesser == null || payAccesser.getId() != payTrade.getPartnerId()){
            builder.setResultCode(ResultCode.ERROR_OF_PARAM_INVALID);
            return false;
        }
        PayRefundTrade payRefundTrade = refundService.queryPayRefundTradeByPartnerRefundNo(param.getPartner_refund_no(), payAccesser.getId());
        if (payRefundTrade != null){
            builder.setResultCode(ResultCode.ERROR_OF_REFUND_EXIST);
            return false;
        }
        if (refundService.getFeeByOrderCodeAndStatus(payTrade.getOrderCode())+param.getFee() > payTrade.getFee()){
            builder.setResultCode(ResultCode.ERROR_OF_REFUND_FEE_OVERFLOW);
            return false;
        }
        return true;
    }

    private PayRefundTrade createRefundOrder(InnerRefundParam param, PayTrade payTrade){
        PayRefundTrade.Builder builder = PayRefundTrade.newBuilder();
        builder.setCreateTime(System.currentTimeMillis())
                .setUpdateTime(System.currentTimeMillis())
                .setRefundCode(StringUtil.createUniqueCode())
                .setSignCorpId(payTrade.getSignCorpId())
                .setDestPayType(payTrade.getDestPayType())
                .setFee(param.getFee())
                .setOrderCode(payTrade.getOrderCode())
                .setPartnerId(payTrade.getPartnerId())
                .setPartnerOrderNo(payTrade.getPartnerOrderNo())
                .setPartnerRefundNo(param.getPartner_refund_no())
                .setSubject(payTrade.getSubject())
                .setStatus(2)
                .setUserId(payTrade.getUserId())
                .setThirdTradeCode(payTrade.getThirdTradeCode());
        if (payTrade.hasAccountId()){
            builder.setAccountId(payTrade.getAccountId());
        }
        refundService.createPayRefundTrade(builder.build());
        return builder.build();
    }

    private Map<String, String> getRefundMap(PayRefundTrade payRefundTrade, InnerRefundParam param){
        Map<String, String> map = Maps.newHashMap();
        map.put("partner", param.getPartner());
        map.put("uid", payRefundTrade.getUserId());
        map.put("create_time", DateHelper.getDateStringByPattern(new Timestamp(payRefundTrade.getCreateTime()), "yyyy-MM-dd HH:mm:ss"));
        if (payRefundTrade.hasRefundTime()){
            map.put("refund_time", DateHelper.getDateStringByPattern(new Timestamp(payRefundTrade.getRefundTime()), "yyyy-MM-dd HH:mm:ss"));
        }
        map.put("partner_order_no", payRefundTrade.getPartnerOrderNo());
        map.put("partner_refund_no", payRefundTrade.getPartnerRefundNo());
        map.put("order_code", payRefundTrade.getOrderCode());
        map.put("refund_code",payRefundTrade.getRefundCode());
        map.put("status",payRefundTrade.getStatus()+"");
        map.put("reason",payRefundTrade.getReason());
        map.put("fee",payRefundTrade.getFee()+"");
        map.put("real_fee",payRefundTrade.getRealFee()+"");
        map.put("charset","utf-8");
        map.put("extra_common_param",payRefundTrade.getExtraCommonParam());
        if (payRefundTrade.hasErrorMsg()){
            map.put("error_msg", payRefundTrade.getErrorMsg());
        }
        return map;
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
