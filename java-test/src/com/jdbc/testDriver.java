package com.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * testDriver
 */
public class testDriver {

    public Connection getConnection() throws SQLException {
        Driver driver = (Driver) Class.forName(JDBCMysqlConfig.driver).newInstance();

        Properties info = new Properties();
        info.put("user", JDBCMysqlConfig.user);
        info.put("password", JDBCMysqlConfig.password);

        return driver.connect(JDBCMysqlConfig.url + JDBCMysqlConfig.dataBase, info);
    }
}