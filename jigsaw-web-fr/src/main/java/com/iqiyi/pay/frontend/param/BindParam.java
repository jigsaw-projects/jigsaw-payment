package com.iqiyi.pay.frontend.param;

/**
 * Created by leishengbao on 8/8/16.
 */
public class BindParam extends BaseParam{

    private String trans_time;

    private String user_id;

    private String user_name;

    private String card_num;

    private String card_type;

    private String card_validity;

    private String card_cvv2;

    private String card_mobile;


    private String cert_num;

    private String sms_key;

    private String sms_code;

    private String platform;

    public String getTrans_time() {
        return trans_time;
    }

    public void setTrans_time(String trans_time) {
        this.trans_time = trans_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getSms_key() {
        return sms_key;
    }

    public void setSms_key(String sms_key) {
        this.sms_key = sms_key;
    }

    public String getSms_code() {
        return sms_code;
    }

    public void setSms_code(String sms_code) {
        this.sms_code = sms_code;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
