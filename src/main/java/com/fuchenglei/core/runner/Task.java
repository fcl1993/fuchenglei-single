package com.fuchenglei.core.runner;

import com.fuchenglei.core.container.Plugin;

import java.lang.annotation.*;

/**
 * 容器启动任务
 *
 * @author 付成垒
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
@Plugin
public @interface Task
{
}
