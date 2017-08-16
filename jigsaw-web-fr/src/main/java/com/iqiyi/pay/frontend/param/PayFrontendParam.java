/**
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 **/

package com.iqiyi.pay.frontend.param;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * request param entity
 *
 * @author Li Hengjun<lihengjun@qiyi.com>
 * @version 1.0.0  7/4/16
 **/
public class PayFrontendParam extends BaseParam {

    static Map<String, String> serviceMap = new HashMap<String, String>();

    static {
        serviceMap.put("pay_by_api","1");
        serviceMap.put("pay_by_ui","2");
        serviceMap.put("pay_by_cashier","3");
    }

    @NotBlank(message = "partner not null")
    private String partner;

    @NotNull(message = "uid not null")
    private Long uid;

    @NotBlank(message = "partner_order_no not null")
    private String partner_order_no;

    @NotBlank(message = "subject not null")
    private String subject;

    @NotNull(message = "fee not null")
    private Long fee;

    @NotBlank(message = "notify_url not null")
    private String notify_url;

    @NotBlank(message = "version not null")
    private String version;

    @NotBlank(message = "sign_type not null")
    private String sign_type;

    @NotBlank(message = "sign not null")
    private String sign;

    @NotNull(message = "fee_unit not null")
    private Long fee_unit;

    @NotBlank(message = "pay_type not null")
    private String pay_type;

    private String extra_common_param;

    private String extend_params;

    private String description;

    private String mobile;

    private String return_url;

    private String expire_time;

    private String return_request;

    private String user_account;

    private String cip;

    private String client_code;

    private String service;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getPartner_order_no() {
        return partner_order_no;
    }

    public void setPartner_order_no(String partner_order_no) {
        this.partner_order_no = partner_order_no;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getFee() {
        return fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getExtra_common_param() {
        return extra_common_param;
    }

    public void setExtra_common_param(String extra_common_param) {
        this.extra_common_param = extra_common_param;
    }

    public Long getFee_unit() {
        return fee_unit;
    }

    public void setFee_unit(Long fee_unit) {
        this.fee_unit = fee_unit;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(String expire_time) {
        this.expire_time = expire_time;
    }

    public String getReturn_request() {
        return return_request;
    }

    public void setReturn_request(String return_request) {
        this.return_request = return_request;
    }

    public String getUser_account() {
        return user_account;
    }

    public void setUser_account(String user_account) {
        this.user_account = user_account;
    }

    public String getCip() {
        return cip;
    }

    public void setCip(String cip) {
        this.cip = cip;
    }

    public String getClient_code() {
        return client_code;
    }

    public void setClient_code(String client_code) {
        this.client_code = client_code;
    }

    public String getExtend_params() {
        return extend_params;
    }

    public void setExtend_params(String extend_params) {
        this.extend_params = extend_params;
    }

    public static String getGatewayType(String service){
        if (service == null){
            return null;
        }
        String gateWayType = serviceMap.get(service);
        return gateWayType;
    }
}


