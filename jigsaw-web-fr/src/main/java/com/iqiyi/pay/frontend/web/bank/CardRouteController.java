package com.iqiyi.pay.frontend.web.bank;

import com.google.common.base.Splitter;
import com.iqiyi.pay.frontend.dto.CardRouteResponse;
import com.iqiyi.pay.frontend.param.BankAdditionProtocol;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.service.accesser.PartnerService;
import com.iqiyi.pay.frontend.service.bank.BankCardInfoService;
import com.iqiyi.pay.frontend.service.cache.BankInfoCacheManager;
import com.iqiyi.pay.frontend.service.id.IdService;
import com.iqiyi.pay.frontend.service.id.IdService.IdCard;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.frontend.service.pwd.PasswordService;
import com.iqiyi.pay.frontend.service.route.CardRouteService;
import com.iqiyi.pay.frontend.utils.Constants;
import com.iqiyi.pay.frontend.utils.Ret;
import com.iqiyi.pay.sdk.BankCardCertification;
import com.iqiyi.pay.sdk.CardBankMap;
import com.iqiyi.pay.sdk.ChannelAccount;
import com.iqiyi.pay.sdk.PayTrade;
import com.iqiyi.pay.sdk.PayUserCard;
import com.iqiyi.pay.web.spt.annotation.Para;
import com.iqiyi.pay.web.spt.annotation.ParamValid;
import com.iqiyi.pay.web.spt.annotation.Sign;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * Created by leishengbao on 8/22/16.
 */
@RestController
@RequestMapping("/bank/")
public class CardRouteController {

    protected Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private IdService idService;
    @Autowired
    private BankCardInfoService cardInfoService;
    @Autowired
    private PasswordService passwordService;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private BankInfoCacheManager bankInfoCacheManager;
    @Autowired
    private CardRouteService cardRouteService;
    @Autowired
    private PayTradeService payTradeService;
    @Autowired
    private BankAdditionProtocol bankAdditionProtocol;

    @Value("${bank.protocol.url}")
    private String protocolUrl;
    @Value("${bank.protocol.name}")
    private String protocolName;
    @Value("${addition.protocol.name}")
    private String additionProtocolName;
    @Value("${bank.bind.need.cvv}")
    private String needCvv;
    @Value("${bank.bind.need.expire.time}")
    private String needExpireTime;

