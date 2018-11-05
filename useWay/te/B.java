package com.fuchenglei.te;

import com.fuchenglei.core.runner.Grade;
import com.fuchenglei.core.runner.RunnerLoop;
import com.fuchenglei.core.runner.RunnerOnce;
import com.fuchenglei.core.runner.RunnerPlugin;

@RunnerPlugin
public class B
{

    @Grade(6)
    @RunnerOnce
    public void say()
    {
        System.out.println("6");
    }

    @Grade(2)
    @RunnerOnce
    public void say2()
    {
        System.out.println("2");
    }

    @Grade(7)
    @RunnerOnce
    public void say7()
    {
        System.out.println("7");
    }

    @Grade(3)
    @RunnerOnce
    public void say3()
    {
        System.out.println("3");
    }

    @RunnerLoop(lazy = 1 * 1000 , time = 10 * 1000)
    public void say8()
    {
        System.out.println("8");
    }

    @RunnerLoop(lazy = 1 * 1000 , time = 10 * 1000)
    public void say9()
    {
        System.out.println("9");
    }

}
