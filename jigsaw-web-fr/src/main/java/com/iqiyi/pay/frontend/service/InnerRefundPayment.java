package com.iqiyi.pay.frontend.service;

import com.iqiyi.kiwi.utils.DateHelper;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.service.refund.RefundService;
import com.iqiyi.pay.frontend.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by liuyanlong on 3/21/17.
 */
@Component
@Scope("prototype")
public class InnerRefundPayment extends AbstractPayment  {

    @Value("${third.query.url}")
    private String THIRD_CHANNEL_URL;

    @Autowired
    private RefundService refundService;

    @Override
    public void configure(Map<String, String> params) {

    }

    @Override
    public PayResult<Map<String, Object>> payRequest(String paymentCode) {
        return null;
    }

    @Override
    public void configureRefund(Map<String, String> params) {
        refundConfigs.put("trans_time",                 transTime());
        refundConfigs.put("refund_fee",                 params.get("refund_fee"));
        refundConfigs.put("orig_trans_time",            params.get("orig_trans_time"));
        refundConfigs.put("orig_order_code",            params.get("order_code"));
        refundConfigs.put("refund_code",                params.get("refund_code"));
        refundConfigs.put("orig_fee",                   params.get("orig_fee"));
        refundConfigs.put("orig_channel_trade_code",    params.get("orig_channel_trade_code"));
        refundConfigs.put("orig_channel_trade_time",    params.get("orig_channel_trade_time"));
    }

    @Override
    public PayResult<Map<String, Object>> refundRequest(String paymentCode) {
        LOGGER.info("inner refund request channel params:{}", refundConfigs);
        PayResult<Map<String, Object>> refundResult = this.getResultFromChannel(THIRD_CHANNEL_URL + paymentCode.toLowerCase() + "-common/refund", refundConfigs);
        LOGGER.info("inner refund channel orderCode:{} result:{}", refundConfigs.get("order_code"), refundResult);
        String refundCode = String.valueOf(refundConfigs.get("refund_code"));
        if (Constants.CHANNEL_REFUND_SUCCESS.equals(refundResult.getCode())){
            Map<String, Object> result = refundResult.getData();
            String fee = String.valueOf(result.get("refund_fee"));
            String thirdRefundCode = String.valueOf(result.get("channel_refund_code"));
            refundService.dealRefundResult(refundCode, thirdRefundCode, Long.parseLong(fee));
        } else if (!Constants.CHANNEL_REFUND_IN_PROCESS.equals(refundResult.getCode()) && refundResult.getData() != null){
            String errorMsg = refundResult.getData().get("channel_resp_msg")+"";
            refundService.dealRefundFail(refundCode, errorMsg);
        }
        return refundResult;
    }

    private String transTime(){
        return DateHelper.getDateStringByPattern(new Timestamp(System.currentTimeMillis()), "yyyyMMddHHmmss");
    }
}
