package com.iqiyi.pay.frontend.utils;

/**
 * Created by leishengbao on 8/31/16.
 */
public class Constants {

    public final static String WSC_SUBJECT = "wallet_sign_consume";


    public final static String BAIDU_API_CODE = "baidubank";


    //银行卡重复签约
    public static final String BANK_SIGNED_CODE = "CARD00011";

    //银行卡余额不足
    public static final String CARD_BANLANCE_NOT_ENOUGH = "CARD00014";

    public static final String BALANCE_NOT_ENOUGH_NEW = "20008";

    //短信验证码错误
    public static final String CARD_SMS_DOCE_ERROR = "CARD00009";


    public static final String SMS_ERROR_NEW = "20014";



    public static final String CARD_PAY_IN_PROCESS = "CARD00032";

    public static final String CHANNEL_REFUND_SUCCESS = "10000";
    public static final String CHANNEL_REFUND_IN_PROCESS = "20009";

    /**
     * 百度银行API返回的code，表示交易处理中，网关不更新订单，只返回给前端成功的消息
     */
    public static final String PAY_IN_PROCESS = "A10000";

    public static final String CARD_REFUND_IN_PROCESS = "REF00006";

    public static final String CARD_UNBIND_NOT_SUPPORT = "CARD00024";
    //签约标志
    public static final String CONTRACT_TAG = "1";
    //签约且支付标注
    public static final String CONTRACT_AND_PAY_TAG = "0";



    public static final String CARD_AUTONEW_EXTEND_PARAMS = "autonew=yes";


    public static final String CARD_ID = "cardId=";


    //身份证
    public static final String ID_CARD_CERT_TYPE = "1";


    //信用卡
    public static final String CREDIT_CARD_TYPE = "2";
}
