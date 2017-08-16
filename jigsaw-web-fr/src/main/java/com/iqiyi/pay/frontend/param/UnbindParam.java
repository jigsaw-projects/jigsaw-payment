package com.iqiyi.pay.frontend.param;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * Created by leishengbao on 8/11/16.
 */
public class UnbindParam extends BaseParam{

    @NotNull(message = "uid not null")
    private Long uid;
    @NotNull(message = "card_id not null")
    private Long card_id;
    @NotBlank(message = "platform not null")
    private String platform;

    private String card_num;
    @NotBlank(message = "authcookie not null")
    private String authcookie;
    @NotBlank(message = "sign not null")
    private String sign;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getCard_id() {
        return card_id;
    }

    public void setCard_id(Long card_id) {
        this.card_id = card_id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCard_num() {
        return card_num;
    }

    public void setCard_num(String card_num) {
        this.card_num = card_num;
    }

    public String getAuthcookie() {
        return authcookie;
    }

    public void setAuthcookie(String authcookie) {
        this.authcookie = authcookie;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
