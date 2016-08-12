package com.danielevans.email;

import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.sql.*;
import java.util.List;

/**
 * Created by evansdb0 on 8/11/16.
 *
 * @author Daniel Evans
 */
public class Email {

    // COLUMN INDEXES FOR EACH COLUMN NAME
    private static int SUBJECT = 2;
    private static int SNIPPET = 3;
    private static int BODY = 4;
    private static int ID = 5;  // IMPORTANT: THE ACTUAL MESSAGE ID THAT CAME WITH THE Message object
    private static int FROM_EMAIL = 6;
    private static int FROM_NAME = 7;
    private static int DATE = 8;
    private static String MYSQL_DATE_PATTERN = "";

    private String fromEmail;
    private String date;
    private String subject;
    private String fromName;
    private String snippet;
    private String body;
    private String messageId;
    private long time;
    private long add;

    public Email(Inbox inbox) throws IOException, SQLException {
        List<Message> messages = inbox.getDefaultInbox();
        Connection con = Conn.makeConnection();

        int numRows = countNumRows(con);
        System.out.println("Number of Rows: " + numRows);


        if (numRows > 0 && numRows - messages.size() > 0) {
            // delete local emails
            System.out.println("difference in remote and local server emails");
        } else if (numRows > 0 && numRows - messages.size() < 0) {
            System.out.println("Diff = " + (numRows - messages.size()));
            for (int i = numRows; i < messages.size(); i++) {
                if (!messageExists(con, messages.get(i))) {
                    insertInto(con, new FullMessage(inbox, messages.get(i)));
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            // save remote emails
        } else {
            // do nothing :)
        }
        for (Message message : messages) {
            if (!messageExists(con, message)) {
                System.out.println("Fetching data from Google's servers");
                FullMessage fm = new FullMessage(inbox, message);
                insertInto(con, fm);
            }
        }
        initFields(con, messages.get(0));
    }

    private void initFields(Connection con, Message m) {
        ResultSet rs = null;  // 6 entries
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM mail WHERE id = ?");
            ps.setString(1, m.getId());
            ;
            rs = ps.executeQuery();
            while (rs.next()) {
                subject = rs.getString(SUBJECT);
                fromEmail = rs.getString(FROM_EMAIL);
                fromName = rs.getString(FROM_NAME);
                snippet = rs.getString(SNIPPET);
                body = Inbox.decodeString(rs.getString(BODY));
                messageId = rs.getString(ID);
                date = rs.getString(DATE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Email(Inbox inbox, Message m) throws IOException, SQLException {
        // initialize this Email with the corresponding data record from DB if exists
        // if it doesnt exist fetch FullMessage, set fields, and store record in DB
        Connection con = Conn.makeConnection();
        // m exists in the database
        if (messageExists(con, m)) {

            initFields(con, m);
        } else {  // m does not exists in the db
            System.out.println("Fetching data from Google's servers");
            FullMessage fm = new FullMessage(inbox, m);
            insertInto(con, fm);
            subject = fm.getSubject();
            snippet = fm.getSnippet();
            messageId = fm.getId();
            body = Inbox.decodeString(fm.getBodyBase64());
            fromEmail = fm.getFromEmail();
            fromName = fm.getFromName();
            EmailDate emailDate = new EmailDate(fm.getDate());
            date = emailDate.mysqlDate();
        }
        con.close();
    }

    private boolean messageExists(Connection conn, FullMessage fullMessage) {
        return messageExists(conn, fullMessage.getM());
    }

    private ResultSet query(Connection connection, String sql) throws SQLException {
        Statement q = connection.createStatement();
        return q.executeQuery(sql);
    }


    private boolean messageExists(Connection conn, Message message) {
        time = System.currentTimeMillis();
        System.out.print("Checking if message exists...");
        try {
            ResultSet rs = query(conn, "SELECT id from mail");

            int i = 0;
            while (rs.next()) {
                if (rs.getString(1).equals(message.getId())) {
                    System.out.println("  Yes");
                    add += System.currentTimeMillis() - time;
                    return true;
                }
            }
            System.out.println(" No");
            add += System.currentTimeMillis() - time;
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("  No");
        add += System.currentTimeMillis() - time;
        return false;
    }

    private int countNumRows(Connection con) {
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

    private boolean insertInto(Connection con, FullMessage fm) {
        EmailDate emailDate = new EmailDate(fm.getDate());
        return insertInto(con, fm.getSubject()
                , fm.getSnippet(), fm.getId(), fm.getBodyBase64()
                , fm.getFromEmail(), fm.getFromName()
                , emailDate.mysqlDate());
    }

    private boolean insertInto(Connection conn,
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
            System.out.println("Inserting data....");
            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Email(FullMessage fm) throws SQLException {
        subject = fm.getSubject();
        fromEmail = fm.getFromEmail();
        fromName = fm.getFromName();
        snippet = fm.getSnippet();
        body = Inbox.decodeString(fm.getBodyBase64());
        messageId = fm.getId();
        EmailDate emailDate = new EmailDate(fm.getDate());
        date = emailDate.getDefault();

        Connection con = Conn.makeConnection();
        // TODO: insert info into DB if not a message with an id of messageId
        if (!messageExists(con, fm)) {
            insertInto(con, subject, snippet, messageId,
                    fm.getBodyBase64(), fromEmail, fromName, emailDate.format("YYYY/MM/DD"));
        }
        con.close();
    }

    public String getSubject() {
        return subject;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public String getFromName() {
        return fromName;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }

    public String getMessageId() {
        return messageId;
    }
}
