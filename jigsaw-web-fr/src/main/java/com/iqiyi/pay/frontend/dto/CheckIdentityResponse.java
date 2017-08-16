/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.dto;

import com.iqiyi.pay.frontend.param.MyParamToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by leishengbao on 2/17/17.
 */
public class CheckIdentityResponse {

    /**
     * channel_resp_code=UNIONPAY_00, channel_resp_msg=³É¹¦[0000000], trans_seq=91071487304091311, sms_key=14873040924442181390481796, cache_key=14873040924971390481796, order_code=2017021712001098104, fee=1
     */
    private String channel_resp_code;

    private String channel_resp_msg;

    private String trans_seq;

    private String sms_key;

    private String cache_key;

    private String order_code;

    private long fee;


    public String getChannel_resp_code() {
        return channel_resp_code;
    }

    public void setChannel_resp_code(String channel_resp_code) {
        this.channel_resp_code = channel_resp_code;
    }

    public String getChannel_resp_msg() {
        return channel_resp_msg;
    }

    public void setChannel_resp_msg(String channel_resp_msg) {
        this.channel_resp_msg = channel_resp_msg;
    }

    public String getTrans_seq() {
        return trans_seq;
    }

    public void setTrans_seq(String trans_seq) {
        this.trans_seq = trans_seq;
    }

    public String getSms_key() {
        return sms_key;
    }

    public void setSms_key(String sms_key) {
        this.sms_key = sms_key;
    }

    public String getCache_key() {
        return cache_key;
    }

    public void setCache_key(String cache_key) {
        this.cache_key = cache_key;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, new MyParamToStringStyle());
    }
}
