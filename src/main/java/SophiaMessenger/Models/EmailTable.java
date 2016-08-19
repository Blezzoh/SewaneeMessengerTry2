package SophiaMessenger.Models;

import com.google.api.services.gmail.model.Message;
import de.email.FullMessage;
import de.email.Inbox;
import de.email.database.Conn;
import de.email.database.EmailDate;

import java.io.IOException;
import java.sql.*;

/**
 * Created by evansdb0 on 8/11/16.
 *
 * @author Daniel Evans
 */
public class EmailTable {

    // TODO: 8/12/16 TURN THIS INTO AN ENUM CLASS
    // COLUMN INDEXES FOR EACH COLUMN NAME
    public static int SUBJECT = 2;
    public static int SNIPPET = 3;
    public static int BODY = 4;
    public static int MESSAGE_ID = 5;  // IMPORTANT: THE ACTUAL MESSAGE ID THAT CAME WITH THE Message object
    public static int FROM_EMAIL = 6;
    public static int FROM_NAME = 7;
    public static int DATE = 8;

    private String fromEmail;
    private String date;
    private String subject;
    private String fromName;
    private String snippet;
    private String body;
    private String messageId;

    /**
     * only use for first initialization of the table
     * this method will not check for duplication
     *
     * @throws IOException  if there is a problem retrieving emails from Google servers
     * @throws SQLException if there is a problem with the connection
     */
    private EmailTable(FullMessage fm) throws IOException, SQLException {
        this(fm.getM());
    }

    public EmailTable(Message m) throws IOException, SQLException {
        // initialize this Email with the corresponding data record from DB if exists
        // if it doesnt exist fetch FullMessage, set fields, and store record in DB
/*        PreparedStatement ps = con.prepareStatement("select * from email.mail where id = ?");
        System.out.println(m.getId());
        ps.setString(1, m.getId());
        ResultSet resultSet = ps.executeQuery();
        if(resultSet.next()) {
            System.out.println("look where i am");
            initFieldsWithDBRecord(resultSet);
        }*/

        Connection con = Conn.makeConnection();
        ResultSet rs = null;  // 6 entries
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM mail WHERE id = ?");
            ps.setString(1, m.getId());
            rs = ps.executeQuery();
            while (rs.next()) {
                subject = rs.getString(SUBJECT);
                fromEmail = rs.getString(FROM_EMAIL);
                fromName = rs.getString(FROM_NAME);
                snippet = rs.getString(SNIPPET);
                body = Inbox.decodeString(rs.getString(BODY));
                messageId = rs.getString(MESSAGE_ID);
                date = rs.getString(DATE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        con.close();
    }

    private void initFields(FullMessage fm) {
        subject = fm.getSubject();
        fromEmail = fm.getFromEmail();
        fromName = fm.getFromName();
        snippet = fm.getSnippet();
        body = fm.getBodyBase64String();
        messageId = fm.getId();
        EmailDate emailDate = new EmailDate(fm.getDate());
        date = emailDate.getDefault();
    }

    private void initFieldsWithDBRecord(ResultSet rs) throws SQLException {
        subject = rs.getString(SUBJECT);
        fromEmail = rs.getString(FROM_EMAIL);
        fromName = rs.getString(FROM_NAME);
        snippet = rs.getString(SNIPPET);
        body = rs.getString(BODY);
        messageId = rs.getString(MESSAGE_ID);
        date = rs.getString(DATE);
    }

    private ResultSet query(Connection connection, String sql) throws SQLException {
        Statement q = connection.createStatement();
        return q.executeQuery(sql);
    }

    private boolean checkMessageExists(Connection conn, Message m) throws SQLException {

        PreparedStatement ps = conn.prepareStatement("SELECT id from mail where id = ?");
        ps.setString(1, m.getId());
        ResultSet rs = ps.executeQuery(m.getId());
        int i = 0;
        while (rs.next()) {
            ++i;
        }
        return i == 1;
    }

    private boolean insertInto(Connection con, FullMessage fm) {
        EmailDate emailDate = new EmailDate(fm.getDate());
        return insertInto(con, fm.getSubject()
                , fm.getSnippet(), fm.getId(), fm.getBodyBase64String()
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