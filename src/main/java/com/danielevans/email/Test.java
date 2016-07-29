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
        List<Message> messages = inbox.listMessagesMatchingQuery("sarah");

        FullMessage[] emailData = new FullMessage[messages.size()];



        System.out.println("Getting email data from servers....");
        for (int i = 0; i < emailData.length; i++) {
            try {
                emailData[i] = new FullMessage(inbox, messages.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Retrieved email data");
        int index = 0;
        for (int i = 0; i < emailData.length; i++) {
            if (emailData[i].getBestMessageBody().equals("========== AMAZON.COM ========== Amazon.com has new recommendations for you based on your browsing history. The Dawn of Software Engineering: from Turing to Dijkstra by Edgar G. Daylight https://www.amazon.com/gp/product/9491386026/ref=em_si_text Price: $30.00 ... The Science of Computing: Shaping a Discipline by Matti Tedre https://www.amazon.com/gp/product/1482217694/ref=em_si_text Price: $50.95 ... It Began with Babbage: The Genesis of Computer Science by Subrata Dasgupta https://www.amazon.com/gp/product/0199309418/ref=em_si_text Price: $36.95 ... The Princeton Companion to Applied Mathematics by Nicholas J. Higham https://www.amazon.com/gp/product/0691150397/ref=em_si_text List Price: $99.50 Price: $87.49 You Save: $12.01 (12%) ... The Annotated Turing: A Guided Tour Through Alan Turing's Historic Paper on Computability and the Turing Machine by Charles Petzold https://www.amazon.com/gp/product/0470229055/ref=em_si_text List Price: $29.99 Price: $15.81 You Save: $14.18 (47%) ... Information Theory: A Tutorial Introduction by James V. Stone https://www.amazon.com/gp/product/0956372856/ref=em_si_text List Price: $25.95 Price: $23.17 You Save: $2.78 (11%) ... The Nature of Technology: What It Is and How It Evolves by W. Brian Arthur https://www.amazon.com/gp/product/1416544062/ref=em_si_text List Price: $16.00 Price: $13.53 You Save: $2.47 (15%) ... The Master Algorithm: How the Quest for the Ultimate Learning Machine Will Remake Our World by Pedro Domingos https://www.amazon.com/gp/product/0465065708/ref=em_si_text List Price: $29.99 Price: $19.94 You Save: $10.05 (34%) ... See even more similar items https://www.amazon.com/gp/yourstore/ref=em_fl ========== Amazon.com http://www.amazon.com/ref=pe_footer/ Connect with Us On Facebook https://www.amazon.com/gp/redirect.html/ref=sef_fb/?location=https://www.facebook.com/Amazon&token=E0915379AEBDF40D2C90D4882003C7011F43D80C On Twitter http://www.amazon.com/gp/redirect.html/ref=sef_twttr/?location=http://twitter.com/amazon&token=7A1A4AE8F6CE0BD277D8295E58702D283F329C0F On Pinterest http://www.amazon.com/gp/redirect.html/ref=sef_pintrst/?location=http://pinterest.com/Amazon&token=9F58B366258E1A8B5259E9BEF3482E02341F42D3 ========== We hope you found this message to be useful. However, if you'd rather not receive future e-mails of this sort from Amazon.com, please visit the opt-out link here: http://www.amazon.com//gp/gss/o/1pPwPm1ycBeIKIW1..e8PV7flhI3C3yyuELtlbU-ONIiHc3b4.--CcFzIJ.PqYiCb Please note that product prices and availability are subject to change. Prices and availability were accurate at the time this newsletter was sent; however, they may differ from those you see when you visit Amazon.com. © 2016 Amazon.com, Inc. or its affiliates. All rights reserved. Amazon, Amazon.com, the Amazon.com logo and 1-Click are registered trademarks of Amazon.com, Inc. or its affiliates. Amazon.com, 410 Terry Avenue N., Seattle, WA 98109-5210. Reference: 201801500 Please note that this message was sent to the following e-mail address: iradub0@sewanee.edu")) {
                index = i;
                break;
            }
        }
        Message m = emailData[index].getM();
        System.out.println(m.getId());
        System.out.println(m.getPayload().toPrettyString());
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
