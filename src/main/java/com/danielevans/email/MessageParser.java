package com.danielevans.email;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by daniel on 6/2/16.
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
        return doc.select("img");
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
//            System.out.println("charIntVal " + charIntVal);
            //  ---- day -----
            // checks for null are so that the vars aren't reset after they've
            // been set
            if (dd == null) {
                // if a valid number is found
                if (charIntVal != -1) {
                    // we want # we are at and the next one
                    dd = date.substring(i, i + 2);
//                    System.out.println("day = " + dd);
                }
            }
            //  ---- month -----
            if (mm == null) {
//                System.out.println(date.substring(i, i+3));
                if (months.get(date.substring(i, i + 3)) != null) {
                    // months are 3 characters long, hence the i + 3
                    mm = months.get(date.substring(i, i + 3));
//                    System.out.println("month = " + mm);
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
        // 2004/04/16
        // yyyy/dd/mm
        return year + "/" + dd + "/" + mm;
    }

    /**
     *
     * @param imgElement expects an element of type img
     * @return the src attribute of the element or nothing if the element has no src attribute
     */
    public static String getImgSrc(Element imgElement) {

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
        return doc.select("img").first().attr("src");
    }

    public static String parseEmailAddress(String emailAddress) {
        String backwardEmailAddress = "";
        for (int i = emailAddress.length()-2; i >=0; i--) {
            if(emailAddress.charAt(i) !=  '<') {
                backwardEmailAddress += emailAddress.charAt(i);
            }
            else
                break;
        }
        return new StringBuilder(backwardEmailAddress).reverse().toString();
    }

    /**
     *
     * @param message Ay gmail message
     * @return All the a links in the email
     * @throws IOException
     */
    public static Elements getLinks(FullMessage message) throws IOException {

        Document doc = generateDocument(message);
        return doc.select("a");
    }

    /**
     *
     * @param linkElement expects an element of type a
     * @return the href attribute of the element or nothing if the element has no href attribute
     */
    public static String getHref(Element linkElement) {

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
        if(message == null) throw new NullPointerException("message can't be null");
        return Jsoup.parse(message.getMessageAsHTML());
    }
}
