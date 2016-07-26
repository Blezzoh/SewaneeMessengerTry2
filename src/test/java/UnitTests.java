import com.danielevans.email.Authenticator;
import com.danielevans.email.FullMessage;
import com.danielevans.email.Inbox;
import com.google.api.services.gmail.model.Message;
import org.junit.Test;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Created by evansdb0 on 7/26/16.
 */
public class UnitTests {

    private Inbox inbox = new Inbox(new Authenticator("evansdb0@sewanee.edu"));

    List<Message> messages = inbox.getDefaultInbox();

    Hashtable<String, FullMessage> emailData = initEmailData();


    private Hashtable<String, FullMessage> initEmailData() {
        System.out.println("Initializing email data...");
        Hashtable<String, FullMessage> emailData = new Hashtable<>(messages.size() * 2);
        for (int i = 0; i < messages.size(); i++) {
            try {
                emailData.put(messages.get(i).getId(), new FullMessage(inbox, messages.get(i)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return emailData;
    }

    @Test
    public void messagesAndEmailDataCount() {
        assertEquals(messages.size(), emailData.size());
    }
}
