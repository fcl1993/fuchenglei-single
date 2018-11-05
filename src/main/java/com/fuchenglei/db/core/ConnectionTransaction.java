package com.fuchenglei.db.core;

import java.sql.Connection;

/**
 * 线程级连接池
 *
 * @author 付成垒
 */
final class ConnectionTransaction
{

    private String stopName;

    private boolean transaction;

    private Connection connection;

    private boolean rollback;

    public ConnectionTransaction()
    {
        this.rollback = false;
    }

    public ConnectionTransaction(String stopName, boolean transaction)
    {
        this.rollback = false;
        this.stopName = stopName;
        this.transaction = transaction;
    }

    public ConnectionTransaction(String stopName, boolean transaction, Connection connection)
    {
        this.rollback = false;
        this.stopName = stopName;
        this.transaction = transaction;
        this.connection = connection;
    }

    public String getStopName()
    {
        return stopName;
    }

    public void setStopName(String stopName)
    {
        this.stopName = stopName;
    }

    public boolean isTransaction()
    {
        synchronized (this)
        {
            return transaction;
        }
    }

    public void setTransaction(boolean transaction)
    {
        synchronized (this)
        {
            this.transaction = transaction;
        }
    }

    public Connection getConnection()
    {
        return connection;
    }

    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }

    public boolean isRollback()
    {
        return rollback;
    }

    public void setRollback(boolean rollback)
    {
        this.rollback = rollback;
    }

}
