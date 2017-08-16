package com.iqiyi.pay.frontend.param;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by liuyanlong on 7/4/17.
 */
@Component
@Configuration
@ConfigurationProperties(prefix = "BANK",locations = "classpath:addition-protocol.properties")
public class BankAdditionProtocol {

    public Map<String,String> CODE = new HashedMap();

    public Map<String, String> getCODE() {
        return CODE;
    }

    public void setCODE(Map<String, String> CODE) {
        this.CODE = CODE;
    }
}
