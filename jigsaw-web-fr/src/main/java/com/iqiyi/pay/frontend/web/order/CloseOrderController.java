package com.iqiyi.pay.frontend.web.order;

import com.iqiyi.pay.common.security.PayUtils;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.service.accesser.PartnerService;
import com.iqiyi.pay.frontend.service.coupon.CouponService;
import com.iqiyi.pay.frontend.service.order.OrderCloseService;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.frontend.utils.Ret;
import com.iqiyi.pay.sdk.PartnerPayKey;
import com.iqiyi.pay.sdk.PayAccesser;
import com.iqiyi.pay.sdk.PayTrade;
import com.iqiyi.pay.web.spt.annotation.Para;
import com.iqiyi.pay.web.spt.annotation.ParamValid;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by leishengbao on 10/21/16.
 */
@RestController
@RequestMapping("/frontend/")
public class CloseOrderController {

    @Autowired
    private PayTradeService tradeService;
    @Autowired
    private OrderCloseService orderCloseService;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private HttpServletRequest request;

    @ParamValid
    @RequestMapping("/closeOrder")
    public String closeOrder(
            @NotNull @Para("user_id") long userId,
            @NotBlank @Para("partner_order_no") String parterOrderNo,
            @NotBlank @Para("partner") String partner,
            @NotNull @Para("contains_coupon") @Max(value = 1) @Min(value = 0) int containsCoupon,
            @NotBlank @Para("sign")String sign
            ) {
        PayAccesser accesser = partnerService.getAccesser(partner);
        if (accesser == null){
            return Ret.toJson(ResultCode.ERROR_OF_ACCESSER_NOT_EXIST);
        }
        if (!checkSign(accesser)){
            return Ret.toJson(ResultCode.ERROR_OF_SIGN_ERROR);
        }
        PayTrade payTrade = tradeService.queryPayTadeByPartnerOrderNo(parterOrderNo, accesser.getId());
        if (payTrade == null || payTrade.getStatus() == 1 || payTrade.getStatus() == 7){
            return Ret.toJson(ResultCode.ERROR_OF_ORDER_CLOSE);
        }
        orderCloseService.closeOrder(partner, payTrade.getSourcePayType(), payTrade.getOrderCode());
        boolean unfreezeCoupon = true;
        if (containsCoupon == 1){
            unfreezeCoupon = couponService.unFrozenCoupon(payTrade.getPartnerOrderNo());
        }
        if (unfreezeCoupon){
            tradeService.closePayTradeByCode(payTrade.getOrderCode());
            return Ret.toJson(ResultCode.SUCCESS);
        }
        return Ret.toJson(ResultCode.ERROR_OF_FAIL);
    }


    private boolean checkSign(PayAccesser accesser){
        Future<PartnerPayKey> future = partnerService.getPartnerPayKey(accesser.getAliasName());
        try {
            PartnerPayKey partnerPayKey = future.get();
            String signType = partnerPayKey.getSignType();
            String key = partnerPayKey.getPayPublickey();
            if("MD5".equals(signType)) {
                key = partnerPayKey.getValue();
            }else if("RSA".equals(signType)||"DSA".equals(signType)){
                key = partnerPayKey.getPayPublickey();
            }
            Map<String, String> params = PayUtils.genMapByRequestParas(request.getParameterMap());
            if(PayUtils.doCheckMessageRequest(params,key)||PayUtils.doCheckMessageRequestNoIp(params,key)){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



}
