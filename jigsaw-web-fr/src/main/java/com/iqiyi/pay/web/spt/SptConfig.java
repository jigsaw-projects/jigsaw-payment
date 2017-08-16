/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.web.spt;

import com.iqiyi.pay.frontend.web.intercept.RefundInterceptor;
import com.iqiyi.pay.monitor.utils.MonitorInterceptor;
import com.iqiyi.pay.web.spt.para.valid.ParaHandlerMethodArgumentResolver;
import com.iqiyi.pay.web.spt.sign.ParameterSignHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.validation.Validator;
import java.util.List;


/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年7月15日
 */
@Configuration
public class SptConfig extends WebMvcConfigurerAdapter {

	
//	@Bean
//	ExeTimeRecordFilter exeTimeRecordFilter() {
//		return new ExeTimeRecordFilter();
//	}


	
/*	@Bean
	public PassportFilter passportFilter() {
		return new PassportFilter();
	}*/

	@Bean 
	public Validator validator() {
		return new LocalValidatorFactoryBean();
	}
	
	@Bean
	public ParameterSignHandlerInterceptor parameterSignHandlerInterceptor() {
	    return new ParameterSignHandlerInterceptor();
	}

	@Bean
	public MonitorInterceptor monitorInterceptor(){
		return new MonitorInterceptor();
	}

	@Bean
	public RefundInterceptor refundInterceptor(){
		return new RefundInterceptor();
	}

	@Bean
	public ParaHandlerMethodArgumentResolver paraHandlerMethodArgumentResolver() {
		return new ParaHandlerMethodArgumentResolver();
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(paraHandlerMethodArgumentResolver());
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(monitorInterceptor());
		registry.addInterceptor(refundInterceptor());
		registry.addInterceptor(parameterSignHandlerInterceptor());
	}

}
