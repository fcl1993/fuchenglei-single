package com.fuchenglei.te;

import com.fuchenglei.core.runner.Grade;
import com.fuchenglei.core.runner.RunnerLoop;
import com.fuchenglei.core.runner.RunnerOnce;
import com.fuchenglei.core.runner.RunnerPlugin;

@RunnerPlugin
public class A
{

    @Grade(1)
    @RunnerOnce
    public void say()
    {
        System.out.println("1");
    }

    @Grade(2)
    @RunnerOnce
    public void say2()
    {
        System.out.println("2");
    }

    @Grade(4)
    @RunnerOnce
    public void say4()
    {
        System.out.println("4");
    }

    @Grade(3)
    @RunnerOnce
    public void say3()
    {
        System.out.println("3");
    }

    @RunnerLoop(lazy = 1 * 1000 , time = 10 * 1000)
    public void say5()
    {
        System.out.println("5");
    }

    @RunnerLoop(lazy = 1 * 1000 , time = 10 * 1000)
    public void say6()
    {
        System.out.println("6");
    }

}
