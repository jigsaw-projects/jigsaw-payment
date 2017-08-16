/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.param;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by leishengbao on 3/3/17.
 */
public class CardPrepareOrderParam extends BaseParam{

    @NotBlank(message = "user_id not null")
    private String user_id;
    @NotBlank(message = "return_url not null")
    private String return_url;
    @NotBlank(message = "authcookie not null")
    private String authcookie;
    @NotBlank(message = "sign not null")
    private String sign;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAuthcookie() {
        return authcookie;
    }

    public void setAuthcookie(String authcookie) {
        this.authcookie = authcookie;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
