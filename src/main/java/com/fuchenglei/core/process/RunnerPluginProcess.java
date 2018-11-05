package com.fuchenglei.core.process;

import com.fuchenglei.core.runner.RunnerPlugin;

import java.util.concurrent.ConcurrentHashMap;

/**
 * RunnerPlugin 注入
 *
 * @author 付成垒
 */
@PostProcess
public final class RunnerPluginProcess implements PluginProcess
{

    private RunnerPlugin pluginType;

    private RunnerProxy proxy = new RunnerProxy();

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
