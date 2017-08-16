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
 * Created by leishengbao on 1/12/17.
 */
public class IapOrderParam extends BaseParam{

    @NotBlank(message = "order_code is not null")
    private String order_code;
    @NotBlank(message = "sign_code is not null")
    private String sign_code;
    @NotBlank(message = "user_id is not null")
    private String user_id;
    @NotBlank(message = "type is not null")
    private String type;
    @NotBlank(message = "dut_type not null")
    private String dut_type;

    public static final String BEFORE_TYPE = "1";

    public static final String AFTER_TYPE = "2";

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }

    public String getSign_code() {
        return sign_code;
    }

    public void setSign_code(String sign_code) {
        this.sign_code = sign_code;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDut_type() {
        return dut_type;
    }

    public void setDut_type(String dut_type) {
        this.dut_type = dut_type;
    }

    public boolean needToDeDut(){
        return "2".equals(type);
    }
}
