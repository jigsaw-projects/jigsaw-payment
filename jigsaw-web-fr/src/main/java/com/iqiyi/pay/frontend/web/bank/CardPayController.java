/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.web.bank;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.iqiyi.pay.common.utils.RequestUtils;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.exception.PwdErrorException;
import com.iqiyi.pay.frontend.exception.PwdFrozenException;
import com.iqiyi.pay.frontend.exception.PwdNotExistException;
import com.iqiyi.pay.frontend.exception.SmsCodeErrorException;
import com.iqiyi.pay.frontend.param.ResultCode;
import com.iqiyi.pay.frontend.risk.RiskAssessment;
import com.iqiyi.pay.frontend.risk.RiskAssessmentHandler;
import com.iqiyi.pay.frontend.service.Payment;
import com.iqiyi.pay.frontend.service.PaymentFactory;
import com.iqiyi.pay.frontend.service.accesser.PartnerService;
import com.iqiyi.pay.frontend.service.bank.BankCardInfoService;
import com.iqiyi.pay.frontend.service.bank.CardDutBindManager;
import com.iqiyi.pay.frontend.service.order.OrderPayService;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.frontend.service.passport.PassPortUserInfoService;
import com.iqiyi.pay.frontend.service.pwd.PasswordService;
import com.iqiyi.pay.frontend.service.sms.SMSCodeService;
import com.iqiyi.pay.frontend.utils.Constants;
import com.iqiyi.pay.frontend.utils.Ret;
import com.iqiyi.pay.sdk.BankCardCertification;
import com.iqiyi.pay.sdk.PayTrade;
import com.iqiyi.pay.sdk.PayUserCard;
import com.iqiyi.pay.sdk.UserInfo;
import com.iqiyi.pay.web.spt.annotation.Para;
import com.iqiyi.pay.web.spt.annotation.ParamValid;
import com.iqiyi.pay.web.spt.annotation.Sign;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年8月29日
 */
@RestController
@RequestMapping("/bank/")
public class CardPayController {
	
	private Logger logger = LoggerFactory.getLogger(CardPayController.class);

	@Value("${cardpay.risk.url}")
	private String riskUrl;


	@Value("${card.risk.limit}")
	private long cardPayRiskLimit;

	@Autowired
	private PasswordService pwdService;
	
	@Autowired
	private BankCardInfoService bankCardService;
	
	@Autowired
	private PaymentFactory paymentFactory;
	
	@Autowired
	private OrderPayService orderService;
	
	@Autowired
	private PayTradeService  tradeService;
	
	@Autowired
	private SMSCodeService smss;

	@Autowired
	private PassPortUserInfoService passPortUserInfoService;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private PartnerService partnerService;

	@Autowired
	private CardDutBindManager cardDutBindManager;

	@Autowired
	private HttpServletRequest request;

