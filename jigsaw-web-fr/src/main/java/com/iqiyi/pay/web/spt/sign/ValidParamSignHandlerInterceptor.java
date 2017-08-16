package com.iqiyi.pay.web.spt.sign;

import com.iqiyi.pay.web.spt.annotation.Sign;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by leishengbao on 9/2/16.
 */
public class ValidParamSignHandlerInterceptor extends HandlerInterceptorAdapter {


    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod hm = (HandlerMethod) handler;
        Sign sign = hm.getMethodAnnotation(Sign.class);
        if (sign == null) {
            return true;
        }


        return true;
    }

}
