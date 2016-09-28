package de.email.aux;

import com.google.api.services.gmail.model.Message;
import de.email.core.Inbox;
import de.email.database.Conn;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static de.email.database.Conn.execute;

/**
 * Created by evansdb0 on 7/18/16.
 *
 * @author Daniel Evans
 */
public class ImageBot {

    private static final String imageTable = "imageLookup";
    private Connection conn;

    public ImageBot() throws SQLException {
        conn = Conn.makeConnection();
    }


    public static void main(String[] args) throws IOException {
        Inbox inbox = new Inbox("evansdb0@sewanee.edu");
        List<Message> ms = inbox.getInbox();
        String ea = "<notification+kjdpik5jhvjd@facebookmail.com>";
//        System.out.println(ea.charAt(findLastDot(ea)) + "  " + ea.length());
//        System.out.println(getEmailSuffix(ea));
        System.out.println("CREATE TABLE IF NOT EXISTS " + imageTable + "(\n" +
                "  id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                "  suffix VARCHAR(1000),\n" +
                "  url    VARCHAR(1000))");
        /*for(int i =0; i < 30; i++)
            System.out.println(parseSenderImage(new FullMessage(inbox, ms.get(i))));*/
    }

    public static void createLookupTable(Connection c) {
        try {
            execute(c, "CREATE TABLE IF NOT EXISTS " + imageTable + "(\n" +
                    "  id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
                    "  suffix VARCHAR(1000),\n" +
                    "  url    VARCHAR(1000))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * the suffix is the portion of the email address that is
     * between the @ symbol and the .xxx character sequence
     *
     * @param ea the email address to get the suffix of
     * @return returns the suffix of ea
     */
    public String getEmailSuffix(String ea) {
        if (ea.length() == 0) return null;
        // get the suffix between @ and .com (or other)
        int at = findLastSymbol(ea, '@') + 1;
        int lastDot = findLastSymbol(ea, '.');
        if (lastDot == -1) return ea.substring(at);
        System.out.println("ea = " + ea);
        String eaSub = ea.substring(at, lastDot);
        // remove mail or Mail from the address
        if (eaSub.contains("mail"))
            eaSub = eaSub.replace("mail", "");
        if (eaSub.contains("Mail"))
            eaSub = eaSub.replace("Mail", "");

        // if remains dots, remove all remaining dots
        if (eaSub.contains(".")) {
            lastDot = findLastSymbol(eaSub, '.') + 1;
            return eaSub.substring(lastDot);
        }
        return eaSub;
    }

    /**
     * @param emailAddress the email address to search for sym
     * @param sym          a character in emailAddress to search for
     * @return the index of the last occurrence of sym in emailAddress
     * or -1 if it is not in emailAddress
     */
    private int findLastSymbol(String emailAddress, char sym) {
        int symi = 0;
        String ea = emailAddress;
        if (ea.contains(String.valueOf(sym))) {
            for (int i = 1; i < ea.length(); i++)
                if (ea.charAt(i) == sym)
                    symi = i;
            return symi;
        }
        return -1;
    }

    public String lookupURL(String suffix) throws SQLException {
        PreparedStatement statement = conn.prepareStatement
                ("SELECT url FROM " + imageTable + " WHERE suffix = ?");
        statement.setString(1, suffix);
        ResultSet rs = statement.executeQuery();
        if (rs.next())
            return rs.getString(1);
        return null;
    }

    public void store(String suffix, String url) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT IGNORE INTO " + imageTable + " (suffix, url) " +
                    "values (?, ?)");
            stmt.setString(1, suffix);
            stmt.setString(2, url);
            stmt.executeUpdate();
            conn.close();
            System.out.println("Inserting data....");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String parseSenderImage(String emailAddress) {
        final String suffix = getEmailSuffix(emailAddress);
        // Lookup the url to save time
        try {
            String url = lookupURL(suffix);
            if (url != null)
                return url;
        } catch (SQLException e) {
            System.out.println("Unable to lookup image");
            e.printStackTrace();
        }

        final String google = "http://www.images.google.com/search?q=";
        final String search = suffix + " logo";
        System.out.println("search = " + search);
        final String charset = "UTF-8";
        final String userAgent = "Mozilla/5.0";
        Document doc = null;
        try {
            doc = Jsoup.connect(google + URLEncoder.encode(search, charset))
                    .userAgent(userAgent).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc != null) {
            Elements elements = doc.body().select("*");
            doc = Jsoup.parse(elements.get(1).attr("href"));
            boolean first = true;
            String link = null;
            // search for elements in the DOM that have as e.ownText() == "Images",
            // the second element will always be our link to the image version of our search
            for (Element e : elements) {
                if (e.ownText().equals("Images")) {
                    if (!first)
                        link = e.attr("href");
                    first = false;
                }
            }
            try {
                doc = Jsoup.connect(link).userAgent(userAgent).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (doc != null) {
                Elements imgs = doc.getAllElements().select("img");
                String url = imgs.get(0).attr("src");
                try {
                    store(suffix, url);
                } catch (SQLException e) {
                    System.out.println("Unable to store image");
                    e.printStackTrace();
                }
                return url;
            }
        }
        return null;
    }
}