	@ParamValid
	@Sign
	@RequestMapping("pay")
	public String cardPay(
			@NotNull  @Para("uid")long userId,
			@NotBlank @Para("order_code")String orderCode,
			@NotNull  @Para("card_id")long cardId,
			@NotBlank @Para("password")String pwd,
			@Para(value = "sms_key", required = false)String smsKey,
			@Para(value = "sms_code", required = false)String smsCode,
			@NotBlank @Para("platform")String platform,
			@NotBlank @Para("authcookie")String authcookie,
			@NotBlank @Para("sign")String sign
			) throws PwdFrozenException, PwdErrorException, PwdNotExistException {

		BankCardCertification cardCert = bankCardService.getBankCard(cardId);
		if (cardCert == null) {
			logger.info("card_cert_not _found: card_id: {}", cardId);
			return Ret.toJson(ResultCode.ERROR_OF_CARD_NOT_EXIT);
		}

		PayTrade trade = tradeService.queryOrderByCode(orderCode);
		if (trade == null) {
			return Ret.toJson(ResultCode.ERROR_OF_ORDER_NOT_EXIT);
		}
		if (trade.getStatus() == 1){
			return Ret.toJson(ResultCode.ERROR_OF_ORDER_PAYED);
		}

		PayUserCard card = bankCardService.getPayUserCard(cardId);
		//异常请求
		if (card == null || card.getUserId() != userId || card.getStatus() != 1) {
			logger.info("card_user_err: uid:{}, card_id: {} card_status:{}", userId, cardId);
			return Ret.toJson(ResultCode.ERROR_OF_PARAM_INVALID);
		}


		if (partnerService.notSupportsCredit(trade) && card.getCardType() == 2){
			return Ret.toJson(ResultCode.ERROR_OF_NO_CREDIT);
		}

		logger.info("[{}:{type={}, partnerId={}, result={}, paltform={}, orderCode={}, bankCode={}, serviceCode={}}]", "statistics", "CardPay", trade.getPartnerId(), "enter" , platform , orderCode, cardCert.getBankCode(), card.getPayType().toLowerCase());

		pwdService.checkPassword(userId, pwd, platform);

		PayResult<Map<String, String>> riskResult = riskProcess(trade, card, cardCert, smsKey, smsCode);
		if (!ResultCode.SUCCESS.getCode().equals(riskResult.getCode())){
			return riskResult.toJson();
		}

		//更新支付服务，商户号等信息，Async异步调用
		trade = tradeService.prepayPayByChannelCode(cardId, card.getPayType().toLowerCase(), orderCode,
				userId, cardCert.getCardNumber(), platform, (int)card.getCardType());

		Map<String, String> params = Maps.newHashMap();
		params.put("uid", userId+"");
		params.put("user_name", cardCert.getOwnerName());
		params.put("card_num", cardCert.getCardNumber());
		params.put("card_type", cardCert.getCardType()+"");
		params.put("card_mobile", cardCert.getBindPhone());
		params.put("cert_type", Constants.ID_CARD_CERT_TYPE);
		params.put("cert_num", cardCert.getOwnerIdNum());
		params.put("order_name", trade.getSubject());
		params.put("order_description",trade.getDescription());
		params.put("subject",trade.getSubject());
		params.put("order_code", orderCode);
		params.put("fee", trade.getRealFee()+"");
		params.put("contract_id", card.getToken());
		params.put("device_ip", request.getRemoteAddr());

		String payServiceCode = card.getPayType();
		Payment payment = paymentFactory.getPayment(payServiceCode);
		payment.configure(params);
		PayResult<Map<String, Object>> ret = payment.payRequest(payServiceCode.toLowerCase());
		logger.info("[{}:{type={}, partnerId={}, result={}, paltform={}, orderCode={}, bankCode={}, serviceCode={}}]", "statistics", "CardPay", trade.getPartnerId(),
				ret.getCode().equals(Constants.CARD_PAY_IN_PROCESS) ? ResultCode.SUCCESS.getCode() : ret.getCode() , platform , orderCode, cardCert.getBankCode(), card.getPayType().toLowerCase());
		return afterOrderPay(ret, orderCode, trade, payServiceCode.toLowerCase());
	}


	private String afterOrderPay(PayResult<Map<String, Object>> ret, String orderCode, PayTrade trade, String serviceCode){
		String billNo = Objects.toString(ret.getData().get("trans_seq"));
		orderService.saveBankBillNo(billNo, orderCode);
		Map<String, Object> orderPayedData = Maps.newHashMap();
		orderPayedData.put("order_code", orderCode);
		orderPayedData.put("fee",  trade.getRealFee());
		if (ResultCode.SUCCESS.getCode().equals(ret.getCode())){
			orderService.orderPayedByServiceCode(ret.getData(), serviceCode);
			cardDutBindManager.dealCardDut(trade);//处理银行卡签约代扣逻辑
			orderPayedData.put("order_status", "1");
		}else if (Constants.CARD_PAY_IN_PROCESS.equals(ret.getCode())){
			orderPayedData.put("order_status", "1");
			cardDutBindManager.dealCardDut(trade);//处理银行卡签约代扣逻辑
			ret.setCode(ResultCode.ERROR_OF_CARD_PAY_IN_PROCESS.getCode());
			ret.setMsg(ResultCode.ERROR_OF_CARD_PAY_IN_PROCESS.getMsg());
		} else if (Constants.PAY_IN_PROCESS.equals(ret.getCode())){
			ret.setCode(ResultCode.SUCCESS.getCode());
			ret.setMsg(ResultCode.SUCCESS.getMsg());
			orderPayedData.put("order_status", "1");
			cardDutBindManager.dealCardDut(trade);//处理银行卡签约代扣逻辑
		}else if (Constants.CARD_BANLANCE_NOT_ENOUGH.equals(ret.getCode())
				|| Constants.BALANCE_NOT_ENOUGH_NEW.equals(ret.getCode())){
			ret.setCode(ResultCode.ERROR_OF_CARD_BANLANCE_NOT_ENOUGH.getCode());
			ret.setMsg(ResultCode.ERROR_OF_CARD_BANLANCE_NOT_ENOUGH.getMsg());
		}else {
			ret.setCode(ResultCode.ERROR_OF_ORDER_UNPAYED.getCode());
			ret.setMsg(ResultCode.ERROR_OF_ORDER_UNPAYED.getMsg());
		}
		ret.setData(orderPayedData);
		return ret.toJson();
	}


