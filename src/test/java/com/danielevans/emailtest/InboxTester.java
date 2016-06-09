package com.danielevans.emailtest;

import com.danielevans.email.Authenticator;
import com.danielevans.email.Inbox;
import com.danielevans.email.LabelMaker;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.List;

/**
 * Created by d\janiel on 6/2/16.
 */
public class InboxTester {

    static Inbox i;
    static List<Message> m;

    public static void main(String[] args) throws IOException {


        i = new Inbox(new Authenticator("evansdb0"));

        m = i.getDefaultInbox();

        List<Label> labels = LabelMaker.listLabels(i);
        /*
        blaise loop through labels above and call method in
        sout below to get your label names
         */
        System.out.println(labels.get(0).getName());

//        System.out.println(MessageParser.parseEmailAddress(e));

/*        BatchRequest b = i.getService().batch();
//callback function. (Can also define different callbacks for each request, as required)
        JsonBatchCallback<Message> bc = new JsonBatchCallback<Message>() {

            @Override
            public void onSuccess(Message t, HttpHeaders responseHeaders)
                    throws IOException {
                FullMessage fullMessage = new FullMessage(i, t);
            }

            @Override
            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders)
                    throws IOException {

            }
        };

// queuing requests on the batch requests
        for (int i1 = 0; i1 < 100; i1++) {
            i.getService().users().messages().get("me", m.get(i1).getId()).queue(b, bc);
        }

        long time = System.currentTimeMillis();
        b.execute();
        System.out.println("searching for messages with only FullMessage creation took "
                + ((System.currentTimeMillis() - time) / 1000.0)
                + " seconds");

        time = System.currentTimeMillis();
        for (int j = 0; j < 100; j++) {
            FullMessage fullMessage = new FullMessage(i, m.get(j));
        }
        System.out.println("searching for messages with only FullMessage creation took "
                + ((System.currentTimeMillis() - time) / 1000.0)
                + " seconds");*/

/*        double times[] = new double[50];

        for (int j = 0; j < 50; j++) {
            avgTime(times,j);
        }
        System.out.println("\n\n\n\n\n\navg message\n\n\n\n" + sum(times));
        for (int j = 0; j < 50; j++) {
            avgMessageItem(times,j);
        }
        System.out.println("\n\n\n\n\n\navg messageItem\n\n\n\n" + sum(times));
    }
    public static void avgTime(double[] times, int index) throws IOException {
        long time = System.currentTimeMillis();
        for (int j = 0; j < 20; j++) {
            FullMessage fullMessage = new FullMessage(i, m.get(j));
        }
        times[index] = (System.currentTimeMillis() - time) / 1000.0;
        System.out.println("searching for messages with only FullMessage creation took "
                + ((System.currentTimeMillis() - time) / 1000.0)
                + " seconds");
    }
    public static double sum(double[] times) {
        int sum = 0;
        for (int i1 = 0; i1 < times.length; i1++) {
            sum+=times[i1];
        }
        return (sum)/(double)(times.length);
    }
    public static void avgMessageItem(double[] times, int index) throws IOException {

        long time = System.currentTimeMillis();
        for (int j = 0; j < 20; j++) {
            new MessageItem(new FullMessage(i, m.get(j)),"hello");
        }
        times[index] = (System.currentTimeMillis() - time) / 1000.0;
        System.out.println("searching for messages with MessageItem creation took "
                + ((System.currentTimeMillis() - time) / 1000.0)
                + " seconds");
    }*/
    }
}
