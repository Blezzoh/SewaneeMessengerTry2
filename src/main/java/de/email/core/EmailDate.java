package de.email.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by evansdb0 on 8/12/16.
 *
 * @author Daniel Evans
 */
public class EmailDate {

    public static final String DEFAULT = "YYYY/mm/dd";
    // make a dictionary for the monthsName
    private static HashMap<String, String> monthsName = new HashMap<>(12);
    private static HashMap<String, String> monthsNumber = new HashMap<>(12);
    private String month;
    private String day;
    private String year;
    private String hour;
    private String minute;
    private String second;

    public EmailDate(String date) {
        // Handles this format -> Wed, 13 Jul 2016 21:01:22 -0000
        parseDate(date);
    }

    public static String getMonthName(String monthNum) {
        initMonthsNumber();
        return monthsNumber.get(monthNum);
    }

    public static String getMonthNameAbbreviated(String monthNum) {
        if (getMonthName(monthNum).equals("May"))
            return getMonthName(monthNum);
        else
            return getMonthName(monthNum).substring(0, 3) + ".";
    }

    private static void initMonthsName() {
        monthsName.put("Jan", "01");
        monthsName.put("Feb", "02");
        monthsName.put("Mar", "03");
        monthsName.put("Apr", "04");
        monthsName.put("May", "05");
        monthsName.put("Jun", "06");
        monthsName.put("Jul", "07");
        monthsName.put("Aug", "08");
        monthsName.put("Sep", "09");
        monthsName.put("Oct", "10");
        monthsName.put("Nov", "11");
        monthsName.put("Dec", "12");
    }

    private static void initMonthsNumber() {
        monthsNumber.put("01", "January");
        monthsNumber.put("02", "February");
        monthsNumber.put("03", "March");
        monthsNumber.put("04", "April");
        monthsNumber.put("05", "May");
        monthsNumber.put("06", "June");
        monthsNumber.put("07", "July");
        monthsNumber.put("08", "August");
        monthsNumber.put("09", "September");
        monthsNumber.put("10", "October");
        monthsNumber.put("11", "November");
        monthsNumber.put("12", "December");

        monthsNumber.put("1", "January");
        monthsNumber.put("2", "February");
        monthsNumber.put("3", "March");
        monthsNumber.put("4", "April");
        monthsNumber.put("5", "May");
        monthsNumber.put("6", "June");
        monthsNumber.put("7", "July");
        monthsNumber.put("8", "August");
        monthsNumber.put("9", "September");
    }

    public String mysqlDate() {
        return format("YYYY-MM-dd");
    }

    public String getDefault() {
        StringBuilder sb = new StringBuilder();
        // builds this format: MM/dd/yyyy HH:mm:ss
        return month == null ? null : sb.append(month).append("/").append(day)
                .append("/").append(year).append(" ").append(hour)
                .append(":").append(minute).append(":").append(second).toString();
    }

    /**
     * COULD MAKE THIS MORE USEFUL BY ADDING PARAMETERS FOR LENGTH OF A
     * YEAR, THE FORMAT OF THE MONTH (January vs. Jan vs jan vs january), etc
     * Precondition: date is of format -----> Wed, 13 Jul 2016 21:01:22 -0000
     *
     * @param date date in format of precondition specified above
     * @return returns a date in format yyyy/dd/mm or yyyy-mm-dd
     */
    public void parseDate(String date) {
        Preconditions.objectNotNull(date, "date is null");
        initMonthsName();

        // set the proper value for i, depends on if there is
        // a day in front of date i.e. if there is a Wed. or Mon.
        // in front of the rest of the date
        int i;
        int dl = date.length();
        if (dl > 3 && Character.isLetter(date.charAt(0))) i = 3;
        else i = 0;

        for (; i + 5 < date.length(); i++) {
            // this will not be -1 if it is a valid number
            int charIntVal = Character.getNumericValue(date.charAt(i));
            //  ---- day -----
            // checks for null are so that the vars aren't reset after they've
            // been set
            if (day == null) {
                // if a valid number is found
                if (i + 2 < dl && charIntVal != -1) {
                    // we want # we are at and the next one
                    day = date.substring(i, i + 2);
                    if (day.contains(" ")) day = "0" + day.trim();
                }
                }
            //  ---- month -----
            if (month == null) {
                if (i + 3 < dl && monthsName.get(date.substring(i, i + 3)) != null) {
                    // monthsName are 3 characters long, hence the i + 3
                    month = monthsName.get(date.substring(i, i + 3));
                }
                }
            // ----- year  -----
            if (year == null) {
                try {
                    if (i + 4 < dl) {
                        // cheintcking to see if we throw exception; if not, we found the year
                        Integer.parseInt(date.substring(i, i + 4));
                        // only get here if we don't throw, years are (assumed) 4 chars long
                        year = date.substring(i, i + 4);
                    }
                } catch (NumberFormatException e) { /* do nothing here */ }
            }
            // hour, minute and second
            if (year != null) {
                i += 5;
                if (i < dl) {
                    String time = date.substring(i, i + 8);
                    hour = time.substring(0, 2);
                    minute = time.substring(3, 5);
                    second = time.substring(6, 8);
                    break;
                }
            }
            }
    }

    private Date defaultDate() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String aDefault = getDefault();
        return aDefault == null ? null : formatter.parse(aDefault);
    }

    public String slashDate() {
        StringBuilder sb = new StringBuilder(20);
        return sb.append(year).append("/").append(month).append("/").append(day).toString();
    }

    // YYYY-MM-DD HH:MM:SS
    public String format(String pattern) {
        Date date = null;
        try {
            date = defaultDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(date);
        SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern);
        return date == null ? "Date not available" : dateFormatter.format(date);
    }

}