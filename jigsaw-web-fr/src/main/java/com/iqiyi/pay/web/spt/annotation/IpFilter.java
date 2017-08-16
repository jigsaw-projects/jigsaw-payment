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
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * 2016年6月1日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface IpFilter {
    /**
     * 应用标识符
     * @return
     */
    public String app();

    /**
     * 功能标识符
     * @return
     */
    public String func();

    /**
     * 是否限制接入方
     * @return
     */
    public boolean limitPartner() default false;
}

