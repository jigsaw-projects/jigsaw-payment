package com.iqiyi.pay.frontend.param;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by leishengbao on 8/30/16.
 */
public class SendSmsCodeParam extends BaseParam{

    @NotBlank(message = "cache_key not null")
    private String cache_key;

    private String order_code;

    @NotBlank(message = "authcookie not null")
    private String authcookie;
    @NotBlank(message = "platform not null")
    private String platform;
    @NotBlank(message = "sign not null")
    private String sign;

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

    public String getAuthcookie() {
        return authcookie;
    }

    public void setAuthcookie(String authcookie) {
        this.authcookie = authcookie;
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
}
