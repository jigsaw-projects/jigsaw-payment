/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.aspect;

import java.lang.annotation.*;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年7月19日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface InvokeTimes {
	
	int value() default 5;
}
