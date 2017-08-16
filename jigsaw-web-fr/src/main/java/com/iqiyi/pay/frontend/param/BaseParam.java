package com.iqiyi.pay.frontend.param;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class BaseParam {


    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, new MyParamToStringStyle());
    }
}
