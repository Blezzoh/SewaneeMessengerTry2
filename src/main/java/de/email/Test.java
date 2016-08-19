package de.email;

import com.google.api.services.gmail.model.Draft;
import com.google.api.services.gmail.model.ListDraftsResponse;
import de.email.interfaces.Auth;

import java.io.IOException;
import java.util.List;

/**
 * Created by Daniel Evans on 7/13/16.
 * @author Daniel Evans
 */
public class Test {


    private static final String MESSAGE_ID = "1562ca4c4997819d";

    public static void main(String[] args) throws IOException, InterruptedException {

        Inbox inbox = new Inbox("iradub0@sewanee.edu");

        listDrafts(inbox);

    }

    /**
     * List the drafts in the user's account.
     *
     * @throws IOException
     */
    public static void listDrafts(Auth auth) throws IOException {
        ListDraftsResponse response = auth.getAuth().service.users()
                .drafts().list(auth.getAuth().userId).execute();
        List<Draft> drafts = response.getDrafts();
        FullMessage m = null;
        for (Draft draft : drafts) {
            if (draft.getId().equals("r-346745505429578240"))
                m = new FullMessage(auth, draft.getMessage());
        }
        if (m != null) {
            System.out.println(m.getFromName());
            System.out.println(m.getSubject());
            System.out.println(m.getBody());
        }
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
