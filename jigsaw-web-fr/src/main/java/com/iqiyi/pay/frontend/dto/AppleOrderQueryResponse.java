/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.dto;

import com.iqiyi.pay.frontend.param.IapOrderParam;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by leishengbao on 1/16/17.
 */
public class AppleOrderQueryResponse {

    public static final Logger logger = LoggerFactory.getLogger(AppleOrderQueryResponse.class);

    private String code;

    private String msg;

    private String quantity;

    private String product_id;

    private String transaction_id;

    private String original_transaction_id;

    private String purchase_date;

    private String is_trial_period;

    private String cancellation_date;

    private String expires_date;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getOriginal_transaction_id() {
        return original_transaction_id;
    }

    public void setOriginal_transaction_id(String original_transaction_id) {
        this.original_transaction_id = original_transaction_id;
    }

    public String getPurchase_date() {
        return purchase_date;
    }

    public void setPurchase_date(String purchase_date) {
        this.purchase_date = purchase_date;
    }

    public String getIs_trial_period() {
        return is_trial_period;
    }

    public void setIs_trial_period(String is_trial_period) {
        this.is_trial_period = is_trial_period;
    }

    public String getCancellation_date() {
        return cancellation_date;
    }

    public void setCancellation_date(String cancellation_date) {
        this.cancellation_date = cancellation_date;
    }

    public String getExpires_date() {
        return expires_date;
    }

    public void setExpires_date(String expires_date) {
        this.expires_date = expires_date;
    }

    public boolean validResponse(){
        return transaction_id !=null && product_id != null && original_transaction_id != null && purchase_date != null;
    }

    public boolean isAutoNewed(IapOrderParam param, int afterDays, int afterTypeBeforeDays, int beforeTypeBeforeDays){
        Date beforeDate = null;
        Date afterDate = DateUtils.truncate(DateUtils.addDays(new Date(), afterDays), Calendar.DATE);
//        if (param.getType().equals(IapOrderParam.AFTER_TYPE)){
            beforeDate = DateUtils.truncate(DateUtils.addDays(new Date(), -afterTypeBeforeDays), Calendar.DATE);
//        }
//        if (param.getType().equals(IapOrderParam.BEFORE_TYPE)){
//            beforeDate = DateUtils.truncate(DateUtils.addDays(new Date(), -beforeTypeBeforeDays), Calendar.DATE);
//        }
        logger.info("[beforDate:{}][afterDate:{}]", beforeDate, afterDate);
        try {
            Date p_date = DateUtils.parseDate(purchase_date, "yyyy-MM-dd HH:mm:ss");
            return p_date.compareTo(beforeDate) > 0 && p_date.compareTo(afterDate) < 0;
        } catch (ParseException e) {
            logger.error("[param:{}]", param, e);
        }
        return false;
    }


    public boolean isRefunded(){
        return StringUtils.isNotBlank(cancellation_date);
    }


    public String getExpireTime(){
        try {
            Date date = DateUtils.addMonths(DateUtils.parseDate(purchase_date, "yyyy-MM-dd HH:mm:ss"),1);
            return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            logger.error("[e:{}]", e);
        }
        return null;
    }
}
