package de.email.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static SophiaMessenger.Config.*;

/**
 * Created by evansdb0 on 8/11/16.
 *
 * @author Daniel Evans
 */
public class Conn {

    public static Connection makeConnection() throws SQLException {
        return DriverManager.getConnection(SERVER_AND_PORT
                + DATABASE_NAME + URL_PARAMS, USERNAME, PASS);
    }
}