package com.iqiyi.pay.frontend.param;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Created by leishengbao on 7/20/16.
 */
public class SignAndPayParam extends BaseParam{

    @NotBlank(message = "order_code not null")
    private String order_code;
    @NotBlank(message = "trans_seq not null")
    private String trans_seq;
    @NotBlank(message = "cache_key not null")
    private String cache_key;
    @NotBlank(message = "authcookie not null")
    private String authcookie;

    @NotNull(message = "uid not null")
    private Long uid;


    private String sms_key;
    @NotBlank(message = "sms_code not null")
    private String sms_code;

    @NotBlank(message = "platform not null")
    private String platform;

    @NotBlank(message = "sign not null")
    private String sign;

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getAuthcookie() {
        return authcookie;
    }

    public void setAuthcookie(String authcookie) {
        this.authcookie = authcookie;
    }

    public String getSms_key() {
        return sms_key;
    }

    public void setSms_key(String sms_key) {
        this.sms_key = sms_key;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getTrans_seq() {
        return trans_seq;
    }

    public void setTrans_seq(String trans_seq) {
        this.trans_seq = trans_seq;
    }

    public String getCache_key() {
        return cache_key;
    }

    public void setCache_key(String cache_key) {
        this.cache_key = cache_key;
    }

    public String getSms_code() {
        return sms_code;
    }

    public void setSms_code(String sms_code) {
        this.sms_code = sms_code;
    }
}
