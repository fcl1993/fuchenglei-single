package com.fuchenglei.core.process;

import com.fuchenglei.core.container.Linked;

import java.lang.reflect.Field;

/**
 * 代理生成及代理类处理
 *
 * @author 付成垒
 */
public abstract class PostProxy<T>
{

    public abstract T createProxy(T object);

    protected void process(Object proxy, Object reference)
    {
        Field[] fields = reference.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++)
        {
            if (fields[i].isAnnotationPresent(Linked.class))
            {
                fields[i].setAccessible(true);
                try
                {
                    fields[i].set(proxy, fields[i].get(reference));
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

}
