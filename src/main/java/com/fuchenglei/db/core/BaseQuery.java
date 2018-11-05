package com.fuchenglei.db.core;

import org.apache.commons.dbutils.OutParameter;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.StatementConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;

/**
 * 不支持在代码中使用
 *
 * @author 付成垒
 */
final class BaseQuery extends AbstractQuery
{

    private DBSQLPrint dbsqlPrint;

    public BaseQuery()
    {
        super();
    }

    public BaseQuery setDbsqlPrint(DBSQLPrint dbsqlPrint)
    {
        this.dbsqlPrint = dbsqlPrint;
        return this;
    }

    public BaseQuery(DBSQLPrint dbsqlPrint)
    {
        super();
        this.dbsqlPrint = dbsqlPrint;
    }

    public BaseQuery(boolean pmdKnownBroken)
    {
        super(pmdKnownBroken);
    }

    public BaseQuery(boolean pmdKnownBroken, DBSQLPrint dbsqlPrint)
    {
        super(pmdKnownBroken);
        this.dbsqlPrint = dbsqlPrint;
    }

    public BaseQuery(StatementConfiguration stmtConfig)
    {
        super(stmtConfig);
    }

    public BaseQuery(StatementConfiguration stmtConfig, DBSQLPrint dbsqlPrint)
    {
        super(stmtConfig);
        this.dbsqlPrint = dbsqlPrint;
    }

    int[] batch(Connection conn, String sql, Object[][] params) throws SQLException
    {
        if (conn == null)
            throw new SQLException("Null connection");
        if (sql == null)
            throw new SQLException("Null SQL statement");
        if (params == null)
            throw new SQLException("Null parameters. If parameters aren't need, pass an empty array.");
        PreparedStatement stmt = null;
        int[] rows = null;
        try
        {
            stmt = this.prepareStatement(conn, sql);

            for (int i = 0; i < params.length; i++)
            {
                this.fillStatement(stmt, params[i]);
                stmt.addBatch();
            }
            rows = stmt.executeBatch();
        }
        catch (SQLException e)
        {
            this.rethrow(e, sql, (Object[]) params);
        }
        finally
        {
            close(stmt);
        }
        return rows;
    }

    <T> T query(Connection conn, String sql, ResultSetHandler<T> rsh, Object... params)
            throws SQLException
    {
        if (conn == null)
            throw new SQLException("Null connection");
        if (sql == null)
            throw new SQLException("Null SQL statement");
        if (rsh == null)
            throw new SQLException("Null ResultSetHandler");
        PreparedStatement stmt = null;
        ResultSet rs = null;
        T result = null;
        try
        {
            stmt = this.prepareStatement(conn, sql);
            this.fillStatement(stmt, params);
            if (this.dbsqlPrint != null && this.dbsqlPrint.isPrintSQL())
            {
                dbsqlPrint.printSQL(stmt);
                dbsqlPrint.cleanProperties();
            }
            rs = this.wrap(stmt.executeQuery());
            result = rsh.handle(rs);
        }
        catch (SQLException e)
        {
            this.rethrow(e, sql, params);
        }
        finally
        {
            try
            {
                close(rs);
            }
            finally
            {
                close(stmt);
            }
        }
        return result;
    }

    int update(Connection conn, String sql, Object... params) throws SQLException
    {
        if (conn == null)
            throw new SQLException("Null connection");
        if (sql == null)
            throw new SQLException("Null SQL statement");
        PreparedStatement stmt = null;
        int rows = 0;
        try
        {
            stmt = this.prepareStatement(conn, sql);
            this.fillStatement(stmt, params);
            if (this.dbsqlPrint != null && this.dbsqlPrint.isPrintSQL())
            {
                dbsqlPrint.printSQL(stmt);
                dbsqlPrint.cleanProperties();
            }
            rows = stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            this.rethrow(e, sql, params);
        }
        finally
        {
            close(stmt);
        }
        return rows;
    }

