package com.fuchenglei.core.container;

import com.fuchenglei.core.process.PostProcess;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * service bean选择处理器
 *
 * @author 付成垒
 */
final class ContainerProcess implements Post
{

    protected ContainerProcess()
    {
    }

    //解析class文件
    @Override
    public void resolve(ConcurrentHashMap<String, Object> container, List<Class<?>> postProcess, List<Class<?>> classes)
    {
        int i = 0, j = 0;
        //获取所有的后置处理器
        for (i = 0; i < classes.size(); i++)
        {
            //判断是否是后置处理器
            if (classes.get(i).isAnnotationPresent(PostProcess.class))
            {
                //增加后置处理器
                postProcess.add(classes.get(i));
            }
        }
        //加载到容器
        for (i = 0; i < classes.size(); i++)
        {
            //判断是不是依赖
            if (classes.get(i).isAnnotation()) continue;
            Annotation[] annotations = classes.get(i).getDeclaredAnnotations();
            for (j = 0; j < annotations.length; j++)
            {
                //判断是不是容器指定的依赖类
                if (annotations[j].annotationType().isAnnotationPresent(Plugin.class))
                {
                    container.put(classes.get(i).getName(), post(classes.get(i), container, postProcess, classes));
                }
            }
        }
    }

    //解析处理容器中的依赖
    @Override
    public Object post(Class<?> clazz, ConcurrentHashMap<String, Object> container, List<Class<?>> postProcess, List<Class<?>> classes)
    {
        try
        {
            int i = 0, j = 0;
            //判断是否已加载到容器
            if (container.containsKey(clazz.getName())) return container.get(clazz.getName());
            //创建实例
            Object o = clazz.newInstance();
            //获取实例属性
            Field[] fields = clazz.getDeclaredFields();
            for (i = 0; i < fields.length; i++)
            {
                Object obj = null;
                //判断注入
                if (fields[i].isAnnotationPresent(Linked.class))
                {
                    fields[i].setAccessible(true);
                    obj = container.get(fields[i].getType().getName());
                    //判断依赖项是否已被容器初始化
                    if (obj == null)
                    {
                        //判断依赖项是否被容器管理
                        if (!classes.contains(fields[i].getType()))
                        {
                            throw new ClassNotFindException("class not find exception! class: " + fields[i].getType().getName());
                        }
                        try
                        {
                            //初始化依赖项到容器
                            Object b = post(fields[i].getType(), container, postProcess, classes);
                            if (b == null)
                                throw new ClassNotFindException("class not find exception! class: " + fields[i].getType().getName());
                            //注入属性
                            fields[i].set(o, b);
                        }
                        catch (IllegalAccessException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        //注入属性
                        fields[i].set(o, obj);
                    }
                }
            }
            Object object = o;
            //分配后置处理器
            for (i = 0; i < postProcess.size(); i++)
            {
                //判断后置处理器是否有用
                if (postProcess.get(i).getDeclaredField("pluginType") == null) continue;
                //获取处理的类型
                Class annotation = postProcess.get(i).getDeclaredField("pluginType").getType();
                //当前类处理器类型
                Annotation[] classAnnotation = clazz.getAnnotations();
                for (j = 0; j < classAnnotation.length; j++)
                {
                    //判断是否能处理当前类
                    if (annotation == classAnnotation[j].annotationType())
                    {
                        //处理当前类
                        object = postProcess.get(i).getMethod("process", Object.class, ConcurrentHashMap.class).invoke(postProcess.get(i).newInstance(), o, container);
                    }
                }
            }
            //加入容器
            container.put(clazz.getName(), object);
            return object;
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
