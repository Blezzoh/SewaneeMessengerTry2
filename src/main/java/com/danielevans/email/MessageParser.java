package com.danielevans.email;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

import static com.danielevans.email.Inbox.MESSAGE_NULL_ERROR;
import static com.danielevans.email.Inbox.decodeString;

/**
 * Created by daniel on 6/2/16.
 * @author Daniel Evans
 */
// TODO: detect links so we can send the user to that page
public class MessageParser {

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

    /**
     * COULD MAKE THIS MORE USEFUL BY ADDING PARAMETERS FOR LENGTH OF A
     * YEAR, THE FORMAT OF THE MONTH (January vs. Jan vs jan vs january), etc
     * Precondition: date is of format Wed, 13 Jul 2016 21:01:22 -0000
     *
     * @param date
     * @return returns a date in format yyyy/dd/mm
     */
    public static String parseDate(String date) {
        Preconditions.objectNotNull(date, "date is null");
        // make a dictionary for the months
        HashMap<String, String> months = new HashMap<>(12);
        months.put("Jan", "01");
        months.put("Feb", "02");
        months.put("Mar", "03");
        months.put("Apr", "04");
        months.put("May", "05");
        months.put("Jun", "06");
        months.put("Jul", "07");
        months.put("Aug", "08");
        months.put("Sep", "09");
        months.put("Oct", "10");
        months.put("Nov", "11");
        months.put("Dec", "12");

        String dd = null;
        String mm = null;
        String year = null;
        for (int i = 3; i + 5 < date.length(); i++) {
            // this will not be -1 if it is a valid number
            int charIntVal = Character.getNumericValue(date.charAt(i));
            //  ---- day -----
            // checks for null are so that the vars aren't reset after they've
            // been set
            if (dd == null) {
                // if a valid number is found
                if (charIntVal != -1) {
                    // we want # we are at and the next one
                    dd = date.substring(i, i + 2);
                }
            }
            //  ---- month -----
            if (mm == null) {
                if (months.get(date.substring(i, i + 3)) != null) {
                    // months are 3 characters long, hence the i + 3
                    mm = months.get(date.substring(i, i + 3));
                }
            }
            // ----- year  -----
            if (year == null) {
                try {
                    // checking to see if we throw exception; if not, we found the year
                    System.out.println(Integer.parseInt(date.substring(i, i + 4)));
                    // only get here if we don't throw, years are (assumed) 4 chars long
                    year = date.substring(i, i + 4);
                    break;
                } catch (NumberFormatException e) { /* do nothing here */ }
            }
        }
        // yyyy/dd/mm
        return year + "/" + dd + "/" + mm;
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
        int end = emailAddress.indexOf('>', start) + 1;
        if (start == -1 || end == -1) return "";
        return emailAddress.substring(start, end);
    }

    public static String parseNameFromEmail(FullMessage message) {
        Preconditions.objectNotNull(message, MESSAGE_NULL_ERROR);
        return parseNameFromEmail(message.getFrom());
    }

    /**
     * Precondition: from must be in the form NameOfPerson <emailAddress@example.com>
     *
     * @param from an email in form NameOfPerson <emailAddress@example.com>
     * @return returns NameOfPerson
     */
    static String parseNameFromEmail(String from) {
        int i = from.indexOf('<');
        if (i == -1) return parseEmailAddress(from);
        return from.substring(0, i);
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
     * @param message Any Gmail message
     * @return the text in the body of message
     * @throws IOException
     */
    public static String getMessageBody(FullMessage message)
            throws IOException {
        Preconditions.objectNotNull(message, MESSAGE_NULL_ERROR);
        // print message body
        return decodeString(m.getPayload().getParts().get(0).getBody().getData());
    }

    /**
     * helper method for getting a parsable version of the email message
     * @param message Ay gmail message
     * @return Document representing the jsoup parsed version of the email message
     * @throws IOException
     */
    private static Document generateDocument(FullMessage message)
            throws IOException {
        if(message == null) throw new NullPointerException("message can't be null");
        String messageAsHTML = message.getMessageAsHTML();
        // we succeeded in retrieving raw data from google servers
        if (messageAsHTML != null)
            return Jsoup.parse(messageAsHTML);
        return null;
    }
}
