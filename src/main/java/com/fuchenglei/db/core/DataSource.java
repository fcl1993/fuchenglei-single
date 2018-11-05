package com.fuchenglei.db.core;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 数据库连接中心
 *
 * @author 付成垒
 */
final class DataSource
{

    private static Logger logger = Logger.getLogger(DataSource.class);

    private DataSource()
    {
        try
        {
            int i = 0;
            //初始化大表策略
            Strategy.init();
            if (sources == null)
                sources = new ConcurrentHashMap<String, BasicDataSource>();
            //开启默认配置
            ResourceBundle bundle = ResourceBundle.getBundle("jdbc");
            CopyOnWriteArrayList<String> db = Strategy.db;
            if (db == null)
                db = new CopyOnWriteArrayList<String>();
            Iterator<String> dbs = db.iterator();
            while (dbs.hasNext())
            {
                String prefix = dbs.next() + "#";
                Properties properties = new Properties();
                properties.setProperty("username", bundle.getString(prefix + "username").trim());
                properties.setProperty("password", bundle.getString(prefix + "password").trim());
                properties.setProperty("url", bundle.getString(prefix + "url").trim());
                properties.setProperty("driverClassName", bundle.getString(prefix + "driverClassName").trim());
                properties.setProperty("minIdle", bundle.getString(prefix + "minIdle").trim());
                properties.setProperty("maxIdle", bundle.getString(prefix + "maxIdle").trim());
                properties.setProperty("maxTotal", bundle.getString(prefix + "maxTotal").trim());
                properties.setProperty("initialSize", bundle.getString(prefix + "initialSize").trim());
                if (bundle.keySet().contains(prefix + "maxWaitMillis") && bundle.getString(prefix + "maxWaitMillis") != null && !"".equals(bundle.getString(prefix + "maxWaitMillis").trim()))
                    properties.setProperty("maxWaitMillis", bundle.getString(prefix + "maxWaitMillis").trim());
                else
                    properties.setProperty("maxWaitMillis", "6000");
                if (bundle.keySet().contains(prefix + "testWhileIdle") && bundle.getString(prefix + "testWhileIdle") != null && !"".equals(bundle.getString(prefix + "testWhileIdle").trim()))
                    properties.setProperty("testWhileIdle", bundle.getString(prefix + "testWhileIdle").trim());
                else
                    properties.setProperty("testWhileIdle", "true");
                if (bundle.keySet().contains(prefix + "timeBetweenEvictionRunsMillis") && bundle.getString(prefix + "timeBetweenEvictionRunsMillis") != null && !"".equals(bundle.getString(prefix + "timeBetweenEvictionRunsMillis").trim()))
                    properties.setProperty("timeBetweenEvictionRunsMillis", bundle.getString(prefix + "timeBetweenEvictionRunsMillis").trim());
                else
                    properties.setProperty("timeBetweenEvictionRunsMillis", "10000");
                if (bundle.keySet().contains(prefix + "numTestsPerEvictionRun") && bundle.getString(prefix + "numTestsPerEvictionRun") != null && !"".equals(bundle.getString(prefix + "numTestsPerEvictionRun").trim()))
                    properties.setProperty("numTestsPerEvictionRun", bundle.getString(prefix + "numTestsPerEvictionRun").trim());
                else
                    properties.setProperty("numTestsPerEvictionRun", "3");
                if (bundle.keySet().contains(prefix + "minEvictableIdleTimeMillis") && bundle.getString(prefix + "minEvictableIdleTimeMillis") != null && !"".equals(bundle.getString(prefix + "minEvictableIdleTimeMillis").trim()))
                    properties.setProperty("minEvictableIdleTimeMillis", bundle.getString(prefix + "minEvictableIdleTimeMillis").trim());
                else
                    properties.setProperty("minEvictableIdleTimeMillis", "120000");
                if (bundle.keySet().contains(prefix + "removeAbandonedOnBorrow") && bundle.getString(prefix + "removeAbandonedOnBorrow") != null && !"".equals(bundle.getString(prefix + "removeAbandonedOnBorrow").trim()))
                    properties.setProperty("removeAbandonedOnBorrow", bundle.getString(prefix + "removeAbandonedOnBorrow").trim());
                else
                    properties.setProperty("removeAbandonedOnBorrow", "true");
                if (bundle.keySet().contains(prefix + "removeAbandonedOnMaintenance") && bundle.getString(prefix + "removeAbandonedOnMaintenance") != null && !"".equals(bundle.getString(prefix + "removeAbandonedOnMaintenance").trim()))
                    properties.setProperty("removeAbandonedOnMaintenance", bundle.getString(prefix + "removeAbandonedOnMaintenance").trim());
                else
                    properties.setProperty("removeAbandonedOnMaintenance", "true");
                if (bundle.keySet().contains(prefix + "removeAbandonedTimeout") && bundle.getString(prefix + "removeAbandonedTimeout") != null && !"".equals(bundle.getString(prefix + "removeAbandonedTimeout").trim()))
                    properties.setProperty("removeAbandonedTimeout", bundle.getString(prefix + "removeAbandonedTimeout").trim());
                else
                    properties.setProperty("removeAbandonedTimeout", "300");
                if (bundle.keySet().contains(prefix + "validationQuery") && bundle.getString(prefix + "validationQuery") != null && !"".equals(bundle.getString(prefix + "validationQuery").trim()))
                    properties.setProperty("validationQuery", bundle.getString(prefix + "validationQuery").trim());
                else
                    properties.setProperty("validationQuery", "SELECT 1");
                if (bundle.keySet().contains(prefix + "validationQueryTimeout") && bundle.getString(prefix + "validationQueryTimeout") != null && !"".equals(bundle.getString(prefix + "validationQueryTimeout").trim()))
                    properties.setProperty("validationQueryTimeout", bundle.getString("validationQueryTimeout").trim());
                else
                    properties.setProperty("validationQueryTimeout", "1");
                if (bundle.keySet().contains(prefix + "testOnReturn") && bundle.getString(prefix + "testOnReturn") != null && !"".equals(bundle.getString(prefix + "testOnReturn").trim()))
                    properties.setProperty("testOnReturn", bundle.getString(prefix + "testOnReturn").trim());
                else
                    properties.setProperty("testOnReturn", "false");
                if (bundle.keySet().contains(prefix + "testOnBorrow") && bundle.getString(prefix + "testOnBorrow") != null && !"".equals(bundle.getString(prefix + "testOnBorrow").trim()))
                    properties.setProperty("testOnBorrow", bundle.getString(prefix + "testOnBorrow").trim());
                else
                    properties.setProperty("testOnBorrow", "true");
                sources.put(prefix.substring(0, prefix.length() - 1), BasicDataSourceFactory.createDataSource(properties));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new DBConfigurationException("Database configuration error");
        }
    }

    private static DataSource dataSource = new DataSource();

    private static ConcurrentHashMap<String, BasicDataSource> sources;

    //暴露数据库连接池
    static BasicDataSource dataSource(String source)
    {
        if (source == null || "".equals(source))
            source = Strategy.defaultDB;
        BasicDataSource db = sources.get(source);
        if (db == null)
            throw new CanNotFindDBException("can not find database : " + source);
        return db;
    }

}
