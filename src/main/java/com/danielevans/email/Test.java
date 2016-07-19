package com.danielevans.email;

import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.List;

/**
 * Created by Daniel Evans on 7/13/16.
 * @author Daniel Evans
 */
public class Test {


    public static void main(String[] args) throws IOException {
        /**
         *    inbox gives access to the user's gmail messages using an authenticator
         */
        Inbox inbox = new Inbox(new Authenticator("evansdb0@sewanee.edu"));
        /**
         *  loads the user's inbox
         */
        List<Message> messages = inbox.getDefaultInbox();

        FullMessage[] emailData = new FullMessage[messages.size() / 2];



        System.out.println("Getting email data from servers....");
        long initTime = System.currentTimeMillis();
        for (int i = 0; i < emailData.length; i++) {
            try {
                emailData[i] = new FullMessage(inbox, messages.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            for (int i1 = 0; i1 < emailData.length; i1++) {
                System.out.println("ITH = " + i1);
                System.out.println(emailData[i1].getMessageBodyAsHTML());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Retrieved email data");


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
    // trying to create dummy data class to save json from repsonce to files
    // so that I can use GSON to read from the file and create json object of the email

    // IMPORTANT:
    // BUT PAYLOAD IS OF TYPE MESSAGE PART, SO THEORETICALLY I COULD USE IT AS THE CLASS
    // TO WRITE TO FROM THE FILE WITH GSON. IF SO NO NEED TO CREATE DUMMY DATA CLASS
    // AND ALL THAT NEEDS TO BE DONE IS TO SAVE THE m.getPayload().toPrettyString()
    // OF EVERY MESSAGE TO THE HARDRIVE ON INSTALL. THEN WHEN THE USER LOADS THE
    // APPLICATION, WE CHECK TO SEE IF ANY NEW MESSAGES HAVE BEEN SENT OR RECEIVED
    // BY THE USER (WE ALSO NEED TO DO THIS PERIODICALLY IN THE BACKGROUND
    // WHILE THE APP IS RUNNING) SO THAT THE APPLICATION STAYS UP TO DATE WITH
    // WHAT IS ON GOOGLE'S SERVERS.

   /* class EmailMessage {

        String id;
        String threadId;
        List<String> labelIds;
        String snippet;
        long historyId;
        long internalDate;
        MessagePart payload;
        String partId


        {
            "id": string,
                "threadId": string,
                "labelIds": [
            string
            ],
            "snippet": string,
                "historyId": unsigned long,
            "internalDate": long,
            "payload": {
            "partId": string,
                    "mimeType": string,
                    "filename": string,
                    "headers": [
            {
                "name": string,
                    "value": string
            }
            ],
            "body": users.messages.attachments Resource,
            "parts": [
            (MessagePart)
            ]
        },
            "sizeEstimate": integer,
                "raw": bytes
        }
    }

    */
}
