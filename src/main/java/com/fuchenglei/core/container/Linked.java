package com.fuchenglei.core.container;

import java.lang.annotation.*;

/**
 * 自动注入
 *
 * @author 付成垒
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface Linked
{

    String value() default "";

}
