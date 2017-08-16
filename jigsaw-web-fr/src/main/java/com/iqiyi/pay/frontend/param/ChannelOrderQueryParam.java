package com.iqiyi.pay.frontend.param;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by liuyanlong on 3/17/17.
 */
public class ChannelOrderQueryParam extends BaseParam {

    @NotBlank(message = "order_code not null")
    private String order_code;

    @NotBlank(message = "sign not null")
    private String sign;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }
}
