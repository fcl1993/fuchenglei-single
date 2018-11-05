package com.fuchenglei.core.process;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理器
 *
 * @author 付成垒
 */
public interface PluginProcess
{

    Object process(Object object, ConcurrentHashMap<String, Object> container);

}
