package com.fuchenglei.core.runner;

import java.lang.annotation.*;

/**
 * 容器启动后的单一任务
 *
 * @author 付成垒
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface RunnerOnce
{

    //延时时间
    int lazy() default 0;

}
