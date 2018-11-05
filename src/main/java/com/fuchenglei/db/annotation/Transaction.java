package com.fuchenglei.db.annotation;

import java.lang.annotation.*;

/**
 * 手动事务控制管理器
 *
 * @author 付成垒
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface Transaction
{

    String value() default "";

}
