package com.iqiyi.pay.frontend.utils;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

/**
 * Created by leishengbao on 8/15/16.
 */
public class MapWapper<K, V> extends HashMap<K, V> {

    public V put(K k, V v){
        if (v == null){
            return null;
        }
        if (!(v instanceof String)){
            return super.put(k, v);
        }
        String value = (String) v;
        if (StringUtils.isNotBlank(value) && !"null".equals(value)){
            return super.put(k, v);
        }
        return null;
    }
}
