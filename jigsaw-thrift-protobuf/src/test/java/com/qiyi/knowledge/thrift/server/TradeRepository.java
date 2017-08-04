/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.qiyi.knowledge.thrift.server;


import com.iqiyi.pay.sdk.Trade;
import com.qiyi.knowledge.thrift.server.BaseProtobufRepository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/**
 * mysql实时库
 *
 * @author zhangchong@qiyi.com
 *
 */
@Repository
public class TradeRepository extends BaseProtobufRepository<String,Trade> {

	public TradeRepository() {
		super(Trade.class, "pay_trade_0");
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		namedParameterJdbcTemplate=new NamedParameterJdbcTemplate(dataSource);
	}

	public long create(Trade trade) {
		return insertMessage(trade);
	}

	public long createOrUpdate(Trade trade) {
		return insertOrUpdateMessage(trade);
	}

	public int update(Trade trade){
        //possible?
        if(StringUtils.isNotBlank(trade.getExtraParams())&&trade.getExtraParams().length()>=1024)
            trade=trade.toBuilder().setExtraParams("").build();
		return updateMessageByCondition(trade,new String[]{"sub_id","code"},new String[]{String.valueOf(trade.getSubId()),trade.getCode()});
	}

    public Trade queryByCode(String code) {
        return queryObjectByCondition(new String[]{"code"},new String[]{code});
    }

    public Trade queryByCode(String code,String uid) {
        return queryObjectByCondition(new String[]{"sub_id","code"},new String[]{uid,code});
    }

    public Trade queryByPartnerOrderNo(int partnerId,String partnerOrderNo) {
        return queryObjectByCondition(new String[]{"partner_id","partner_order_no"},new Object[]{Integer.valueOf(partnerId),partnerOrderNo});
    }

}
