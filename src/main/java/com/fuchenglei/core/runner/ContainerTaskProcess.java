package com.fuchenglei.core.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.*;

/**
 * 容器初始化任务
 *
 * @author 付成垒
 */
public final class ContainerTaskProcess
{

    private List<TaskProcess> taskLoopProcesses;

    private List<TaskProcess> taskOnceProcesses;

    //开机class解析
    public void resolve(ScheduledExecutorService executor, ConcurrentHashMap<String, Object> container, CopyOnWriteArrayList<Object> containerTask, CopyOnWriteArrayList<Object> containerNewTask)
    {
        int i = 0, j = 0;
        Enumeration<String> keys = container.keys();
        while (keys.hasMoreElements())
        {
            String key = keys.nextElement();
            if (container.get(key).getClass().getSuperclass() == null) continue;
            Class<?>[] cs = container.get(key).getClass().getInterfaces();
            for (i = 0; i < cs.length; i++)
            {
                if (Runner.class.isAssignableFrom(cs[i]))
                {
                    containerTask.add(container.get(key));
                    break;
                }
            }
            if (container.get(key).getClass().getSuperclass().isAnnotationPresent(RunnerPlugin.class))
            {
                containerNewTask.add(container.get(key));
            }
        }
        this.taskOnceProcesses = new ArrayList<TaskProcess>();
        this.taskLoopProcesses = new ArrayList<TaskProcess>();
        for (i = 0; i < containerTask.size(); i++)
        {
            Object object = containerTask.get(i);
            if (object instanceof ContainerRunner)
                try
                {
                    Method method = object.getClass().getMethod("run", null);
                    if (method.isAnnotationPresent(Grade.class))
                    {
                        this.taskOnceProcesses.add(new TaskProcess(method, object, method.getDeclaredAnnotation(Grade.class).value()));
                    }
                    else
                    {
                        this.taskOnceProcesses.add(new TaskProcess(method, object, Integer.MAX_VALUE));
                    }
                }
                catch (NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
            if (object instanceof ContainerLoopRunner)
            {
                try
                {
                    Method method = object.getClass().getMethod("run", null);
                    if (method.isAnnotationPresent(RunnerLoop.class))
                        this.taskLoopProcesses.add(new TaskProcess(method, object));
                }
                catch (NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
            }
        }
        for (i = 0; i < containerNewTask.size(); i++)
        {
            Object object = containerNewTask.get(i);
            Method[] methods = object.getClass().getSuperclass().getDeclaredMethods();
            for (j = 0; j < methods.length; j++)
            {
                Method method = methods[j];
                if (method.isAnnotationPresent(RunnerOnce.class))
                {
                    if (method.isAnnotationPresent(Grade.class))
                        this.taskOnceProcesses.add(new TaskProcess(method, object, method.getDeclaredAnnotation(Grade.class).value()));
                    else
                        this.taskOnceProcesses.add(new TaskProcess(method, object, Integer.MAX_VALUE));
                }
                else if (method.isAnnotationPresent(RunnerLoop.class))
                {
                    this.taskLoopProcesses.add(new TaskProcess(method, object));
                }
            }
        }
        this.taskOnceProcesses.sort(new Comparator<TaskProcess>()
        {
            @Override
            public int compare(TaskProcess taskProcessBefore, TaskProcess taskProcessAfter)
            {
                if (taskProcessBefore.getGrade() < taskProcessAfter.getGrade()) return -1;
                return 0;
            }
        });
        post(executor, this.taskOnceProcesses);
        post(executor, this.taskLoopProcesses);
    }

    //开机class加载初始化
    public void post(ScheduledExecutorService executor, List<TaskProcess> taskProcesses)
    {
        int i = 0;
        for (i = 0; i < taskProcesses.size(); i++)
        {
            Method method = taskProcesses.get(i).getMethod();
            Object object = taskProcesses.get(i).getObject();
            if (method.isAnnotationPresent(RunnerOnce.class) || ContainerRunner.class.isAssignableFrom(method.getDeclaringClass()))
            {
                try
                {
                    if (method.getDeclaredAnnotation(RunnerOnce.class) != null && method.getDeclaredAnnotation(RunnerOnce.class).lazy() != 0)
                        Thread.sleep(method.getDeclaredAnnotation(RunnerOnce.class).lazy());
                    method.invoke(object, null);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if (method.isAnnotationPresent(RunnerLoop.class))
            {
                executor.scheduleAtFixedRate(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            method.invoke(object, null);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, method.getDeclaredAnnotation(RunnerLoop.class).lazy(), method.getDeclaredAnnotation(RunnerLoop.class).time(), TimeUnit.MILLISECONDS);
            }
        }
    }

}
