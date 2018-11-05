package com.fuchenglei.db.core;

import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapHandler;

import java.sql.SQLException;
import java.util.Map;

/**
 * 数据库操作管理器
 *
 * @author 付成垒
 */
public class DBBaseQuery extends AbstractDBQuery
{

    protected DBBaseQuery()
    {
        super();
    }

    protected <T> T insert(String sql, ResultSetHandler<T> rsh, Object... params)
    {
        try
        {
            return super.baseQuery.insert(super.queryConnection(), sql, rsh, params);
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            throw new SQLExecuteException("SQL execution error: " + exception.getMessage());
        }
    }

    protected <T extends Number> T insert(String sql, Object... params)
    {
        try
        {
            Map result = super.baseQuery.insert(super.queryConnection(), sql, new MapHandler(), params);

            if (result == null || result.get("GENERATED_KEY") == null)
                return (T) (new Integer(-1));
            return (T) result.get("GENERATED_KEY");
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            throw new SQLExecuteException("SQL execution error: " + exception.getMessage());
        }
    }

    protected int[] batch(String sql, Object[][] params)
    {
        try
        {
            return super.baseQuery.batch(super.queryConnection(), sql, params);
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            throw new SQLExecuteException("SQL execution error: " + exception.getMessage());
        }
    }

    protected int executeUpdate(String sql, Object... params)
    {
        try
        {
            return super.baseQuery.update(super.queryConnection(), sql, params);
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            throw new SQLExecuteException("SQL execution error: " + exception.getMessage());
        }
    }

    protected int execute(String sql, Object... params)
    {
        try
        {
            return super.baseQuery.execute(super.queryConnection(), sql, params);
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            throw new SQLExecuteException("SQL execution error: " + exception.getMessage());
        }
    }

    protected <T> T query(String sql, ResultSetHandler<T> rsh, Object... params)
    {
        try
        {
            return super.baseQuery.query(super.queryConnection(), sql, rsh, params);
        }
        catch (SQLException exception)
        {
            exception.printStackTrace();
            throw new SQLExecuteException("SQL execution error: " + exception.getMessage());
        }
    }

}
