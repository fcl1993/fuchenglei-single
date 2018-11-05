package com.fuchenglei.db.core;

/**
 * 找不到数据库异常
 *
 * @author 付成垒
 */
final class CanNotFindDBException extends DataBaseException
{

    public CanNotFindDBException(String message)
    {
        super(message);
    }

}
