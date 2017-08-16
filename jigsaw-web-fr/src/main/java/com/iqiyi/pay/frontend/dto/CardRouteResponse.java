/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.dto;

/**
 * Created by leishengbao on 2/17/17.
 */
public class CardRouteResponse {

    private String uid;

    private String is_unionpay;

    private String bank_code;

    private String bank_name;

    private String card_type;

    private String card_type_string;

    private String order_code;

    private String id_card;

    private String user_name;

    private String bank_protocol_url;

    private String addition_protocol_name;

    private String addition_protocol_url;

    private String bank_protocol_name;

    private String is_wallet_pwd_set;

    private boolean has_off = false;

    private String subject;

    private long fee;

    private long off_price;

    private boolean needCvv;//信用卡绑卡根据不同的银行提示是否需要填写cvv
    private boolean needExpireTime;//信用卡绑卡根据不同的银行提示是否需要填写有效期

    private String card_num_last;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIs_unionpay() {
        return is_unionpay;
    }

    public void setIs_unionpay(String is_unionpay) {
        this.is_unionpay = is_unionpay;
    }

    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getCard_type_string() {
        return card_type_string;
    }

    public void setCard_type_string(String card_type_string) {
        this.card_type_string = card_type_string;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getBank_protocol_url() {
        return bank_protocol_url;
    }

    public void setBank_protocol_url(String bank_protocol_url) {
        this.bank_protocol_url = bank_protocol_url;
    }

    public String getIs_wallet_pwd_set() {
        return is_wallet_pwd_set;
    }

    public void setIs_wallet_pwd_set(String is_wallet_pwd_set) {
        this.is_wallet_pwd_set = is_wallet_pwd_set;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public long getOff_price() {
        return off_price;
    }

    public void setOff_price(long off_price) {
        this.off_price = off_price;
    }

    public boolean isHas_off() {
        return has_off;
    }

    public void setHas_off(boolean has_off) {
        this.has_off = has_off;
    }

    public boolean isNeedCvv() {
        return needCvv;
    }

    public void setNeedCvv(boolean needCvv) {
        this.needCvv = needCvv;
    }

    public boolean isNeedExpireTime() {
        return needExpireTime;
    }

    public void setNeedExpireTime(boolean needExpireTime) {
        this.needExpireTime = needExpireTime;
    }

    public String getCard_num_last() {
        return card_num_last;
    }

    public void setCard_num_last(String card_num_last) {
        this.card_num_last = card_num_last;
    }

    public String getBank_protocol_name() {
        return bank_protocol_name;
    }

    public void setBank_protocol_name(String bank_protocol_name) {
        this.bank_protocol_name = bank_protocol_name;
    }

    public void setAddition_protocol_url(String addition_protocol_url) {
        this.addition_protocol_url = addition_protocol_url;
    }

    public String getAddition_protocol_url() {
        return addition_protocol_url;
    }

    public void setAddition_protocol_name(String addition_protocol_name) {
        this.addition_protocol_name = addition_protocol_name;
    }

    public String getAddition_protocol_name() {
        return addition_protocol_name;
    }
}
