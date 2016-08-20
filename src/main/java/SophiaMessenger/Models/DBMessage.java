package SophiaMessenger.Models;

import com.google.api.services.gmail.model.Message;
import de.email.FullMessage;
import de.email.MessageParser;
import de.email.database.Conn;
import de.email.database.EmailDate;
import de.email.interfaces.Mail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static de.email.database.MessageTableManager.tableName;

/**
 * Created by evansdb0 on 8/11/16.
 *
 * @author Daniel Evans
 */
public class DBMessage implements Mail {

    // TODO: 8/12/16 TURN THIS INTO AN ENUM CLASS
    // COLUMN INDEXES FOR EACH COLUMN NAME
    public static int SUBJECT = 2;
    public static int SNIPPET = 3;
    public static int BODY = 4;
    public static int MESSAGE_ID = 5;  // IMPORTANT: THE ACTUAL MESSAGE ID THAT CAME WITH THE DBMessage object
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
     * useful for getting a database record using a full message
     * @throws IOException  if there is a problem retrieving emails from Google servers
     * @throws SQLException if there is a problem with the connection
     */
    public DBMessage(FullMessage fm) throws IOException, SQLException {
        this(fm.getId());
    }

    /**
     * useful when we want to retrieve email data from MessageTableManager
     * using a list of messages
     *
     * @param m a message from google servers usually retrieved using
     *          inbox.getInbox() or inbox.listMessagesMatchingQuery(String)
     * @throws IOException
     * @throws SQLException
     */
    public DBMessage(Message m) throws SQLException {
        this(m.getId());
    }

    private DBMessage(String mId) throws SQLException {
        Connection con = null;
        try {
            con = Conn.makeConnection();
            ResultSet rs = null;  // 6 entries
            PreparedStatement ps = con.prepareStatement("SELECT * FROM " + tableName + " WHERE message_id = ?");
            System.out.println(mId);
            ps.setString(1, mId);
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println(fromEmail);
                initFieldsWithDBRecord(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // close db connection
        if (con != null) con.close();
    }

    /**
     *
     * @param rs Precondition: Must be a Result Set retrieved with "select * from message ..."
     * @throws SQLException
     */
    private void initFieldsWithDBRecord(ResultSet rs) throws SQLException {
        subject = rs.getString(SUBJECT);
        fromEmail = rs.getString(FROM_EMAIL);
        fromName = rs.getString(FROM_NAME);
        snippet = rs.getString(SNIPPET);
        body = rs.getString(BODY);
        messageId = rs.getString(MESSAGE_ID);
        date = rs.getString(DATE);
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
        return MessageParser.parseEmailAddress(fromEmail);
    }

    public String getFromName() {
        return MessageParser.parseNameFromMessage(fromName);
    }

    public String getSnippet() {
        return snippet;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String getId() {
        return messageId;
    }

    public String getDate() {
        System.out.println(date);
        return date;
    }

    public String getMessageId() {
        return messageId;
    }
}