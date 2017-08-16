package com.iqiyi.pay.frontend.web;

import com.iqiyi.pay.frontend.service.cache.BankInfoCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by leishengbao on 9/6/16.
 */
@RestController
@RequestMapping("/test/")
public class CacheTestController {

    Logger LOGGER = LoggerFactory.getLogger(CacheTestController.class);

    @Autowired
    BankInfoCacheManager bankInfoCacheManager;


    @RequestMapping("get")
    public Map<String, String> cache(@RequestParam String key){
        return bankInfoCacheManager.getBankTransInfo(key);
    }



    @RequestMapping("notify")
    public String setIdCert(@RequestParam String order_code){




        return "SUCCESS";
    }
}
