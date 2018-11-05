package com.fuchenglei.db.core;

import org.apache.log4j.Logger;

import java.sql.Statement;

/**
 * 数据库sql打印
 *
 * @author 付成垒
 */
final class DBSQLPrint
{

    private static Logger logger = Logger.getLogger(DBSQLPrint.class);

    private String db;

    private StackTraceElement executePosition;

    private boolean printSQL = Strategy.printSQL;

    DBSQLPrint()
    {
    }

    DBSQLPrint(String db, StackTraceElement executePosition)
    {
        this.db = db;
        this.executePosition = executePosition;
    }

    DBSQLPrint(String db, StackTraceElement executePosition, boolean printSQL)
    {
        this.db = db;
        this.executePosition = executePosition;
        this.printSQL = printSQL;
    }

    void printSQL(Statement statement)
    {
        String sql = null;
        try
        {
            String sqlTemp = statement.toString();
            sql = sqlTemp.substring(sqlTemp.indexOf(":") + 2);
        }
        catch (Exception e)
        {
            logger.error("sql print error: " + e.getMessage());
            throw new DBSQLOutException("sql print error: " + e.getMessage());
        }
        logger.info("db operation info {" + "\"executePosition\":\"" + this.executePosition.getClassName() + "." + this.executePosition.getMethodName() + "." + this.executePosition.getLineNumber() + "\",\"db\":\"" + this.db + "\",\"sql\":\"" + sql + "\"}");
    }

    void cleanProperties()
    {
        this.db = null;
        this.executePosition = null;
    }

    String getDb()
    {
        return db;
    }

    void setDb(String db)
    {
        this.db = db;
    }

    StackTraceElement getExecutePosition()
    {
        return executePosition;
    }

    void setExecutePosition(StackTraceElement executePosition)
    {
        this.executePosition = executePosition;
    }

    boolean isPrintSQL()
    {
        return printSQL;
    }

    void setPrintSQL(boolean printSQL)
    {
        this.printSQL = printSQL;
    }
}
