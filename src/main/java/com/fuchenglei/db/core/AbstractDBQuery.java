package com.fuchenglei.db.core;

import com.fuchenglei.core.container.Container;
import com.fuchenglei.db.annotation.DB;
import com.fuchenglei.db.annotation.Transaction;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author 付成垒
 */
abstract class AbstractDBQuery
{

    protected BaseQuery baseQuery;

    private final static ThreadLocal<ConnectionTransaction> connectionTransaction = new ThreadLocal<ConnectionTransaction>();

    private DBSQLPrint dbsqlPrint;

    protected AbstractDBQuery()
    {
        this.dbsqlPrint = new DBSQLPrint();
        this.baseQuery = new BaseQuery(this.dbsqlPrint);
    }

    protected Connection queryConnection()
    {
        globalRollback();
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StackTraceElement element = elements[4];
        Method method = Container.obtainClassSource(element.getClassName(), element.getMethodName());
        if (method == null)
            throw new NullMethodException("The calling function could not be found.");
        this.dbSqlExecutePrefix(method);
        ConnectionTransaction pool = connectionTransaction.get();
        String db = method.isAnnotationPresent(DB.class) ? method.getDeclaredAnnotation(DB.class).source() : Strategy.defaultDB;
        if (this.dbsqlPrint.isPrintSQL())
        {
            this.dbsqlPrint.setDb(db);
            this.dbsqlPrint.setExecutePosition(element);
        }
        return pool.getConnection();
    }

    protected void getConnection(boolean transaction, String source)
    {
        try
        {
            ConnectionTransaction pool = connectionTransaction.get();
            Connection connection = pool.getConnection();
            if (connection == null)
            {
                connection = DataSource.dataSource(source).getConnection();
                pool.setConnection(connection);
            }
            if (transaction && connection.getAutoCommit())
            {
                pool.setTransaction(true);
                connection.setAutoCommit(false);
            }
        }
        catch (SQLException e)
        {
            throw new CanNotGetConnectionException("Unable to get the database connection");
        }
    }

    static void connectStart(String startMethodName, boolean transaction) throws SQLException
    {
        globalRollback();
        ConnectionTransaction pool = connectionTransaction.get();
        if (pool == null)
            connectionTransaction.set(new ConnectionTransaction(startMethodName, transaction, null));
    }

    static void rollback()
    {
        try
        {
            connectionTransaction.get().setRollback(true);
            Connection connection = connectionTransaction.get().getConnection();
            if (connection != null)
            {
                if (!connection.getAutoCommit())
                {
                    connection.rollback();
                }
            }
        }
        catch (Exception e)
        {
        }
    }

    static void close(String connectionInfo)
    {
        boolean close = false;
        try
        {
            ConnectionTransaction pool = connectionTransaction.get();
            Connection connection = pool.getConnection();
            if (pool.getStopName().equals(connectionInfo))
            {
                close = true;
                if (connection != null)
                {
                    if (!connection.getAutoCommit())
                    {
                        connection.commit();
                        connection.close();
                    }
                    else
                        connection.close();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (close)
                connectionTransaction.remove();
        }
    }

    private void dbSqlExecutePrefix(Method method)
    {
        String source = Strategy.defaultDB;
        DB db = method.isAnnotationPresent(DB.class) ? method.getDeclaredAnnotation(DB.class) : null;
        if (db != null)
            source = db.source();
        this.getConnection(method.isAnnotationPresent(Transaction.class) ? true : false, source);
    }

    private static void globalRollback()
    {
        if (connectionTransaction.get() != null && connectionTransaction.get().isRollback())
            throw new DataBaseException("global rollback");
    }

}
