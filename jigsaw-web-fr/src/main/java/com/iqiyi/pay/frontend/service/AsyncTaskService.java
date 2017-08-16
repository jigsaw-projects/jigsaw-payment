package com.iqiyi.pay.frontend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.SuccessCallback;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by leishengbao on 9/6/16.
 */
@Component
public class AsyncTaskService {


    Logger LOGGER = LoggerFactory.getLogger(AsyncTaskService.class);

    @Async
    public void test1(){
        test2();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("[test][time:{}]", new Date());
    }


    @Async
    public void test2(){
        Future<String> future = test2("lisi");
        String name = null;
        try {
            name = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        LOGGER.info("[test][time:{}][name:{}]", new Date(), name);
    }


    @Async
    public Future<String> test2(String name){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("[test][time:{}][name:{}]", new Date(), name);
        AsyncResult<String> result =  new AsyncResult<>(name);
        result.addCallback(new SuccessCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LOGGER.info("[test][time:{}][name:{}]", new Date(), name);
            }
        }, null);
        return result;
    }
}
