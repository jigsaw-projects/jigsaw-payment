package org.jigsaw.payment.metric;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 跟踪计算方法的执行时间的annotation类
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月13日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Timer {
	/**
	 * value值将作为本timer的ID
	 * @return
	 */
    String value();
}
