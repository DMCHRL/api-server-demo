package com.connectiontech.demo.annotation;

import java.lang.annotation.*;

/**
 * @author
 * <p>
 * 需要签名注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RSASign {

	/**
	 * 是否AOP统一处理
	 *
	 * @return false, true
	 */
	boolean value() default true;
}
