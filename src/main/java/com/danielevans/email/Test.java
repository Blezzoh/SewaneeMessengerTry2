package com.danielevans.email;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * Created by Daniel Evans on 7/13/16.
 *
 * @author Daniel Evans
 */
public class Test {


    private static final String MESSAGE_ID = "1562ca4c4997819d";

    public static void main(String[] args) throws IOException, InterruptedException {

        Inbox inbox = new Inbox("iradub0@sewanee.edu");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd");
        String date = sdf.format(Date.from(Instant.now()));
        System.out.println("after:" + date);
        inbox.listMessagesMatchingQuery("after:" + sdf.format(Date.from(Instant.now())));
//            DB.fillTable(inbox);
    }


    // -----------------     BENCHMARKING IF DESIRED -----------------------------------
        /*

        long time = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            MessageParser.parseNameFromEmail(emailData[30]);
        }
        System.out.println(System.currentTimeMillis() - time);

        time = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            MessageParser.parseNameFromEmail2(emailData[30].getFrom());
        }
        System.out.println(System.currentTimeMillis() - time);

        */
}
