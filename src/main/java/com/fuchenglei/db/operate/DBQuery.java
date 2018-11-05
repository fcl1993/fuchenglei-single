package com.fuchenglei.db.operate;

import com.fuchenglei.db.core.DBBaseQuery;
import org.apache.commons.dbutils.ResultSetHandler;

/**
 * 数据库操作管理器
 *
 * @author 付成垒
 */
public final class DBQuery extends DBBaseQuery
{

    private DBQuery()
    {
        super();
    }

    public static DBQuery query()
    {
        return new DBQuery();
    }

    public <T> T insert(String sql, ResultSetHandler<T> rsh, Object... params)
    {
        return super.insert(sql, rsh, params);
    }

    public <T extends Number> T insert(String sql, Object... params)
    {
        return super.insert(sql, params);
    }

    public int executeUpdate(String sql, Object... params)
    {
        return super.executeUpdate(sql, params);
    }

    public int execute(String sql, Object... params)
    {
        return super.execute(sql, params);
    }

    public <T> T query(String sql, ResultSetHandler<T> rsh, Object... params)
    {
        return super.query(sql, rsh, params);
    }

    //暂时只提供次函数做批处理
    public int[] batch(String sql, Object[][] params)
    {
        return super.batch(sql, params);
    }

}
