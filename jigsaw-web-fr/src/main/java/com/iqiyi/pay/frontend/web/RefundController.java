package com.iqiyi.pay.frontend.web;

import com.google.common.collect.Maps;
import com.iqiyi.kiwi.utils.DateHelper;
import com.iqiyi.pay.common.security.PayUtils;
import com.iqiyi.pay.common.utils.StringUtil;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.aspect.Refund;
import com.iqiyi.pay.frontend.param.RefundParam;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.service.Payment;
import com.iqiyi.pay.frontend.service.PaymentFactory;
import com.iqiyi.pay.frontend.service.accesser.PartnerService;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.frontend.service.refund.RefundService;
import com.iqiyi.pay.frontend.utils.Constants;
import com.iqiyi.pay.sdk.PartnerPayKey;
import com.iqiyi.pay.sdk.PayAccesser;
import com.iqiyi.pay.sdk.PayRefundTrade;
import com.iqiyi.pay.sdk.PayTrade;
import com.iqiyi.pay.sdk.PaymentService;
import com.iqiyi.pay.sdk.TradeStatus;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by leishengbao on 9/2/16.
 */
@RestController
@RequestMapping("/frontend/")
public class RefundController extends BaseController<RefundParam>{


    public static final Logger LOGGER = LoggerFactory.getLogger(RefundController.class);

    @Autowired
    PayTradeService payTradeService;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private PaymentFactory paymentFactory;

    @Refund
    @RequestMapping(value = "refund", method = {RequestMethod.POST, RequestMethod.GET})
    public Map<String, String> refund(@Valid RefundParam param, BindingResult result){
        PayResult<Map<String, String>> refundResult = super.commonInvoke(param, result);
        Map<String, String> ret = Maps.newHashMap();
        ret.put("code", refundResult.getCode());
        ret.put("msg", refundResult.getMsg());
        if (refundResult.getData() != null){
            ret.putAll(refundResult.getData());
            ret.put("sign", genReturnSign(ret, param.getPartner()));
        }
        return ret;
    }


    @Override
    protected void generateResultData(PayResultBuilder builder, RefundParam param) {
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
        params.put("user_id", payTrade.getUserId());
        PaymentService paymentService = partnerService.queryPaymentServiceById(payTrade.getDestPayType());
        Payment payment = paymentFactory.refundPayment(partnerService.queryPaymentTypeById(payTrade.getSourcePayType()).getPayCode());
        payment.configureRefund(params);
        PayResult<Map<String, Object>> result = payment.refundRequest(paymentService.getPayCode());
        if (ResultCode.SUCCESS.getCode().equals(result.getCode())){
            builder.setResultCode(ResultCode.SUCCESS);
        }else if(Constants.CARD_PAY_IN_PROCESS.equals(result.getCode())) {
            builder.setResultCode(ResultCode.ERROR_OF_REFUND_IN_PROCCESS);
        } else {
            builder.setResultCode(ResultCode.ERROR_OF_REFUND_FAIL);
        }
        PayAccesser payAccesser = partnerService.getAccesser(param.getPartner());
        payRefundTrade = refundService.queryPayRefundTradeByPartnerRefundNo(param.getPartner_refund_no(), payAccesser.getId());
        builder.setData(getRefundMap(payRefundTrade, param));
    }



    public boolean validateRequest(PayResultBuilder builder, RefundParam param, PayTrade payTrade){
        if (payTrade == null){
            builder.setResultCode(ResultCode.ERROR_OF_ORDER_NOT_EXIT);
            return false;
        }
        if (payTrade.getStatus() != TradeStatus.PAID_VALUE){
            builder.setResultCode(ResultCode.ERROR_OF_ORDER_IS_UNPAYED);
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
        if (payAccesser == null || payAccesser.getId() != payTrade.getPartnerId() ||
                !payTrade.getPartnerOrderNo().equals(param.getPartner_order_no())){
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



    private PayRefundTrade createRefundOrder(RefundParam param, PayTrade payTrade){
        PayRefundTrade.Builder builder = PayRefundTrade.newBuilder();
        builder.setCreateTime(System.currentTimeMillis())
                .setUpdateTime(System.currentTimeMillis())
                .setRefundCode(StringUtil.createUniqueCode())
                .setSignCorpId(payTrade.getSignCorpId())
                .setDestPayType(payTrade.getDestPayType())
                .setFee(param.getFee())
                .setNotifyUrl(param.getNotify_url())
                .setOrderCode(payTrade.getOrderCode())
                .setPartnerId(payTrade.getPartnerId())
                .setPartnerOrderNo(payTrade.getPartnerOrderNo())
                .setPartnerRefundNo(param.getPartner_refund_no())
                .setSubject(payTrade.getSubject())
                .setStatus(2)
                .setUserId(payTrade.getUserId())
                .setThirdTradeCode(payTrade.getThirdTradeCode());
        if (StringUtils.isNotBlank(param.getReason())){
            builder.setReason(param.getReason());
        }
        if (StringUtils.isNotBlank(param.getExtend_params())){
            builder.setExtendParams(param.getExtend_params());
        }
        if (payTrade.hasAccountId()){
            builder.setAccountId(payTrade.getAccountId());
        }
        refundService.createPayRefundTrade(builder.build());
        return builder.build();
    }

    @Override
    protected boolean checkSign(RefundParam param) {
        boolean checkSign = false;
        try {
            PartnerPayKey partnerPayKey = partnerService.getPartnerPayKey(param.getPartner()).get();
            Map<String, String> params = PayUtils.genMapByRequestParas(getRequest().getParameterMap());
            if(PayUtils.doCheckMessageRequest(params,partnerPayKey.getValue())
                    ||PayUtils.doCheckMessageRequestNoIp(params,partnerPayKey.getValue())){
                checkSign = true;
            }
        } catch (Exception e) {
            LOGGER.error("[payTrade:{}]" ,e);
        }
        return checkSign;
    }



    private Map<String, String> getRefundMap(PayRefundTrade payRefundTrade, RefundParam param){
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


    private String genReturnSign(Map<String, String> params, String partner){
        PartnerPayKey partnerPayKey = partnerService.syncGetPartnerPayKey(partner);
        return PayUtils.signMessageRequest(params, partnerPayKey != null ? partnerPayKey.getAccesserPublickey() : null);
    }
}
