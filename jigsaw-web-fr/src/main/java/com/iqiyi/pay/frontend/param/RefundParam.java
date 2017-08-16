package com.iqiyi.pay.frontend.param;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by leishengbao on 9/2/16.
 */
public class RefundParam extends BaseParam{

    @NotBlank(message = "order_code not null")
    private String order_code;
    @NotBlank(message = "partner_order_no not null")
    private String partner_order_no;
    @NotBlank(message = "partner_refund_no not null")
    private String partner_refund_no;
    @NotBlank(message = "partner not null")
    private String partner;
    @NotNull(message = "fee not null")
    @Min(value = 0)
    private Long fee;
    @NotBlank(message = "notify_url not null")
    private String notify_url;

    private String extend_params;

    private String extra_common_param;

    private String charset;
    @NotBlank(message = "sign not null")
    private String sign;

    private String reason;

    private String version;

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getPartner_order_no() {
        return partner_order_no;
    }

    public void setPartner_order_no(String partner_order_no) {
        this.partner_order_no = partner_order_no;
    }

    public String getPartner_refund_no() {
        return partner_refund_no;
    }

    public void setPartner_refund_no(String partner_refund_no) {
        this.partner_refund_no = partner_refund_no;
    }

    public Long getFee() {
        return fee;
    }

    public void setFee(Long fee) {
        this.fee = fee;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getExtend_params() {
        return extend_params;
    }

    public void setExtend_params(String extend_params) {
        this.extend_params = extend_params;
    }

    public String getExtra_common_param() {
        return extra_common_param;
    }

    public void setExtra_common_param(String extra_common_param) {
        this.extra_common_param = extra_common_param;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }
}
