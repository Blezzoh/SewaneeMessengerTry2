import com.google.api.services.gmail.model.Message;
import de.email.FullMessage;
import de.email.Inbox;
import org.junit.Test;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Created by evansdb0 on 7/26/16.
 * @author Daniel Evans
 */
public class UnitTests {

    Inbox inbox = new Inbox("evansdb0@sewanee.edu");

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
    public void sameIds() {
        Iterator<Map.Entry<String, FullMessage>> iterator = emailData.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, FullMessage> next = iterator.next();
            assertEquals(next.getKey(), next.getValue().getId());
        }
    }

    @Test
    public void messagesAndEmailDataCount() {
        assertEquals(messages.size(), emailData.size());
    }

    @Test
    public void testHTML() {
        assertTrue(FullMessage.testForHTML("<a kdlsjfdlkfjdfds>"));
//        assertTrue(FullMessage.testForHTML("<a href=\"sdfd>\">"));
//        assertTrue(FullMessage.testForHTML("<a kdlsjfdlkfjdfds>"));
//        assertTrue(FullMessage.testForHTML("<a kdlsjfdlkfjdfds>"));
    }
}
