package com.fuchenglei.db.core;

/**
 * 获取不到数据库连接
 *
 * @author 付成垒
 */
final class CanNotGetConnectionException extends DataBaseException
{

    public CanNotGetConnectionException(String message)
    {
        super(message);
    }

}
