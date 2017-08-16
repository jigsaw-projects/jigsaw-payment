/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */

package com.iqiyi.pay.web.spt.para.valid;

import com.iqiyi.pay.web.spt.annotation.Para;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;


/**
 * 识别@Para注明的参数
 * 
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * 2016年6月3日
 */
public class ParaHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

	private SimpleTypeConverter converter = new SimpleTypeConverter();
	
	private DefaultParameterNameDiscoverer ppnd = new DefaultParameterNameDiscoverer();
	
	@Autowired
	HttpServletResponse response;
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterAnnotation(Para.class) != null;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		parameter.initParameterNameDiscovery(ppnd);
		Para p = parameter.getParameterAnnotation(Para.class);
		if (p == null) {
			return null;
		}
		Object val = webRequest.getParameter(p.name());
		if (p.required()) {
			if (val == null) {
				val = p.defaultValue();
			}
		}
		if (ValueConstants.DEFAULT_NONE.equals(val)) {
			val = null;
		}
		try {
			return converter.convertIfNecessary(val, parameter.getParameterType());
		} catch (Exception e) {
			throw new ParamErrorexception(p.name(), e.getMessage());
		}
	}
}
