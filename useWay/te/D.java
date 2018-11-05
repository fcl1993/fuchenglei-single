package com.fuchenglei.te;

import com.fuchenglei.core.runner.RunnerLoop;
import com.fuchenglei.function.ContainerLoopRunner;
import com.fuchenglei.core.annotation.Task;

@Task
public class D implements ContainerLoopRunner
{

    @Override
    @RunnerLoop(lazy = 1 * 1000 , time = 10 * 1000)
    public void run()
    {
        System.out.println("11");
    }

}
