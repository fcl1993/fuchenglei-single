package com.fuchenglei.db.core;

import org.apache.log4j.Logger;

import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 资源库
 *
 * @author 付成垒
 */
final class Strategy
{

    private static Logger logger = Logger.getLogger(Strategy.class);

    private static final ResourceBundle resource = ResourceBundle.getBundle("jdbc");

    static final boolean printSQL = resource.keySet().contains("printSQL") ? Boolean.parseBoolean(resource.getString("printSQL")) : false;

    static CopyOnWriteArrayList<String> db = null;

    static final String defaultDB = resource.keySet().contains("defaultDB") ? resource.getString("defaultDB") : "db";

    static void init()
    {
        int i = 0;

        if (resource.keySet().contains("db"))
        {
            String strategy = resource.getString("db").substring(1, resource.getString("db").length() - 1);
            String[] strategys = strategy.split(",");
            for (i = 0; i < strategys.length; i++)
            {
                if (db == null)
                    db = new CopyOnWriteArrayList<String>();
                try
                {
                    db.add(String.valueOf(strategys[i]));
                }
                catch (Exception e)
                {
                    logger.error("error: " + strategy);
                    throw new DBConfigurationException("error: " + strategy);
                }

            }
        }

    }

}
