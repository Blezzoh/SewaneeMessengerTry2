package de.email.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by daniel on 9/30/16.
 *
 * @author Daniel Evans
 */
public class DB {

    public static String lookup(String selectWhat,
                                String tblName,
                                String colName,
                                String compOp,
                                String checkAgainst) throws SQLException {


        Connection con = Conn.makeConnection();
        PreparedStatement ps = null;
        String retVal = "";
        if (compOp.equals("="))
            ps = con.prepareStatement("SELECT "
                    + selectWhat +
                    " from "
                    + tblName +
                    " where "
                    + colName + "= ?");
        if (ps != null) {
            ps.setString(1, checkAgainst);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                retVal = rs.getString(1);
        }
        con.close();
        return retVal;
    }

    public static ResultSet selectAll(String selectWhat,
                                      String modifier,
                                      String tblName) throws SQLException {

        Connection c = Conn.makeConnection();
        PreparedStatement ps = null;
        if (modifier == null)
            ps = c.prepareStatement("SELECT "
                    + selectWhat +
                    " from "
                    + tblName);
        else
            ps = c.prepareStatement("SELECT " + modifier + " "
                    + selectWhat +
                    " from "
                    + tblName);

        ResultSet rs = ps.executeQuery();
        return rs;
    }

    public static void createUserTable() {
        Connection c = null;
        try {
            c = Conn.makeConnection();
            Conn.execute(c, "CREATE TABLE IF NOT EXISTS " + Config.USER_TABLE + "\n" +
                    "(\n" +
                    "  id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                    "  username VARCHAR(500),\n" +
                    "  pass VARCHAR(500)\n" +
                    ")");
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // close connections to db
            try {
                assert c != null;
                c.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
}
