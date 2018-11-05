package com.fuchenglei.core.runner;

import com.fuchenglei.core.container.ReferencePlugin;
import com.fuchenglei.core.process.PostProcess;
import com.fuchenglei.core.process.PluginProcess;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ReferencePlugin class容器注入
 *
 * @author 付成垒
 */
@PostProcess
public final class TaskPluginProcess implements PluginProcess
{

    private ReferencePlugin pluginType;

    private TaskProxy proxy;

    @Override
    public Object process(Object object, ConcurrentHashMap<String, Object> container)
    {
        Object proxyDataClass = null;
        if (proxy != null)
        {
            proxyDataClass = proxy.createProxy(object);
        }
        else
        {
            proxyDataClass = object;
        }
        container.put(object.getClass().getName(), proxyDataClass);
        return proxyDataClass;
    }

}
