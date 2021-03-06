package com.fuchenglei.core.process;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * ReferencePlugin生成器
 *
 * @author 付成垒
 */
final class ReferenceProxy<T> extends PostProxy<T> implements MethodInterceptor
{

    private T object;

    public T createProxy(T object)
    {
        this.object = object;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.object.getClass());
        enhancer.setCallback(this);
        Object o = enhancer.create();
        super.process(o, object);
        return (T) o;
    }

    @Override
    public Object intercept(Object object, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable
    {
        Object obj = null;
        try
        {
            obj = methodProxy.invoke(this.object, objects);
        }
        catch (Exception e)
        {
        }
        return obj;
    }

}
