/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */

package com.iqiyi.pay.web.spt.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.ValueConstants;

/**
 * 标注自定义参数处理
 * 
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * 2016年6月3日
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Para {
	
	@AliasFor("value")
	String name() default "";
	
	@AliasFor("name")
	String value() default "";
	
	boolean required() default true;

	String defaultValue() default ValueConstants.DEFAULT_NONE;
}
