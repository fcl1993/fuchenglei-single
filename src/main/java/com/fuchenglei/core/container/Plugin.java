package com.fuchenglei.core.container;

import java.lang.annotation.*;

/**
 * 容器依赖
 *
 * @author 付成垒
 */
@Target(value = ElementType.ANNOTATION_TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface Plugin
{
}
