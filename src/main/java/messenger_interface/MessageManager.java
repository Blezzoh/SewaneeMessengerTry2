package messenger_interface;

import com.danielevans.email.FullMessage;
import com.google.api.services.gmail.model.Message;
import javafx.scene.control.Pagination;

import java.util.Hashtable;
import java.util.List;

/**
 * Created by evansdb0 on 8/4/16.
 *
 * @author Daniel Evans
 */
public class MessageManager extends Pagination {


    private MessageItem[] mItems;
    private Hashtable<String, FullMessage> emailData;


    public MessageManager(List<Message> messages) {
        initEmailData(messages);

    }

    private void initEmailData(List<Message> messages) {
        System.out.println("Initializing email data...");
        emailData = new Hashtable<>(messages.size() * 2);
        for (int i = 0; i < messages.size(); i++) {
            // emailData.put(messages.get(i).getId(), new FullMessage(inbox, messages.get(i)));
        }
    }
}
