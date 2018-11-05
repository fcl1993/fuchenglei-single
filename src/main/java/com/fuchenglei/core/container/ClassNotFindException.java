package com.fuchenglei.core.container;

/**
 * 类加载失败
 *
 * @author 付成垒
 */
public class ClassNotFindException extends RuntimeException
{

    public ClassNotFindException(String message)
    {
        super(message);
    }

}
