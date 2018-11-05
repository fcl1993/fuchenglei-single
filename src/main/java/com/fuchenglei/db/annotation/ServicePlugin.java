package com.fuchenglei.db.annotation;

import com.fuchenglei.core.container.Plugin;

import java.lang.annotation.*;

/**
 * 服务注册
 *
 * @author 付成垒
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Plugin
@Inherited
public @interface ServicePlugin
{

    String value() default "";

}
