package com.fuchenglei.core.container;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 后置处理器
 *
 * @author 付成垒
 */
interface Post
{

    //class解析
    void resolve(ConcurrentHashMap<String, Object> container, List<Class<?>> postProcess, List<Class<?>> classes);

    //class处理器
    Object post(Class<?> clazz, ConcurrentHashMap<String, Object> container, List<Class<?>> postProcess, List<Class<?>> classes);

}
