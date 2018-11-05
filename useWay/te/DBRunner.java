package com.fuchenglei.db;

import com.fuchenglei.core.annotation.Linked;
import com.fuchenglei.core.runner.RunnerOnce;
import com.fuchenglei.core.runner.RunnerPlugin;
import com.fuchenglei.db.DataCenter;

/**
 * 系统级容器初始化启动
 *
 * @author 付成垒
 */
@RunnerPlugin
public class DBRunner
{

    @Linked
    private DataCenter dataCenter;

    @RunnerOnce
    public void databaseStart()
    {
        dataCenter.start();
    }

}
