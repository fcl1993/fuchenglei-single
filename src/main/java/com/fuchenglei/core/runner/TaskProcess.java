package com.fuchenglei.core.runner;

import java.lang.reflect.Method;

/**
 * 任务
 *
 * @author 付成垒
 */
final class TaskProcess
{

    private Method method;

    private Object object;

    private int grade;

    public TaskProcess(Method method, Object object)
    {
        this.method = method;
        this.object = object;
    }

    public TaskProcess(Method method, Object object, int grade)
    {
        this.method = method;
        this.object = object;
        this.grade = grade;
    }

    public Method getMethod()
    {
        return method;
    }

    public void setMethod(Method method)
    {
        this.method = method;
    }

    public Object getObject()
    {
        return object;
    }

    public void setObject(Object object)
    {
        this.object = object;
    }

    public int getGrade()
    {
        return grade;
    }

    public void setGrade(int grade)
    {
        this.grade = grade;
    }

}
