package com.fuchenglei.db.core;

import com.fuchenglei.core.process.PostProcess;
import com.fuchenglei.core.process.PluginProcess;
import com.fuchenglei.db.annotation.ServicePlugin;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ServicePlugin 注入
 *
 * @author 付成垒
 */
@PostProcess
public final class ServicePluginProcess implements PluginProcess
{

    private ServicePlugin pluginType;

    private ServiceProxy proxy = new ServiceProxy();

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
