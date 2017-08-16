package com.iqiyi.pay.frontend.web;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.iqiyi.kiwi.utils.ReflectionUtils;
import com.iqiyi.pay.common.security.PayUtils;
import com.iqiyi.pay.common.utils.RequestUtils;
import com.iqiyi.pay.common.utils.result.PayResult;
import com.iqiyi.pay.common.utils.result.PayResultBuilder;
import com.iqiyi.pay.frontend.param.BaseParam;
import com.iqiyi.pay.frontend.param.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by leishengbao on 5/25/16.
 */
public abstract class BaseController<T extends BaseParam>{

    protected Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected Environment environment;
    @Autowired
    String profileValue;

    protected PayResult commonInvoke(T param, BindingResult result){
        Stopwatch stopwatch = Stopwatch.createStarted();

        LOGGER.debug("[step:enter][params:{}]",param);
        PayResultBuilder builder = PayResultBuilder.create().setResultCode(ResultCode.SUCCESS);

        if (!result.hasErrors() && !checkSign(param)){
            builder.setResultCode(ResultCode.ERROR_OF_SIGN_ERROR);
        }else if (!result.hasErrors()){
            generateResultData(builder, param);
        }
        result.getAllErrors().forEach(objectError -> builder.setResultCode(ResultCode.ERROR_OF_PARAM_INVALID).setMsg(objectError.getDefaultMessage()));
        PayResult payResult = builder.build();
        LOGGER.debug("[step:exit][params:{}][time:{}][result:{}]", param, stopwatch.elapsed(TimeUnit.MILLISECONDS),  payResult);
        return payResult;
    }


    /**
     * generate result
     * @param builder
     * @param param
     */
    protected void generateResultData(PayResultBuilder builder, T param){

    }

    /**
     * check sign
     * @param param
     * @return
     */
    protected boolean checkSign(T param){
        String key = getRequest().getParameter("authcookie");
        Map<String, String> signMap = Maps.newHashMap();
        List<Field> fields = Lists.newArrayList();
        Class<?> superClass = param.getClass();
        for (; superClass != Object.class; superClass = superClass.getSuperclass()) {
            fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
        }
        fields.forEach(field -> {
            Object val = null;
            try {
                val = ReflectionUtils.invokeGetterMethod(param, field.getName());
            }catch (Exception e){}
            if (val != null){
                signMap.put(field.getName(), String.valueOf(val));
            }
        });
        return PayUtils.doCheckMessageRequest(signMap, key);
    }

    /**
     * @return
     */
    protected HttpServletRequest getRequest(){
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * @return
     */
    protected HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    /**
     * get remote ip address
     * @return
     */
    protected String getRemoteIP(){
        return RequestUtils.getRemoteAddr(this.getRequest());
    }
}
