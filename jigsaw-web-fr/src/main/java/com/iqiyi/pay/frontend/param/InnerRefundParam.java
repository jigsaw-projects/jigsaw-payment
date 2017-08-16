package com.iqiyi.pay.frontend.param;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by liuyanlong on 3/20/17.
 */
public class InnerRefundParam extends BaseParam{
    @NotBlank(message = "order_code not null")
    private String order_code;
    @NotBlank(message = "partner not null")
    private String partner;
    @NotNull(message = "fee not null")
    @Min(value = 0)
    private Long fee;
    @NotBlank(message = "partner_refund_no not null")
    private String partner_refund_no;
    @NotBlank(message = "sign not null")
    private String sign;

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public Long getFee() {
        return fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public String getPartner_refund_no() {
        return partner_refund_no;
    }

    public void setPartner_refund_no(String partner_refund_no) {
        this.partner_refund_no = partner_refund_no;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
