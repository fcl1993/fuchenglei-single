package com.fuchenglei.te;

import com.fuchenglei.core.runner.Grade;
import com.fuchenglei.function.ContainerRunner;
import com.fuchenglei.core.annotation.Task;

@Task
public class C implements ContainerRunner
{

    @Override
    @Grade(2)
    public void run()
    {
        System.out.println(10);
    }

}
