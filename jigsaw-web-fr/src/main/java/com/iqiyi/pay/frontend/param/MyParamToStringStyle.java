package com.iqiyi.pay.frontend.param;

import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Arrays;
import java.util.List;

/**
 * Created by leishengbao on 8/1/16.
 */
public class MyParamToStringStyle extends ToStringStyle{

    public static final List<String> HIDENFIELDS = Arrays.asList( "cert_num", "card_");

    /**
     * <p>Constructor.</p>
     * <p>
     * <p>Use the static constant rather than instantiating.</p>
     */
    public MyParamToStringStyle() {
        super();
        this.setUseShortClassName(true);
        this.setUseIdentityHashCode(false);
        this.setContentStart("{");
        this.setContentEnd("}");
    }

    @Override
    protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
        if (value != null){
            super.appendDetail(buffer, fieldName, value);
        }
    }

    @Override
    public void append(final StringBuffer buffer, final String fieldName, final Object value, final Boolean fullDetail) {
        if (value == null){
            return;
        }
        String hideValue = String.valueOf(value);
        for (String hide : HIDENFIELDS){
            if (fieldName.contains(hide) && !"card_id".equals(fieldName)){
                hideValue = "******";
            }
        }
        super.append(buffer, fieldName, hideValue, fullDetail);
    }
}
