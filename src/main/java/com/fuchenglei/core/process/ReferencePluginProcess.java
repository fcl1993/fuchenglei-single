package com.fuchenglei.core.process;

import com.fuchenglei.core.container.ReferencePlugin;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ReferencePlugin class容器注入
 *
 * @author 付成垒
 */
@PostProcess
public final class ReferencePluginProcess implements PluginProcess
{

    private ReferencePlugin pluginType;

    private ReferenceProxy proxy = new ReferenceProxy();

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
