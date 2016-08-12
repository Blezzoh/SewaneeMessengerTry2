package com.danielevans.email;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by evansdb0 on 8/11/16.
 *
 * @author Daniel Evans
 */
public class Conn {

    private static final String DATABASE_NAME = "email";
    private static final String USERNAME = "root";
    private static final String PASS = "mydataDan";

    public static Connection makeConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DATABASE_NAME, USERNAME, PASS);
    }
}
