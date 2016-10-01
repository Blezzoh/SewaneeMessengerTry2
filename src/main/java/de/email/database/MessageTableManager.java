package de.email.database;

import com.google.api.services.gmail.model.Message;
import de.email.core.EmailDate;
import de.email.core.FullMessage;
import de.email.core.Inbox;

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
 * @author Daniel Evans
 */
public class MessageTableManager {

    public static final String tableName = "email.message";

    // ----------------- ALL UPDATE MessageTableManager METHODS -----------------------------

    public static void updateMessageTable(Inbox inbox, boolean before)
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
        // minimize the messages to insert by taking the date closest to now
        List<Message> messages = inbox.listMessagesMatchingQuery("after:" + ld);

        // eliminate duplicate messages before retrieving email data (FullMessages) b/c that is expensive
        if (messages.size() != 0)
            messages = checkAgainstDBMessages(conn, messages);

        System.out.println("message update size = " + messages.size());

        List<Message> beforeDateMessages = null;

        long time = System.currentTimeMillis();
        if (before) {
            beforeDateMessages = inbox.listMessagesMatchingQuery
                    ("before:" + getLocalDate(d, -1, "yyyy/LLLL/dd"));
            if (beforeDateMessages.size() != 0) {
                beforeDateMessages = checkAgainstDBMessages(conn, beforeDateMessages);
                System.out.println("m.size after check " + beforeDateMessages.size());
            }
        }

        // TODO: FIGURE OUT WHY THE DATE AND OTHER FIELDS IN DBMESSAGE ARE NULL
        // TODO: delete all old messages in database, right now old messages are not shown
        // TODO: the messages shown are in line with w/ messages from the inbox.getInbox() call, but they are still in the database
        // constraint on db to disallow duplicate message ids so it
        // will only insert messages that are not already in db
        // get messages as full messages and insert the them
        System.out.println("Gathering email data....");
        List<FullMessage> fms = new ArrayList<>(messages.size());

        for (Message message : messages)
            fms.add(new FullMessage(inbox, message));
        for (FullMessage fm : fms)
            insertInto(conn, fm);

        if (before) {
            fms = new ArrayList<>(beforeDateMessages.size());
            for (Message message : beforeDateMessages)
                fms.add(new FullMessage(inbox, message));
            for (FullMessage fm : fms)
                insertInto(conn, fm);
        }

        conn.close();
        System.out.println("Messages loaded.");
        System.out.println("update time " + (System.currentTimeMillis() - time));
        // todo: update views w/ new data ?????????
    }

    public static int numberOfMatchingMessages(List<Message> messages) throws SQLException {
        Connection con = null;
        try {
            con = Conn.makeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int count = 0;
        for (Message message : messages) {
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                assert con != null;
                ps = con.prepareStatement("SELECT * FROM " + tableName + " WHERE message_id = ?");
                ps.setString(1, message.getId());
                rs = ps.executeQuery();
                if (rs.next()) ++count;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (con != null)
            con.close();
        return count;
    }


    private static List<Message> checkAgainstDBMessages(Connection conn, List<Message> messages) {
        List<Message> nonDuplicateMessages = new ArrayList<>(messages.size());
        try {
            ResultSet rs;
            int i = 0;
            while (i < messages.size()) {
                PreparedStatement ps = conn.prepareStatement("SELECT COUNT(message_id) FROM message WHERE message_id = ?");
                ps.setString(1, messages.get(i).getId());
                rs = ps.executeQuery();
//                rs = query(conn, "select count(message_id) from message where message_id = " + messages.get(i).getId());
                if (rs.next())
                    if (rs.getInt(1) == 0) {

                        nonDuplicateMessages.add(messages.get(i));
                    }
                ++i;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nonDuplicateMessages;
    }

    private static String getLocalDate(String date, int numDays, String format) {
        LocalDate d = LocalDate.parse(date);
        LocalDate numDaysBeforeNow = d.minusDays(numDays);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return numDaysBeforeNow.format(formatter);
    }

    public static void createMessageTable() {
        Connection c = null;
        try {
            c = Conn.makeConnection();
            Conn.execute(c, "CREATE TABLE IF NOT EXISTS " + tableName + "\n" +
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

    private static ResultSet query(Connection connection, String sql) throws SQLException {
        return connection.createStatement().executeQuery(sql);
    }

    // ------------------------- END UPDATE MessageTableManager METHODS ----------------------------

    private static int numRows(Connection con) {
        ResultSet rs = null;
        try {
            rs = query(con, "select count(id) from message");
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
        return numRows() <= messages.size();
    }

    public static boolean update(Inbox inbox) throws SQLException {
        return update(inbox.getInbox());
    }

    public static void fillTable(Inbox inbox) throws IOException, SQLException {
        long initTime = System.currentTimeMillis();
        Connection connection = Conn.makeConnection();
        if (numRows(connection) == 0) {
            List<Message> messages = inbox.getInbox();
            System.out.print("MessageTableManager connection successful. Loading " + messages.size() + " messages... ");
            for (Message message : messages) {
                FullMessage fm = new FullMessage(inbox, message);
                insertInto(connection, fm);
            }
            System.out.println("Messages loaded.");
        }
        if (connection != null) {
            connection.close();
        }
    }

    public static void createTableFromOriginal() {

    }

    private static boolean insertInto(Connection con, FullMessage fm) {
        EmailDate emailDate = new EmailDate(fm.getDate());
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