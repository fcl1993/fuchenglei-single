package com.fuchenglei.core.runner;

import com.fuchenglei.core.container.Plugin;

import java.lang.annotation.*;

/**
 * 容器开机时的任务
 *
 * @author 付成垒
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Plugin
@Inherited
public @interface RunnerPlugin
{
}
