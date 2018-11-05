package com.fuchenglei.core.process;

import java.lang.annotation.*;

/**
 * 处理器
 *
 * @author 付成垒
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface PostProcess
{
}
