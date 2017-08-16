package com.iqiyi.pay.frontend.web.bank;

import com.google.common.base.Splitter;
import com.iqiyi.pay.common.security.PayUtils;
import com.iqiyi.pay.common.utils.result.IResult;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.dto.CheckIdentityResponse;
import com.iqiyi.pay.frontend.param.CheckIdentityParam;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.service.BankPayment;
import com.iqiyi.pay.frontend.service.Payment;
import com.iqiyi.pay.frontend.service.PaymentFactory;
import com.iqiyi.pay.frontend.service.bank.BankCardInfoService;
import com.iqiyi.pay.frontend.service.cache.BankInfoCacheManager;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.frontend.service.route.CardRouteService;
import com.iqiyi.pay.frontend.utils.Constants;
import com.iqiyi.pay.frontend.valid.IdcardValidator;
import com.iqiyi.pay.frontend.web.BaseController;
import com.iqiyi.pay.sdk.CardBankMap;
import com.iqiyi.pay.sdk.ChannelAccount;
import com.iqiyi.pay.sdk.PayTrade;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

/**
 * Created by leishengbao on 8/8/16.
 */
@RestController
@RequestMapping("/bank/")
public class CheckIdentityController extends BaseController<CheckIdentityParam> {

    @Autowired
    PaymentFactory paymentFactory;
    @Autowired
    PayTradeService payTradeService;
    @Autowired
    BankCardInfoService bankCardInfoService;
    @Autowired
    CardRouteService cardRouteService;
    @Autowired
    ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    BankInfoCacheManager bankInfoCacheManager;

    @Value("${bank.bind.need.cvv}")
    private String needCvv;
    @Value("${bank.bind.need.expire.time}")
    private String neewExpireTime;


    @RequestMapping("checkIdentity")
    public PayResult<CheckIdentityResponse> checkIdentity(@Valid CheckIdentityParam param, BindingResult result) {
        return super.commonInvoke(param, result);
    }



    @Override
    protected void generateResultData(PayResultBuilder builder, CheckIdentityParam param) {
        if (!validateRequest(builder, param)){
            return;
        }
        CardBankMap cardBankMap = bankCardInfoService.queryBankInfo(param.getCard_num());
        if (cardBankMap == null){
            builder.setResultCode(ResultCode.ERROR_OF_ROUTE_NOT_EXIST);
            return;
        }
        ChannelAccount channelAccount = cardRouteService.queryBankRouteByRate(cardBankMap.getBankCode(), cardBankMap.getCardType(), param.getPlatform(), param.getOrder_code());
        if (channelAccount == null){
            builder.setResultCode(ResultCode.ERROR_OF_ROUTE_NOT_EXIST);
            return;
        }

        //信用卡，根据配置规则，判断是否输入过期时间及CVV
        if(Constants.CREDIT_CARD_TYPE.equals(param.getCard_type())){
            if(Splitter.on(",").splitToList(needCvv).contains(channelAccount.getChannelCode())){
                if(StringUtils.isBlank(param.getCard_cvv2())||!NumberUtils.isNumber(param.getCard_cvv2())||StringUtils.length(param.getCard_cvv2()) != 3){
                    builder.setResultCode(ResultCode.ERROR_OF_CARD_CVV2_INVALID);
                    return;
                }
            }
            if(Splitter.on(",").splitToList(neewExpireTime).contains(channelAccount.getChannelCode())){
                if(StringUtils.isBlank(param.getCard_validity())){
                    builder.setResultCode(ResultCode.ERROR_OF_CARD_EXPIRE_TIME);
                    return;
                }
            }
        }

        PayTrade payTrade = payTradeService.queryOrderByCode(param.getOrder_code());
        String paymentCode = channelAccount.getChannelCode();
        Payment payment = paymentFactory.getPayment(paymentCode);
        ((BankPayment)payment).configureCheckId(PayUtils.genMapByRequestParas(getRequest().getParameterMap()));
        PayResult<Map<String, Object>> result = ((BankPayment)payment).checkIdentity(paymentCode);
        if (result == null || result.getData() == null){
            builder.setResultCode(ResultCode.ERROR_OF_CARD_INFO_VALID);
            return;
        }
        CheckIdentityResponse checkIdentityResult = new CheckIdentityResponse();
        try {
            BeanUtils.populate(checkIdentityResult, result.getData());
        } catch (Exception e) {
            LOGGER.error("[copy bean error, order_code:{}]", param.getOrder_code(), e);
        }
        builder.setResultCode(new IResult() {
            @Override
            public String getCode() {
                return result.getCode();
            }

            @Override
            public String getMsg() {
                return result.getMsg();
            }
        });
        checkIdentityResult.setOrder_code(param.getOrder_code());
        checkIdentityResult.setFee(payTrade.getRealFee());
        builder.setData(checkIdentityResult);
    }




    private boolean validateRequest(PayResultBuilder builder, CheckIdentityParam param){
        IdcardValidator idcardValidator = new IdcardValidator();
        if (!idcardValidator.isValidatedAllIdcard(param.getCert_num())){
            builder.setResultCode(ResultCode.ERROR_OF_CARD_CERT_INVALID);
            return false;
        }
        if (StringUtils.length(param.getCard_mobile()) != 11){
            builder.setResultCode(ResultCode.ERROR_OF_CARD_MOBILE_INVALID);
            return false;
        }
        return true;
    }

}
