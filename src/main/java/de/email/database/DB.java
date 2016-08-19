package de.email.database;

import com.google.api.services.gmail.model.Message;
import de.email.FullMessage;
import de.email.Inbox;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;


/**
 * Created by evansdb0 on 8/12/16.
 *
 * @author Daniel Evans
 */
public class DB {

    // ----------------- ALL UPDATE DB METHODS -----------------------------

    public static void updateDatabase(Inbox inbox)
            throws IOException, SQLException {
        Connection conn = Conn.makeConnection();
        ResultSet resultSet = query(conn, "select date from mail order by date desc limit 1");
        String d = null;
        if (resultSet.next()) {
            d = resultSet.getString(1);
            System.out.println("First date out of email table " + d);
        } else
            return;
        // use this -> YYYY-mm-dd format because that is how the date was stored in MySQL
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-mm-dd");
        Date date = null;
        try {
            date = sdf.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("after:" + getLocalDate(date, 1, "yyyy/LLLL/dd"));
        List<Message> messages = inbox.listMessagesMatchingQuery
                ("after:" + getLocalDate(date, 1, "yyyy/LLLL/dd"));

        ResultSet rs = query(conn, "select id from mail");

        System.out.print("Saving new messages...");
        saveAllNewMessages(messages, rs, conn);
        System.out.println("Done. \nDeleting old messages");
        deleteAllOldMessages(messages, rs, conn);
    }

    private static String getLocalDate(Date date, int numDays, String format) {
        LocalDate d = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate numDaysBeforeNow = d.minusDays(numDays);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return numDaysBeforeNow.format(formatter);
    }

    private static void saveAllNewMessages(List<Message> messages,
                                           ResultSet rs, Connection conn) {
        System.out.println(messages.size());
    }

    private static void deleteAllOldMessages(List<Message> messages,
                                             ResultSet rs, Connection conn) {
        matches();
    }

    private static void matches() {

    }

    private static ResultSet query(Connection connection, String sql) throws SQLException {
        return connection.createStatement().executeQuery(sql);
    }

    // ------------------------- END UPDATE DB METHODS ----------------------------

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
        ResultSet rs = query("select count(id) from mail");
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
            System.out.print("DB connection successful. Loading " + messages.size() + " messages... ");
            for (int i = 0; i < messages.size(); i++) {
                FullMessage fm = new FullMessage(inbox, messages.get(i));
                insertInto(connection, fm);
            }
            System.out.print("Done.\n");
            System.out.println("Initialization time: " + ((System.currentTimeMillis() - initTime) / 1000));
            connection.close();
        } else {
            System.out.println("Updating and exiting...");
            updateDatabase(inbox);
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
            stmt = conn.prepareStatement("INSERT INTO email.mail (subject, snippet, body, id, fromEmail, fromName, date) " +
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

    private boolean messageExists(Connection conn, Message message, ResultSet rs) {
        System.out.print("Checking if message exists...");
        try {
            rs = query(conn, "SELECT id from mail");

            int i = 0;
            while (rs.next()) {
                if (rs.getString(1).equals(message.getId())) {
                    System.out.println("  Yes");
                    return true;
                }
            }
            System.out.println(" No");
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("  No");
        return false;
    }
}