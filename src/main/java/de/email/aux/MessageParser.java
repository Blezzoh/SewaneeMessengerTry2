package de.email.aux;

import de.email.core.FullMessage;
import de.email.core.Preconditions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by daniel on 6/2/16.
 *
 * @author Daniel Evans
 */
// TODO: detect links so we can send the user to that page
public class MessageParser {

    public static void main(String[] args) {
        String ea = "<this>";
        System.out.println(MessageParser.parseEmailAddress(ea));
    }

    public static String getTextFromHTML(String html) {
        return generateDocument(html).text();
    }

    private static Document generateDocument(String html) {
        return Jsoup.parse(html);
    }

    /**
     * @param message Any gmail message
     * @return all img tags in message
     * @throws IOException
     */
    public static Elements getImgs(FullMessage message) throws IOException {
        Document doc = generateDocument(message);
        if (doc != null)
            return doc.select("img");
        return null;
    }

    public static <T> void checkDuplicatesInArray(T[] arr) {
        for (int j = 0; j < arr.length; j++) {
            for (int k = j + 1; k < arr.length; k++) {
                if (arr[j] != null && arr[j].equals(arr[k]))
                    System.out.println("j = " + j + "  " + "k = " + k + "  " + arr[j]);
            }
        }
    }


    /**
     *
     * @param imgElement expects an element of type img
     * @return the src attribute of the element or nothing if the element has no src attribute
     */
    public static String getImgSrc(Element imgElement) {
        Preconditions.objectNotNull(imgElement, "imgElement is null");
        return imgElement.attr("src");
    }

    /**
     * Precondition: from must be in the form NameOfPerson <emailAddress@example.com>
     *
     * @param from an email in form NameOfPerson <emailAddress@example.com>
     * @return returns NameOfPerson
     */
    public static String parseNameFromMessage(String from) {
        if (from == null) {
            return null;
        }
        int i = from.indexOf('<');
        if (i == -1) return from;
        return stripEnds(from.substring(0, i));
    }

    /**
     *
     * @param message Any gmail message
     * @return the src attribute of the first image in the email
     * @throws IOException
     */
    public static String getFirstImgSrc(FullMessage message) throws IOException {
        Document doc = generateDocument(message);
        if (doc != null)
            return doc.select("img").first().attr("src");
        return null;
    }

    /**
     * Precondition: from must be in the form NameOfPerson <emailAddress@example.com>
     *
     * @param emailAddress an email in form NameOfPerson <emailAddress@example.com>
     * @return returns emailAddress@example.com (see param)
     */
    public static String parseEmailAddress(String emailAddress) {
        Preconditions.objectNotNull(emailAddress, "emailAddress is null");
        int start = emailAddress.indexOf('<');
        int end = emailAddress.indexOf('>', start);
        if (start == -1 || end == -1) return emailAddress;

        return emailAddress.substring(start + 1, end);
    }

    public static boolean testForHTML(String strToTest) {
        String r1 = "</?\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[\\^'\">\\s]+))?)+\\s*|\\s*)/?>";
        Pattern p1 = Pattern.compile(r1);
        for (int i = 0; i + 1 < strToTest.length(); ) {
            int i1 = strToTest.indexOf("<", i);
            int i2 = strToTest.indexOf(">", i1);
            if (i1 == -1 || i2 == -1 || i2 == strToTest.length() - 1)
                return false;

            if (p1.matcher(strToTest.substring(i1, i2 + 1)).matches()) {
                return true;
            }
            i = i2 + 1;
        }
        return false;
    }

    private static String stripEnds(String str) {
        int start = 0;
        int end = str.length() - 1;
        for (int i = 0; i < str.length() && !Character.isLetter(str.charAt(i)); i++)
            /* is not an alphabetical character */
            ++start;
        for (int i = end; i >= 0 && !Character.isLetter(str.charAt(i)); --i)
            /* is not an alphabetical character */
            --end;

        return str.substring(start, end + 1);
    }

    /**
     *
     * @param message Ay gmail message
     * @return All the a links in the email
     * @throws IOException
     */
    public static Elements getLinks(FullMessage message) throws IOException {
        Document doc = generateDocument(message);
        if (doc != null)
            return doc.select("a");
        return null;
    }

    /**
     *
     * @param linkElement expects an element of type a
     * @return the href attribute of the element or nothing if the element has no href attribute
     */
    public static String getHref(Element linkElement) {
        Preconditions.objectNotNull(linkElement, "linkElement is null");
        return linkElement.attr("href");
    }

    /**
     * helper method for getting a parsable version of the email message
     * @param message Ay gmail message
     * @return Document representing the jsoup parsed version of the email message
     * @throws IOException
     */
    private static Document generateDocument(FullMessage message)
            throws IOException {
        Preconditions.objectNotNull(message, "message is null");
        String messageAsHTML = null;
        try {
            messageAsHTML = message.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // we succeeded in retrieving raw data from google servers
        if (messageAsHTML != null)
            return Jsoup.parse(messageAsHTML);
        return null;
    }
}