    <T> T insert(Connection conn, String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException
    {
        if (conn == null)
            throw new SQLException("Null connection");
        if (sql == null)
            throw new SQLException("Null SQL statement");
        if (rsh == null)
            throw new SQLException("Null ResultSetHandler");
        PreparedStatement stmt = null;
        T generatedKeys = null;
        try
        {
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            this.fillStatement(stmt, params);
            if (this.dbsqlPrint != null && this.dbsqlPrint.isPrintSQL())
            {
                dbsqlPrint.printSQL(stmt);
                dbsqlPrint.cleanProperties();
            }
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            generatedKeys = rsh.handle(resultSet);
        }
        catch (SQLException e)
        {
            this.rethrow(e, sql, params);
        }
        finally
        {
            close(stmt);
        }
        return generatedKeys;
    }

    <T> T insertBatch(Connection conn, String sql, ResultSetHandler<T> rsh, Object[][] params) throws SQLException
    {
        if (conn == null)
            throw new SQLException("Null connection");
        if (sql == null)
            throw new SQLException("Null SQL statement");
        if (params == null)
            throw new SQLException("Null parameters. If parameters aren't need, pass an empty array.");
        PreparedStatement stmt = null;
        T generatedKeys = null;
        try
        {
            stmt = this.prepareStatement(conn, sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++)
            {
                this.fillStatement(stmt, params[i]);
                if (this.dbsqlPrint != null && this.dbsqlPrint.isPrintSQL())
                {
                    dbsqlPrint.printSQL(stmt);
                    dbsqlPrint.cleanProperties();
                }
                stmt.addBatch();
            }
            stmt.executeBatch();
            ResultSet rs = stmt.getGeneratedKeys();
            generatedKeys = rsh.handle(rs);
        }
        catch (SQLException e)
        {
            this.rethrow(e, sql, (Object[]) params);
        }
        finally
        {
            close(stmt);
        }
        return generatedKeys;
    }

    int execute(Connection conn, String sql, Object... params) throws SQLException
    {
        if (conn == null)
            throw new SQLException("Null connection");
        if (sql == null)
            throw new SQLException("Null SQL statement");
        CallableStatement stmt = null;
        int rows = 0;
        try
        {
            stmt = this.prepareCall(conn, sql);
            this.fillStatement(stmt, params);
            if (this.dbsqlPrint != null && this.dbsqlPrint.isPrintSQL())
            {
                dbsqlPrint.printSQL(stmt);
                dbsqlPrint.cleanProperties();
            }
            stmt.execute();
            rows = stmt.getUpdateCount();
            this.retrieveOutParameters(stmt, params);
        }
        catch (SQLException e)
        {
            this.rethrow(e, sql, params);
        }
        finally
        {
            close(stmt);
        }
        return rows;
    }

    //暂无此需求
    /*<T> List<T> execute(Connection conn, String sql, ResultSetHandler<T> rsh, Object... params) throws SQLException
    {
        if (conn == null)
            throw new SQLException("Null connection");
        if (sql == null)
            throw new SQLException("Null SQL statement");
        if (rsh == null)
            throw new SQLException("Null ResultSetHandler");
        CallableStatement stmt = null;
        List<T> results = new LinkedList<T>();
        try
        {
            stmt = this.prepareCall(conn, sql);
            this.fillStatement(stmt, params);
            if (this.dbsqlPrint != null && this.dbsqlPrint.isPrintSQL())
            {
                dbsqlPrint.printSQL(stmt);
                dbsqlPrint.cleanProperties();
            }
            boolean moreResultSets = stmt.execute();
            ResultSet rs = null;
            while (moreResultSets)
            {
                try
                {
                    rs = this.wrap(stmt.getResultSet());
                    results.add(rsh.handle(rs));
                    moreResultSets = stmt.getMoreResults();
                }
                finally
                {
                    close(rs);
                }
            }
            this.retrieveOutParameters(stmt, params);

        }
        catch (SQLException e)
        {
            this.rethrow(e, sql, params);
        }
        finally
        {
            close(stmt);
        }
        return results;
    }*/

    private void retrieveOutParameters(CallableStatement stmt, Object[] params) throws SQLException
    {
        if (params != null)
        {
            for (int i = 0; i < params.length; i++)
            {
                if (params[i] instanceof OutParameter)
                {
                    Method method = null;
                    try
                    {
                        method = OutParameter.class.getDeclaredMethod("setValue", CallableStatement.class, int.class);
                        method.invoke(params[i], stmt, i + 1);
                    }
                    catch (NoSuchMethodException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                    catch (InvocationTargetException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
