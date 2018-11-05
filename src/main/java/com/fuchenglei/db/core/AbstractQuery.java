package com.fuchenglei.db.core;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.OutParameter;
import org.apache.commons.dbutils.StatementConfiguration;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Arrays;

/**
 * 抽象
 *
 * @author 付成垒
 */
abstract class AbstractQuery
{

    private volatile boolean pmdKnownBroken = false;

    private final StatementConfiguration stmtConfig;

    public AbstractQuery()
    {
        this.stmtConfig = null;
    }

    public AbstractQuery(boolean pmdKnownBroken)
    {
        this.pmdKnownBroken = pmdKnownBroken;
        this.stmtConfig = null;
    }

    public AbstractQuery(StatementConfiguration stmtConfig)
    {
        this.stmtConfig = stmtConfig;
    }

    public boolean isPmdKnownBroken()
    {
        return pmdKnownBroken;
    }

    protected PreparedStatement prepareStatement(Connection conn, String sql) throws SQLException
    {
        PreparedStatement ps = conn.prepareStatement(sql);
        try
        {
            configureStatement(ps);
        }
        catch (SQLException e)
        {
            ps.close();
            throw e;
        }
        return ps;
    }

    protected PreparedStatement prepareStatement(Connection conn, String sql, int returnedKeys) throws SQLException
    {
        PreparedStatement ps = conn.prepareStatement(sql, returnedKeys);
        try
        {
            configureStatement(ps);
        }
        catch (SQLException e)
        {
            ps.close();
            throw e;
        }
        return ps;
    }

    private void configureStatement(Statement stmt) throws SQLException
    {
        if (stmtConfig != null)
        {
            if (stmtConfig.isFetchDirectionSet())
                stmt.setFetchDirection(stmtConfig.getFetchDirection());
            if (stmtConfig.isFetchSizeSet())
                stmt.setFetchSize(stmtConfig.getFetchSize());
            if (stmtConfig.isMaxFieldSizeSet())
                stmt.setMaxFieldSize(stmtConfig.getMaxFieldSize());
            if (stmtConfig.isMaxRowsSet())
                stmt.setMaxRows(stmtConfig.getMaxRows());
            if (stmtConfig.isQueryTimeoutSet())
                stmt.setQueryTimeout(stmtConfig.getQueryTimeout());
        }
    }

    protected CallableStatement prepareCall(Connection conn, String sql) throws SQLException
    {
        return conn.prepareCall(sql);
    }

    public void fillStatement(PreparedStatement stmt, Object... params) throws SQLException
    {
        ParameterMetaData pmd = null;
        if (!pmdKnownBroken)
        {
            try
            {
                pmd = stmt.getParameterMetaData();
                if (pmd == null)
                {
                    pmdKnownBroken = true;
                }
                else
                {
                    int stmtCount = pmd.getParameterCount();
                    int paramsCount = params == null ? 0 : params.length;

                    if (stmtCount != paramsCount)
                    {
                        throw new SQLException("Wrong number of parameters: expected "
                                + stmtCount + ", was given " + paramsCount);
                    }
                }
            }
            catch (SQLFeatureNotSupportedException ex)
            {
                pmdKnownBroken = true;
            }
        }
        if (params == null)
        {
            return;
        }
        CallableStatement call = null;
        if (stmt instanceof CallableStatement)
        {
            call = (CallableStatement) stmt;
        }
        for (int i = 0; i < params.length; i++)
        {
            if (params[i] != null)
            {
                if (call != null && params[i] instanceof OutParameter)
                {
                    Method method = null;
                    try
                    {
                        method = OutParameter.class.getDeclaredMethod("register", CallableStatement.class, int.class);
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
                else
                {
                    stmt.setObject(i + 1, params[i]);
                }
            }
            else
            {
                int sqlType = Types.VARCHAR;
                if (!pmdKnownBroken)
                {
                    try
                    {
                        sqlType = pmd.getParameterType(i + 1);
                    }
                    catch (SQLException e)
                    {
                        pmdKnownBroken = true;
                    }
                }
                stmt.setNull(i + 1, sqlType);
            }
        }
    }

    public void fillStatementWithBean(PreparedStatement stmt, Object bean, PropertyDescriptor[] properties) throws SQLException
    {
        Object[] params = new Object[properties.length];
        for (int i = 0; i < properties.length; i++)
        {
            PropertyDescriptor property = properties[i];
            Object value = null;
            Method method = property.getReadMethod();
            if (method == null)
            {
                throw new RuntimeException("No read method for bean property "
                        + bean.getClass() + " " + property.getName());
            }
            try
            {
                value = method.invoke(bean, new Object[0]);
            }
            catch (InvocationTargetException e)
            {
                throw new RuntimeException(
                        "Couldn't invoke method: " + method,
                        e
                );
            }
            catch (IllegalArgumentException e)
            {
                throw new RuntimeException(
                        "Couldn't invoke method with 0 arguments: " + method, e);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(
                        "Couldn't invoke method: " + method,
                        e
                );
            }
            params[i] = value;
        }
        fillStatement(stmt, params);
    }

    public void fillStatementWithBean(PreparedStatement stmt, Object bean, String... propertyNames) throws SQLException
    {
        PropertyDescriptor[] descriptors;
        try
        {
            descriptors = Introspector.getBeanInfo(bean.getClass())
                    .getPropertyDescriptors();
        }
        catch (IntrospectionException e)
        {
            throw new RuntimeException("Couldn't introspect bean "
                    + bean.getClass().toString(), e);
        }
        PropertyDescriptor[] sorted = new PropertyDescriptor[propertyNames.length];
        for (int i = 0; i < propertyNames.length; i++)
        {
            String propertyName = propertyNames[i];
            if (propertyName == null)
            {
                throw new NullPointerException("propertyName can't be null: "
                        + i);
            }
            boolean found = false;
            for (int j = 0; j < descriptors.length; j++)
            {
                PropertyDescriptor descriptor = descriptors[j];
                if (propertyName.equals(descriptor.getName()))
                {
                    sorted[i] = descriptor;
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                throw new RuntimeException("Couldn't find bean property: "
                        + bean.getClass() + " " + propertyName);
            }
        }
        fillStatementWithBean(stmt, bean, sorted);
    }

    protected void rethrow(SQLException cause, String sql, Object... params) throws SQLException
    {
        String causeMessage = cause.getMessage();
        if (causeMessage == null)
        {
            causeMessage = "";
        }
        StringBuffer msg = new StringBuffer(causeMessage);
        msg.append(" Query: ");
        msg.append(sql);
        msg.append(" Parameters: ");
        if (params == null)
            msg.append("[]");
        else
            msg.append(Arrays.deepToString(params));
        SQLException e = new SQLException(msg.toString(), cause.getSQLState(), cause.getErrorCode());
        e.setNextException(cause);
        throw e;
    }

    protected ResultSet wrap(ResultSet rs)
    {
        return rs;
    }

    protected void close(Statement stmt) throws SQLException
    {
        DbUtils.close(stmt);
    }

    protected void close(ResultSet rs) throws SQLException
    {
        DbUtils.close(rs);
    }

}
