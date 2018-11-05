package com.fuchenglei.core.runner;

import java.lang.annotation.*;

/**
 * 容器启动的循环任务
 *
 * @author 付成垒
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface RunnerLoop
{

    String value() default "";

    //延时时间
    int lazy() default 0;

    //间隔时间
    int time() default 10 * 1000;

}