	private PayResult<Map<String, String>> riskProcess(PayTrade payTrade , PayUserCard card, BankCardCertification bcc, String smsKey, String smsCode){
		PayResultBuilder<Map<String, String>> builder = PayResultBuilder.create().setResultCode(ResultCode.SUCCESS);
		Long userId = Long.parseLong(payTrade.getUserId());
		JSONObject paras = new JSONObject();
		paras.put("service", "pay_by_api");
		paras.put("partner", payTrade.getPartnerId());
		paras.put("subject", payTrade.getSubject());
		paras.put("mobile", payTrade.getMobile());
		paras.put("fee", payTrade.getFee());
		paras.put("fee_unit", payTrade.getFeeUnit());
		paras.put("pay_type", card.getPayType());
		paras.put("cip", payTrade.getIp());
		paras.put("partner_order_no", payTrade.getPartnerOrderNo());
		paras.put("show_url", payTrade.getShowUrl());
		paras.put("card_id", card.getId());
		paras.put("card_num", bcc.getCardNumber());
		paras.put("cert_num", bcc.getOwnerIdNum());
		paras.put("card_mobile", bcc.getBindPhone());
		paras.put("cip", RequestUtils.getRemoteAddr(request));
		paras.put("qiyi_id", request.getParameter("qiyi_id"));
		paras.put("Dfp", request.getParameter("Dfp"));
		paras.put("uid", userId);
		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
		form.set("biz_code", "payment");
		form.set("biz_name", "card_payment");
		form.set("params", paras.toJSONString());
		try {
			logger.info("send risk: {}", form.toString());
			String res = restTemplate.postForObject(riskUrl, form, String.class);
			logger.info("return risk: {}", res);
			RiskAssessment ra = RiskAssessmentHandler.handle(res);
			if (ra.getLevel() <= 0){
				return builder.build();
			}
			if (ra.getLevel() == 1 && StringUtils.isNotBlank(smsCode) && StringUtils.isNotBlank(smsKey)){
				try {
					smss.validSmsCode(userId, smsKey, smsCode);
					return builder.build();
				} catch (SmsCodeErrorException e) {
					return  builder.setResultCode(ResultCode.ERROR_OF_SMSCODE_WRONG).build();
				} catch (Exception e) {
					return builder.setResultCode(ResultCode.ERROR_OF_SYSTEM).build();
				}
			}
			if (ra.getLevel() == 1 && StringUtils.isBlank(smsCode)){
				UserInfo userInfo = passPortUserInfoService.getUserInfoByUserId(String.valueOf(userId));
				String mobile = userInfo.getPhone();
				String newSmsKey = smss.sendSMS(userId, mobile);
				Map<String, String> retMap = Maps.newHashMap();
				retMap.put("sms_key", newSmsKey);
				retMap.put("sms_code_length", "6");
				retMap.put("sms_template", "爱奇艺支付验证码{}");
				retMap.put("mobile", mobile);
				return builder.setResultCode(ResultCode.RISK_SMS_MSG).setData(retMap).build();
			}
			if (payTrade.getFee() > cardPayRiskLimit){
				return builder.setResultCode(ResultCode.RISK).build();
			}
			//// riskLevel ==2 || riskLevel ==3
			return builder.setResultCode(ResultCode.RISK).build();
		}catch (Exception e){
			logger.error("[risk][error:]", e);
			return builder.build();
		}
	}

}
