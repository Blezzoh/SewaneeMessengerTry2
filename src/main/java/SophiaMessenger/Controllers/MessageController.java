package SophiaMessenger.Controllers;

import SophiaMessenger.Views.MessageView;
import com.google.api.services.gmail.model.Message;
import de.email.Authenticator;
import de.email.FullMessage;
import de.email.Inbox;
import de.email.MessageParser;
import javafx.scene.layout.TilePane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by evansdb0 on 8/5/16.
 *
 * @author Daniel Evans
 */
public class MessageController extends TilePane {

    private List<MessageView> messageViews;
    private String tempImgURL =
            "http://4.bp.blogspot.com/-SjsG6gvCasI/Ve6PJxhPEiI/AAAAAAAAFYU/dYvGfnIxPzk/s1600/Kundwa%2BDoriane%2Brwanda.jpg";
    private Authenticator auth;

    // TODO: MAKE THE imgUrls apart of the fms so that way all the data for MessageView is in one place
    public MessageController(Inbox inbox, int numMessageViews /*, Iterable<String> imgUrls */) {

        auth = inbox.getAuth();
        messageViews = new ArrayList<>(numMessageViews);
        // should this be getDefaultInbox() or listMessages() ???
        List<Message> messages = inbox.getInbox();

        // fill the messageViews with data
//        Iterator<String> iterator = imgUrls.iterator();
        for (int i = 0; i < messageViews.size(); i++) {
            messageViews.add(new MessageView(messages.get(i).getId(), tempImgURL));
            setMessageViewFields(messages.get(i), i /*, iterator.next() */);
        }
        this.getChildren().addAll(messageViews);
    }

    public void setMessageViewFields(Message message, int i /*, String imgUrl */) {
        FullMessage fm = null;
        try {
            fm = new FullMessage(auth, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fm != null) {
            MessageView mv = messageViews.get(i);
            mv.getSenderField().setText(fm.getFromName());
            mv.getSubjectField().setText("S: " + fm.getSubject() + "\n");
            mv.getSnippetField().setText(fm.getSnippet());
            mv.getDateField().setText("Sent: " + MessageParser.parseDate(fm.getDate()));
            // the messageViews define the way they look, so the controller asks the view
            // how to make the view's image look
//        messageViews[index].setPicCanvas(MessageView.makeImage(imgUrl));
        }
    }

    private void setMessageViewFieldEvents(int index) {
        MessageView mv = messageViews.get(index);
        mv.getSnippetField().setOnMousePressed(e -> {
//            setScene();
        });
        mv.getSubjectField().setOnMousePressed(e -> {
//            setScene();
        });
    }

    public void setScene(Message m) {

    }
}