    @ParamValid
    @Sign
    @RequestMapping(value = "/route",produces = "application/json")
    public String cardRoute(
    		@NotNull @Para("uid")long userId,
    		@NotBlank @Para("card_num")String cardNo,
    		@NotBlank @Para("order_code")String orderCode,
            @NotBlank @Para("is_contract")String isContract,
    		@NotBlank @Para("platform")String platform,
            @NotBlank @Para("authcookie")String authcookie,
            @NotBlank @Para("sign")String sign
    		) {

        if (cardNo.length() < 16 || cardNo.length() > 19){
            LOGGER.info("cardRouteController bank route cardNo length error userId:{} cardNo:{} orderCode:{}",userId,cardNo,orderCode);
            return Ret.toJson(ResultCode.ERROR_OF_CARD_VALID);
        }

    	CardBankMap card = cardInfoService.queryBankInfo(cardNo);
        if (card == null){
            LOGGER.info("cardRouteController bank route cardBankMap is null userId:{} cardNo:{} orderCode:{}",userId,cardNo,orderCode);
            return Ret.toJson(ResultCode.ERROR_OF_CARD_VALID);
        }
        boolean hasPwd = passwordService.isPasswordSet(userId);
        if (partnerService.notSupportsCredit(orderCode) && card.getCardType() == 2){
            LOGGER.info("cardRouteController bank route bank not support userId:{} cardNo:{} orderCode:{}",userId,cardNo,orderCode);
            return Ret.toJson(ResultCode.ERROR_OF_NO_CREDIT);
        }

        BankCardCertification bcc = cardInfoService.queryBankCertifycation(String.valueOf(userId), cardNo);
        PayUserCard payUserCard = null;
        if (bcc != null){
            payUserCard = cardInfoService.getPayUserCard(bcc.getId());
        }
        boolean hasBindCard = payUserCard !=null && payUserCard.getStatus() == 1;
        if (hasBindCard && hasPwd){
            LOGGER.info("cardRouteController bank route user:{} had already bound this card:{} orderCode:{}",userId,cardNo,orderCode);
            return Ret.toJson(ResultCode.ERROR_OF_CARD_HAS_BIND);
        }else if (hasBindCard && !hasPwd){
            LOGGER.info("cardRouteController bank route user:{} had already bound this card:{} orderCode:{},pwd not set",userId,cardNo,orderCode);
            return Ret.toJson(ResultCode.ERROR_OF_CARD_HAS_BIND_PWD_NOT_SET);
        }

        ChannelAccount channelAccount = cardRouteService.queryBankRouteByRate(card.getBankCode(), card.getCardType(), platform, orderCode);
        if (channelAccount == null){
            LOGGER.info("cardRouteController bank route channelAccount is null cardType:{} platform:{} orderCode:{}",card.getCardType(),platform,orderCode);
            return Ret.toJson(ResultCode.ERROR_OF_ROUTE_NOT_EXIST);
        }
        CardRouteResponse cardRouteResponse = new CardRouteResponse();
        cardRouteResponse.setCard_num_last(cardNo.substring(cardNo.length()-4));
        cardRouteResponse.setUid(String.valueOf(userId));
        cardRouteResponse.setOrder_code(orderCode);
        cardRouteResponse.setIs_unionpay("0");//是否银联通道 0：否 1：是
        cardRouteResponse.setBank_code(card.getBankCode());
        cardRouteResponse.setBank_name(card.getBankName());
        cardRouteResponse.setCard_type(String.valueOf(card.getCardType()));
        String card_type_string;
        if (card.getCardType() == 2){
            card_type_string = "信用卡";
            if(Splitter.on(",").splitToList(needCvv).contains(channelAccount.getChannelCode())){
                cardRouteResponse.setNeedCvv(true);
            }
            if(Splitter.on(",").splitToList(needExpireTime).contains(channelAccount.getChannelCode())){
                cardRouteResponse.setNeedExpireTime(true);
            }
        }else {
            card_type_string = "储蓄卡";
        }
        cardRouteResponse.setCard_type_string(card_type_string);
        IdCard id = idService.getIdInfo(userId);
        cardRouteResponse.setId_card(id.idNo);
        cardRouteResponse.setUser_name(id.idName);
        cardRouteResponse.setBank_protocol_url(protocolUrl);
        cardRouteResponse.setBank_protocol_name(protocolName);

        //绑卡页面，钱包协议+银行协议的url和协议名称 统一后端返回
        String additionProtocolUrl = bankAdditionProtocol.getCODE().get(card.getBankCode()+"-"+card.getCardType());
        if(StringUtils.isNotBlank(additionProtocolUrl)){
            cardRouteResponse.setAddition_protocol_name(additionProtocolName);
            cardRouteResponse.setAddition_protocol_url(additionProtocolUrl);
        }

        cardRouteResponse.setIs_wallet_pwd_set(hasPwd ? "1" : "0");
        bankInfoCacheManager.setOrderContractInfo(orderCode, isContract);
        PayTrade payTrade = payTradeService.preparePay(orderCode, channelAccount, cardNo, platform, card.getCardType());
        cardRouteResponse.setSubject(payTrade.getSubject());
        cardRouteResponse.setFee(payTrade.getRealFee());
        cardRouteResponse.setOff_price(payTrade.getFee()-payTrade.getRealFee());
        if (payTrade.getFee() != payTrade.getRealFee()){
            cardRouteResponse.setHas_off(true);
        }
        //统计需求，打印日志
        LOGGER.info("[{}:{type={}, partnerId={}, result={}, paltform={}, orderCode={}, isContract={}, bankCode={}, serviceCode={}}]", "statistics", "CardSign", payTrade==null?0:payTrade.getPartnerId(), "enter",
                platform , orderCode, Constants.CONTRACT_TAG.equals(isContract), card.getBankCode(), channelAccount.getChannelCode());
        return Ret.toJson(ResultCode.SUCCESS, cardRouteResponse);
    }


}
