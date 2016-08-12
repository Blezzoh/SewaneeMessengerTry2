package com.danielevans.email;

import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel Evans on 7/13/16.
 *
 * @author Daniel Evans
 */
public class Test {


    private static final String MESSAGE_ID = "1562ca4c4997819d";

    public static void main(String[] args) throws IOException, InterruptedException {

        Inbox inbox = new Inbox("iradub0@sewanee.edu");

        List<Message> messages = inbox.listMessages();

        try {
            new Email(inbox);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<FullMessage> initEmailData(Inbox inbox, List<Message> messages) {
        System.out.println("Test: initializing email data...");
        List<FullMessage> emailData = new ArrayList<>(messages.size() * 2);
        for (int i = 0; i < messages.size(); i++) {
            try {
                emailData.add(new FullMessage(inbox, messages.get(i)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return emailData;
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
