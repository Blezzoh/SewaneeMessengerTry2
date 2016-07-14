import com.danielevans.email.Authenticator;
import com.danielevans.email.FullMessage;
import com.danielevans.email.Inbox;
import com.google.api.services.gmail.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by evansdb0 on 7/13/16.
 */
public class InboxTest {


    public static void main(String[] args) {

        Inbox inbox = new Inbox(new Authenticator("evansdb0@sewanee.edu"));

        List<Message> messages = new ArrayList<>(50);

        List<FullMessage> fms = new ArrayList<>(50);

        System.out.println(fms.get(0).getDate());
    }
}
