package com.fuchenglei.db.core;

/**
 * sql执行失败异常
 *
 * @author 付成垒
 */
final class SQLExecuteException extends DataBaseException
{

    public SQLExecuteException(String message)
    {
        super(message);
    }

}
