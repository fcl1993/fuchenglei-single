package com.fuchenglei.db;

import com.fuchenglei.core.annotation.ServicePlugin;
import com.fuchenglei.core.annotation.Transaction;
import com.fuchenglei.core.runner.Grade;
import com.fuchenglei.core.runner.RunnerOnce;
import com.fuchenglei.core.runner.RunnerPlugin;
import com.fuchenglei.util.KeyAndValue;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据提供初始化
 *
 * @author 付成垒
 */
@ServicePlugin
public class DataCenter
{

    private static Logger logger = Logger.getLogger(DataCenter.class);

    //初始化数据库
    @Transaction
    public void start()
    {
        List<KeyAndValue> data = new ArrayList<KeyAndValue>();
        int i = 0;
        data.add(new KeyAndValue("SELECT table_name FROM information_schema.TABLES WHERE table_name ='information';", "CREATE TABLE `information` (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
                "  `data` varchar(1000) NOT NULL COMMENT '数据',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;"));
        i = data.size();
        for (KeyAndValue arg : data)
        {
            DBQuery runner = new DBQuery();
            Object[] objects = runner.query(arg.getKey(), new ArrayHandler());
            if (objects.length == 0)
            {
                runner.executeUpdate(arg.getValue());
            }
            i--;
        }
        if (0 != i)
            logger.info("数据库初始化完成..");
    }

}
