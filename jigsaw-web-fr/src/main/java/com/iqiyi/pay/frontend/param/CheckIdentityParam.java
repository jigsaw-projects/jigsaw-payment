package com.iqiyi.pay.frontend.param;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by leishengbao on 8/8/16.
 */
public class CheckIdentityParam extends BaseParam{

    @NotBlank(message = "order_code not null")
    private String order_code;

    @NotBlank(message = "authcookie not null")
    private String authcookie;

    @NotBlank(message = "uid not null")
    private String uid;

    @NotBlank(message = "user_name not null")
    private String user_name;

    @NotBlank(message = "card_num not null")
    @Length(min = 10, message = "card_num length valid")
    private String card_num;

    @NotBlank(message = "card_type not null")
    private String card_type;

    private String card_validity;

    private String card_cvv2;

    @NotBlank(message = "card_mobile not null")
    private String card_mobile;

    private String cert_type;

    @NotBlank(message = "cert_num not null")
    private String cert_num;

    @NotBlank(message = "platform not null")
    private String platform;

    @NotBlank(message = "sign not null")
    private String sign;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getCard_num() {
        return card_num;
    }

    public void setCard_num(String card_num) {
        this.card_num = card_num;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getCard_validity() {
        return card_validity;
    }

    public void setCard_validity(String card_validity) {
        this.card_validity = card_validity;
    }

    public String getCard_cvv2() {
        return card_cvv2;
    }

    public void setCard_cvv2(String card_cvv2) {
        this.card_cvv2 = card_cvv2;
    }

    public String getCard_mobile() {
        return card_mobile;
    }

    public void setCard_mobile(String card_mobile) {
        this.card_mobile = card_mobile;
    }


    public String getCert_num() {
        return cert_num;
    }

    public void setCert_num(String cert_num) {
        this.cert_num = cert_num;
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

    public String getOrder_code() {
        return order_code;
    }

    public String getAuthcookie() {
        return authcookie;
    }

    public void setAuthcookie(String authcookie) {
        this.authcookie = authcookie;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getCert_type() {
        return cert_type;
    }

    public void setCert_type(String cert_type) {
        this.cert_type = cert_type;
    }
}
