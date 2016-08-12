package com.danielevans.email;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import static com.danielevans.email.Inbox.MESSAGE_NULL_ERROR;

/**
 * Created by daniel on 6/2/16.
 *
 * @author Daniel Evans
 */
// TODO: detect links so we can send the user to that page
public class MessageParser {

    public static String getBodyTextFromHTML(String html) {
        return generateDocument(Inbox.decodeString(html)).text();
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
     * @return returns emailAddress@example.com
     */
    public static String parseEmailAddress(String emailAddress) {
        Preconditions.objectNotNull(emailAddress, "emailAddress is null");
        int start = emailAddress.indexOf('<');
        int end = emailAddress.indexOf('>', start) + 1;
        if (start == -1 || end == -1) return emailAddress;
        String str = emailAddress.substring(start, end);
        if (checkIsValidEmailAddress(str)) // primitive check for email address validity
            return emailAddress;
        else
            return str;
    }

    public static boolean checkIsValidEmailAddress(String emailAddress) {
        return emailAddress.contains("@") && emailAddress.contains(".") && emailAddress.length() > 7;
    }

    public static String stripEnds(String str) {
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
     * Precondition: from must be in the form NameOfPerson <emailAddress@example.com>
     *
     * @param from an email in form NameOfPerson <emailAddress@example.com>
     * @return returns NameOfPerson
     */
    static String parseNameFromEmail(String from) {
        int i = from.indexOf('<');
        if (i == -1) return from;
        return stripEnds(from.substring(0, i));
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
        Preconditions.objectNotNull(message, MESSAGE_NULL_ERROR);
        String messageAsHTML = null;
        try {
            messageAsHTML = message.getMessageBodyAsHTML();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // we succeeded in retrieving raw data from google servers
        if (messageAsHTML != null)
            return Jsoup.parse(messageAsHTML);
        return null;
    }
    /*public static String parseLinks(FullMessage m) {
*//**
     *  Document doc = null;
     try {
     doc = generateDocument(m);
     } catch (IOException e) {
     e.printStackTrace();
     }
     // message contained html
     if(doc != null) {

     }
     *//*
        String google = "http://www.google.com/search?q=";
        String search = parseNameFromEmail(m);
        String charset = "UTF-8";
        String userAgent = "ImageBot 1.0 https://github.com/Blezzoh/My_Sewanee_Messenger";
        Elements links = null;
        try {
            links = Jsoup.connect(google + URLEncoder.encode(search, charset)).userAgent(userAgent).get().select(".g>.r>a");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (links != null) {

        }
    }*/
}
