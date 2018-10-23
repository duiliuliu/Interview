package com.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * JDBCConfig
 */
public class JDBCMysqlConfig {

    public static final String driver = "com.mysql.jdbc.Driver";
    public static final String url = "jdbc:mysql://localhost:3306/";
    public static final String dataBase = "Test";
    public static final String user = "user";
    public static final String password = "123456";

    static {
        // It can be configured through properties files.
        // You need to have a mysqlJDBC.properties file under the current project.

        // Properties properties = new Properties();
        // InputStream inputStream = JDBCMysqlConfig.class.getResourceAsStream("mysqlJDB.properties");
        // properties.load(in);
        // driver = properties.getProperty("driver");
        // url = properties.getProperty("url");
        // dataBase = properties.getProperty("database");
        // user = properties.getProperty("user");
        // password = properties.getProperty("password");

    }

}