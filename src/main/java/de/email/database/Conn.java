package de.email.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by evansdb0 on 8/11/16.
 *
 * @author Daniel Evans
 */
public class Conn {

    private static final String SERVER_AND_PORT = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_NAME = "email";
    private static final String URL_PARAMS = "?autoReconnect=true&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASS = "Scourts14?!";

    public static Connection makeConnection() throws SQLException {
        return DriverManager.getConnection(SERVER_AND_PORT
                + DATABASE_NAME + URL_PARAMS, USERNAME, PASS);
    }
}