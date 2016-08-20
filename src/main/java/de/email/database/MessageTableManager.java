package de.email.database;

import com.google.api.services.gmail.model.Message;
import de.email.FullMessage;
import de.email.Inbox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by evansdb0 on 8/12/16.
 *
 * @author Daniel Evans
 */
public class MessageTableManager {

    public static final String tableName = "email.message";

    // ----------------- ALL UPDATE MessageTableManager METHODS -----------------------------

    public static void updateMessageTable(Inbox inbox)
            throws IOException, SQLException {
        Connection conn = Conn.makeConnection();
        ResultSet resultSet = query(conn, "select date from " + tableName + " order by date desc limit 1");
        // get most recent date from database
        String d = null;
        if (resultSet.next()) {
            d = resultSet.getString(1);
        } else return;
        // get the date 1 day before d
        String ld = getLocalDate(d, 1, "yyyy/LLLL/dd");
        // search for messages on google's servers that are newer than ld
        List<Message> messages = inbox.listMessagesMatchingQuery("after:" + ld);

        // TODO: FIGURE OUT WHY THE DATE AND OTHER FIELDS IN DBMESSAGE ARE NULL

        // get messages as full messages and insert the them
        List<FullMessage> fms = new ArrayList<>(messages.size());
        for (Message message : messages)
            fms.add(new FullMessage(inbox, message));
        for (FullMessage fm : fms)
            insertInto(conn, fm);

    }

    private static String getLocalDate(String date, int numDays, String format) {
        LocalDate d = LocalDate.parse(date);
        System.out.println("local date " + d);
        LocalDate numDaysBeforeNow = d.minusDays(numDays);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return numDaysBeforeNow.format(formatter);
    }

    public static void createMessageTable() {
        Connection c = null;
        try {
            c = Conn.makeConnection();
            execute(c, "CREATE TABLE IF NOT EXISTS " + tableName + "\n" +
                    "(\n" +
                    "  id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                    "  subject VARCHAR(2000),\n" +
                    "  snippet VARCHAR(1000),\n" +
                    "  body LONGTEXT,\n" +
                    "  message_id VARCHAR(100) NOT NULL UNIQUE,\n" +
                    "  fromEmail VARCHAR(500),\n" +
                    "  fromName VARCHAR(500),\n" +
                    "  date DATE\n" +
                    ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveAllNewMessages(List<Message> messages,
                                           ResultSet rs, Connection conn) {
    }

    private static void deleteAllOldMessages(List<Message> messages,
                                             ResultSet rs, Connection conn) {
        matches();
    }

    private static void matches() {

    }

    private static boolean execute(Connection connection, String sql) throws SQLException {
        return connection.createStatement().execute(sql);
    }

    private static ResultSet query(Connection connection, String sql) throws SQLException {
        return connection.createStatement().executeQuery(sql);
    }

    // ------------------------- END UPDATE MessageTableManager METHODS ----------------------------

    private static int numRows(Connection con) {
        ResultSet rs = null;
        try {
            rs = query(con, "select count(id) from mail");
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static ResultSet query(String sql) throws SQLException {
        Connection connection = Conn.makeConnection();
        return query(connection, sql);
    }

    public static int numRows() throws SQLException {
        ResultSet rs = query("select count(id) from " + tableName);
        if (rs.next())
            return rs.getInt(1);
        return -1;
    }

    private static boolean update(List<Message> messages) throws SQLException {
        return numRows() != messages.size();
    }

    public static boolean update(Inbox inbox) throws SQLException {
        return update(inbox.getInbox());
    }

    public static void fillTable(Inbox inbox) throws IOException, SQLException {
        long initTime = System.currentTimeMillis();
        List<Message> messages = inbox.getInbox();
        if (numRows() == 0) {
            Connection connection = Conn.makeConnection();
            System.out.print("MessageTableManager connection successful. Loading " + messages.size() + " messages... ");
            for (int i = 0; i < messages.size(); i++) {
                FullMessage fm = new FullMessage(inbox, messages.get(i));
                insertInto(connection, fm);
            }
            System.out.print("Done.\n");
            System.out.println("Initialization time: " + ((System.currentTimeMillis() - initTime) / 1000));
            connection.close();
        }
    }

    public static void createTableFromOriginal() {

    }

    private static boolean insertInto(Connection con, FullMessage fm) {
        de.email.database.EmailDate emailDate = new de.email.database.EmailDate(fm.getDate());
        return insertInto(con, fm.getSubject()
                , fm.getSnippet(), fm.getId(), fm.getBodyBase64String()
                , fm.getFromEmail(), fm.getFromName()
                , emailDate.mysqlDate());
    }

    private static boolean insertInto(Connection conn,
                                      String subject,
                                      String snippet,
                                      String messageId,
                                      String body,
                                      String fromEmail,
                                      String fromName,
                                      String date) {


        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT IGNORE INTO " + tableName + " (subject, snippet, body, message_id, fromEmail, fromName, date) " +
                    "values (?, ?, ?, ?, ?, ?, ?)");
            stmt.setString(1, subject);
            stmt.setString(2, snippet);
            stmt.setString(3, body);
            stmt.setString(4, messageId);
            stmt.setString(5, fromEmail);
            stmt.setString(6, fromName);
            stmt.setString(7, date);
            stmt.executeUpdate();
            System.out.println("Inserting data....");

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}