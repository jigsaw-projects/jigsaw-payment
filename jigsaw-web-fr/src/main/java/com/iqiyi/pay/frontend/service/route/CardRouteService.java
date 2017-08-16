/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.service.route;

import com.iqiyi.pay.client.RouteService;
import com.iqiyi.pay.sdk.ChannelAccount;
import com.iqiyi.pay.sdk.service.AccountService;
import com.iqiyi.pay.sdk.service.RouteService.QueryBankRouteRequest;
import com.iqiyi.pay.sdk.service.RouteService.QueryBankRouteResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年8月30日
 */
@Service
public class CardRouteService {

	@Autowired
	private RouteService routeService;
	@Autowired
	private com.iqiyi.pay.client.AccountService accountService;

	public boolean isChinaUnion(String bankCode, int  cardType, String platform) {
//		ChannelAccount channelAccount = queryBankRoute(bankCode, cardType, platform);
//		String channelCode = channelAccount.getChannelCode();
		return false;
	}
	
	public ChannelAccount queryBankRoute(String bankCode, int cardType, String platformCode) {
		QueryBankRouteRequest request = QueryBankRouteRequest.newBuilder()
				.setBankCode(bankCode).setPlatformCode(platformCode).setCardType(cardType).build();
		QueryBankRouteResponse response = routeService.queryBankRoute(request);
		if (response.hasChannelAccount()){
			return response.getChannelAccount();
		}
		return null;
	}


	public ChannelAccount queryBankRouteByRate(String bankCode, int cardType, String platformCode, String orderCode){
		if (StringUtils.isBlank(orderCode)){
			return queryBankRoute(bankCode, cardType, platformCode);
		}
		char[] cs = orderCode.toCharArray();
		char rate = '0';
		//2016122318233914802
		if (cs.length > 1){
			rate = cs[16];
		}
		QueryBankRouteRequest request = QueryBankRouteRequest.newBuilder()
				.setBankCode(bankCode).setPlatformCode(platformCode).setCardType(cardType)
				.setRate(String.valueOf(rate))
				.build();
		QueryBankRouteResponse response = routeService.queryBankRoute(request);
		if (response.hasChannelAccount()){
			return response.getChannelAccount();
		}
		return null;
	}

	public ChannelAccount queryChannelAccount(String channelCode){
		AccountService.QueryChannelAccountByCodeRequest request = AccountService.QueryChannelAccountByCodeRequest.newBuilder()
				.setChannelCode(channelCode).build();
		AccountService.QueryChannelAccountByCodeResponse response = accountService.queryChannelAccountByCode(request);
		if (response.hasAccount()){
			return response.getAccount();
		}
		return null;
	}

}
