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
 * Created by leishengbao on 2/10/17.
 */
public class IapQueryOrderRefundParam extends BaseParam{

    @NotBlank(message = "order_code not null")
    private String order_code;

    private String type;//1、表示自动续费订单

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }
}
