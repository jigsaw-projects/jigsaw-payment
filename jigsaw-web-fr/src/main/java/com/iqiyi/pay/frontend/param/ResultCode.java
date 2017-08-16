/**
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 **/

package com.iqiyi.pay.frontend.param;

import com.iqiyi.pay.common.utils.result.IResult;

/**
 * error code
 *
 * @author Li Hengjun<lihengjun@qiyi.com>
 * @version 1.0.0  7/4/16
 **/
public enum ResultCode implements IResult{

    SUCCESS("A00000","成功"),
    ERROR_OF_PARAM_INVALID("ERR00000", "param is invalid"),
    ERROR_OF_PAYTYPE_NOT_SUPPORTED("ERR00001", "paytype not supported"),
    ERROR_OF_SIGN_ERROR("ERR00002", "sign is error"),
    ERROR_OF_SYSTEM("ERR00003", "系统异常"),

    ERROR_OF_ACCESSER_NOT_EXIST("ACE00001", "accesser not exist"),
    ERROR_OF_ACCESSER_PAYTYPE_NOT_SUPPORT("ACE00002", "accesser pay_type not supported"),
    ERROR_OF_PAYTYPE_NOT_EXIST("ACE00003", "pay_type not exist"),
    ERROR_OF_ROUTE_NOT_EXIST("ACE00004", "该银行卡暂不支持此功能"),
    ERROR_OF_MOBILE_NOT_BIND("ACE00004", "mobile not bind"),

    ERROR_OF_CARD_CREDIT("CAR00001", "信用卡有效期和验证码不能为空"),
    ERROR_OF_CARD_NOT_EXIT("CAR00002", "卡不存在"),
    ERROR_OF_CARD_INFO_VALID("CAR00003", "信息验证失败，请核对后重试"),
    ERROR_OF_CARD_VALID("CAR00004", "卡号不正确，请核对后重试"),
    ERROR_OF_CARD_HAS_BIND("CAR00005", "您已绑定过该卡"),
    ERROR_OF_CARD_HAS_BIND_PWD_NOT_SET("CAR00006", "该卡已绑定，请到爱奇艺钱包首页补全支付密码后重试"),
    ERROR_OF_CARD_BANLANCE_NOT_ENOUGH("CAR00006", "银行卡余额不足"),
    ERROR_OF_CARD_PAY_IN_PROCESS("CAR00007", "银行卡交易处理中"),
    ERROR_OF_CARD_CVV2_INVALID("CAR00008", "安全码输入错误"),
    ERROR_OF_CARD_CERT_INVALID("CAR00009", "身份证号码不正确"),
    ERROR_OF_CARD_MOBILE_INVALID("CAR00010", "手机号输入错误"),
    ERROR_OF_CARD_EXPIRE_TIME("CAR00011", "过期时间不能为空"),

    ERROR_OF_ORDER_PAYED("ORD000001", "订单已支付"),
    ERROR_OF_ORDER_UNPAYED("ORD000002", "支付失败"),
    ERROR_OF_ORDER_NOT_EXIT("ORD000003", "order not exit"),
    ERROR_OF_ORDER_CLOSE("ORD000004", "订单不存在或者已支付或者已关闭"),
    ERROR_OF_CHANNEL_CLOSE_ORDER_FAIL("ORD000004", "调用渠道关闭订单失败"),
    ERROR_OF_ORDER_IS_UNPAYED("ORD000005", "订单未支付"),
    ERROR_OF_ORDER_STATUS("ORD000005", "订单状态非法"),
    ERROR_OF_PAY_SERVICE_NOT_EXIT("ORD000006", "无法通过订单获取支付服务"),
    ERROR_OF_PAY_SERVICE_NOT_SUPPORT("ORD000007", "暂不支持该支付方式的第三方查单"),
    ERROR_OF_CHANNEL_RETURN_NULL("ORD000008", "渠道返回空"),
    ERROR_OF_CHANNEL_QUERY_SUCCESS_UPDATE_FAILED("ORD000009", "渠道返回订单已支付，更新订单失败，请重试"),


    ERROR_OF_BANK_SIGN_ERROR("CARD000003", "银行卡签约失败"),

    ERROR_OF_PASSWORD_WRONG("ERR00004", "支付密码错误"),
    ERROR_OF_PASSWORD_FROZEN("ERR00011", "支付密码已被冻结"),




    ERROR_OF_PASSWORD_NOT_EXIST("PWD00012", ""),

    ERROR_OF_CHANNEL_FAILED("CHAN00001", "channel error"), 
    
    ERROR_OF_SMSCODE_WRONG("SMS00002", "短信验证码错误"),

    ERROR_OF_SMS_INFO_EXPIRE("SMS00001", "验证信息已失效"),

    RISK("RISK00002", "交易存在风险，终止交易"),

    RISK_SMS_MSG("RISK00001", "由于风控原因需要验证您的短信验证码"),

    ERROR_OF_NO_CREDIT("CARD000003", "不支持信用卡支付"),

    ERROR_OF_FAIL("fail", "FAIL"),



    ERROR_OF_FEE("RFD00001", "退款金额不正确"),
    ERROR_OF_REFUND_EXIST("RFD00002", "退款订单已存在"),
    ERROR_OF_REFUND_FEE_OVERFLOW("RFD00002", "退款金额超限"),
    ERROR_OF_REFUND_FAIL("RFD00003", "退款失败"),
    ERROR_OF_REFUND_IN_PROCCESS("REF00006", "退款结果处理中"),
    ;

    private final String code;
    private final String msg;

    ResultCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}