package com.iqiyi.pay.frontend.param;

/**
 * Created by leishengbao on 8/19/16.
 */
public class QueryContractParam extends BaseParam{


    private String uid;

    private String card_id;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCard_id() {
        return card_id;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }
}
