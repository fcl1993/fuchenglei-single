package com.fuchenglei.db.core;

import net.sf.cglib.proxy.CallbackFilter;

import java.lang.reflect.Method;

/**
 * ServicePlugin 生成器回调选择
 *
 * @author 付成垒
 */
final class TransactionCall implements CallbackFilter
{
    @Override
    public int accept(Method method)
    {
        //保留功能
        return 0;
    }

}
