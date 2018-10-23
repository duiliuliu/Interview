package com.jdbc;

import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * testDriverManager
 */
public class testDriverManager {

    public Connection getConnection() throws SQLException {
        Class.forName(JDBCMysqlConfig.driver);
        return DriverManager.getConnection(JDBCMysqlConfig.url + JDBCMysqlConfig.dataBase, JDBCMysqlConfig.user,
                JDBCMysqlConfig.password);
    }
}