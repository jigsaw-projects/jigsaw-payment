/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.service.bank;

import com.iqiyi.pay.client.AccountService;
import com.iqiyi.pay.client.ConfigService;
import com.iqiyi.pay.client.RouteService;
import com.iqiyi.pay.sdk.BankCardCertification;
import com.iqiyi.pay.sdk.CardBankMap;
import com.iqiyi.pay.sdk.PayUserCard;
import com.iqiyi.pay.sdk.service.AccountService.QueryBankCardCertificationRequest;
import com.iqiyi.pay.sdk.service.AccountService.QueryBankCardCertificationResponse;
import com.iqiyi.pay.sdk.service.ConfigService.QueryBankInfoByCardNumRequest;
import com.iqiyi.pay.sdk.service.ConfigService.QueryBankInfoByCardNumResponse;
import com.iqiyi.pay.sdk.service.RouteService.*;
import com.iqiyi.pay.sdk.service.RouteService.QueryPayCardRequest;
import com.iqiyi.pay.sdk.service.RouteService.QueryPayCardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年8月30日
 */
@Service
public class BankCardInfoService {

	@Autowired
	private ConfigService configService;
	
	@Autowired
	private RouteService routeService;
	
	@Autowired
	private AccountService accountService;

	
	public BankCardCertification getBankCard(long cardId) {
		QueryBankCardCertificationRequest request = QueryBankCardCertificationRequest.newBuilder().setCardId(cardId).build();
		QueryBankCardCertificationResponse response = accountService.queryBankCardCertification(request);
		if (response.hasCertification()){
			return response.getCertification();
		}
		return BankCardCertification.getDefaultInstance();
	}
	
	public CardBankMap queryBankInfo(String cardNo) {
		QueryBankInfoByCardNumRequest request = QueryBankInfoByCardNumRequest
				.newBuilder().setCardNum(cardNo).build();
		QueryBankInfoByCardNumResponse res = configService.queryBankInfoByCardNum(request);
		if (res.hasCardBankMap()) {
			return res.getCardBankMap();
		}
		return null;
	}
	
	public PayUserCard getPayUserCard(long cardId) {
		QueryPayCardRequest request = QueryPayCardRequest.newBuilder().setCardId(cardId).build();
		QueryPayCardResponse response = routeService.queryPayCard(request);
		if (response.hasPayUserCard()){
			return response.getPayUserCard();
		}
		return null;
	}

	public BankCardCertification queryBankCertifycation(String uid, String cardNum){
		com.iqiyi.pay.sdk.service.AccountService.QueryBankCardCertificationByUserIdAndCardNumRequest request = com.iqiyi.pay.sdk.service.AccountService.QueryBankCardCertificationByUserIdAndCardNumRequest.newBuilder()
				.setCardNumber(cardNum)
				.setUserId(Long.parseLong(uid))
				.build();
		com.iqiyi.pay.sdk.service.AccountService.QueryBankCardCertificationByUserIdAndCardNumResponse response = accountService.queryBankCardCertificationByUserIdAndCardNum(request);
		if (response.hasCertification()){
			return response.getCertification();
		}
		return null;
	}


	public BankCardCertification queryBankCertificationByCardId(long cardId){
		QueryBankCardCertificationRequest request = QueryBankCardCertificationRequest.newBuilder().setCardId(cardId).build();
		QueryBankCardCertificationResponse response = accountService.queryBankCardCertification(request);
		if (response.hasCertification()){
			return response.getCertification();
		}
		return null;
	}


	public String queryCardTokenByCardNum(String cardNum, String payType){
		QueryTokenByCardNumAndPayTypeRequest request = QueryTokenByCardNumAndPayTypeRequest.newBuilder()
				.setCardNum(cardNum).setPayType(payType).build();
		QueryTokenByCardNumAndPayTypeResponse response = routeService.queryTokenByCardNumAndPayType(request);
		if (response.hasToken()){
			return response.getToken();
		}
		return null;
	}
	
}
