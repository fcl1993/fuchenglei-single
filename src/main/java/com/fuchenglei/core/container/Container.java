package com.fuchenglei.core.container;

import com.fuchenglei.core.runner.ContainerTaskProcess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 付成垒容器
 *
 * @author 付成垒
 */
public class Container<T>
{

    //启动任务管理器
    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    //后置处理器
    private static List<Class<?>> postProcess;

    //锁
    private static Lock lock = new ReentrantLock();

    //核心容器
    private static ConcurrentHashMap<String, Object> container;

    //任务容器接口板
    private static CopyOnWriteArrayList<Object> containerTask;

    //任务管理器Annotation版
    private static CopyOnWriteArrayList<Object> containerNewTask;

    //容器依赖class
    private static List<Class<?>> classes;

    //容器依赖class
    private static ConcurrentHashMap<String, Class<?>> clazzs;

    //class文件原子
    private static ConcurrentHashMap<String, ConcurrentHashMap<String, Method>> classSource;

    //启动付成垒容器
    public static void start()
    {
        containerSecurityCheckStart();
    }

    //启动付成垒容器
    public static void startSpring(Object object)
    {
        int i = 0;
        start();
        if (object != null)
        {
            Collection<Object> os = container.values();
            for (Object obj : os)
            {
                Class clazz = null;
                for (i = 0; i < classes.size(); i++)
                {
                    if (obj.getClass() == classes.get(i) || obj.getClass().getSuperclass() == classes.get(i))
                    {
                        clazz = classes.get(i);
                        break;
                    }
                }
                Method method = null;
                try
                {
                    method = object.getClass().getDeclaredMethod("registerSingleton", String.class, Object.class);
                    method.invoke(object, clazz.getName(), obj);
                }
                catch (NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    //容器使用的安全检查
    private static void containerSecurityCheckStart()
    {
        lock.lock();
        if (container == null || containerTask == null)
        {
            //初始化容器属性
            container = new ConcurrentHashMap<String, Object>();
            containerTask = new CopyOnWriteArrayList<Object>();
            containerNewTask = new CopyOnWriteArrayList<Object>();
            postProcess = new ArrayList<Class<?>>();
            classes = new ArrayList<Class<?>>();
            clazzs = new ConcurrentHashMap<String, Class<?>>();
            classSource = new ConcurrentHashMap<String, ConcurrentHashMap<String, Method>>();
            initBean();
        }
        lock.unlock();
    }

    //初始化容器
    private static void initBean()
    {
        int i = 0, j = 0;
        //加载系统依赖
        classes.addAll(BeanScan.getClassName("com.fuchenglei", true, Arrays.asList(new String[]{"com.fuchenglei.core", "com.fuchenglei.db"})));
        //加载用户依赖
        ResourceBundle bundle = ResourceBundle.getBundle("fuchenglei");
        String scanner = bundle.keySet().contains("scanner") ? bundle.getString("scanner") : "";
        String[] scanners = null;
        if (scanner == null || "".equals(scanner.trim()))
        {
            scanners = new String[]{""};        //不配置 扫根目录
        }
        else
        {
            scanners = scanner.split("&");
        }
        List<Class<?>> cl = new ArrayList<Class<?>>();
        cl = BeanScan.getClassName("", true, Arrays.asList(scanners));
        for (j = 0; j < cl.size(); j++)
        {
            //唯一性保证
            if (!classes.contains(cl.get(j)))
                classes.add(cl.get(j));
        }
        //加载依赖到容器
        for (Class<?> clazz : classes)
        {
            clazzs.put(clazz.getName(), clazz);
        }
        //加载原子
        for (i = 0; i < classes.size(); i++)
        {
            Method[] methods = classes.get(i).getDeclaredMethods();
            if (methods != null)
            {
                ConcurrentHashMap<String, Method> mmp = new ConcurrentHashMap<String, Method>();
                for (j = 0; j < methods.length; j++)
                {
                    mmp.put(methods[j].getName(), methods[j]);
                }
                classSource.put(classes.get(i).getName(), mmp);
            }
            else
            {
                classSource.put(classes.get(i).getName(), new ConcurrentHashMap<String, Method>());
            }
        }
        //初始化系统依赖
        new ContainerProcess().resolve(container, postProcess, classes);
        //初始化系统初始任务
        new ContainerTaskProcess().resolve(executor, container, containerTask, containerNewTask);
    }

    //获取容器中的依赖对象
    public T obtainBean(String name)
    {
        //检查容器安全
        containerSecurityCheckStart();
        return (T) (container.get(name));
    }

    //获取容器中的依赖对象
    public static <T> T obtainBean(Class<? extends T> clazz)
    {
        //检查容器安全
        containerSecurityCheckStart();
        return (T) (container.get(clazz.getName()));
    }

    //获取容器中的依赖
    public static Class<?> obtainClass(String name)
    {
        //检查容器安全
        containerSecurityCheckStart();
        return clazzs.get(name);
    }

    //获取容器中的函数原子
    public static Method obtainClassSource(String className, String methodName)
    {
        //检查容器安全
        containerSecurityCheckStart();
        return classSource.get(className).get(methodName);
    }

}
