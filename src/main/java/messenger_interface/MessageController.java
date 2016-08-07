package messenger_interface;

import com.danielevans.email.FullMessage;
import com.danielevans.email.Inbox;
import com.danielevans.email.MessageParser;
import com.google.api.services.gmail.model.Message;
import javafx.scene.layout.TilePane;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;


/**
 * Created by evansdb0 on 8/5/16.
 * @author Daniel Evans
 */
public class MessageController extends TilePane {

    private MessageView[] messageViews;
    private Hashtable<String, FullMessage> emailData;
    private String tempImgURL =
            "http://4.bp.blogspot.com/-SjsG6gvCasI/Ve6PJxhPEiI/AAAAAAAAFYU/dYvGfnIxPzk/s1600/Kundwa%2BDoriane%2Brwanda.jpg";

    // TODO: MAKE THE imgUrls apart of the fms so that way all the data for MessageView is in one place
    public MessageController(Inbox inbox, int numMessageViews /*, Iterable<String> imgUrls */) {

        messageViews = new MessageView[numMessageViews];
        List<Message> messages = inbox.getDefaultInbox();
        initEmailData(inbox ,messages);
        // fill the messageViews with data
//        Iterator<String> iterator = imgUrls.iterator();
        for (int i = 0; i < messageViews.length; i++) {
            messageViews[i] = new MessageView(messages.get(i).getId(),tempImgURL)
            setMessageViewFields(messages.get(i), i /*, iterator.next() */);
        }
        this.getChildren().addAll(messageViews);
    }

    public void setMessageViewFields(Message message, int index /*, String imgUrl */) {
        FullMessage fm = emailData.get(message.getId());
        messageViews[index].getSenderField().setText(MessageParser.parseSenderFromEmail(fm));
        messageViews[index].getSubjectField().setText("S: " + fm.getSubject() + "\n");
        messageViews[index].getSnippetField().setText(fm.getSnippet());
        messageViews[index].getDateField().setText("Sent: " + MessageParser.parseDate(fm.getDate()));
        // the messageViews define the way they look, so the controller asks the view
        // how to make the view's image look
//        messageViews[index].setPicCanvas(MessageView.makeImage(imgUrl));
    }

    private void initEmailData(Inbox inbox, List<Message> messages) {
        System.out.println("Initializing email data...");
        emailData = new Hashtable<>(messages.size() * 2);
        for (int i = 0; i < messages.size(); i++) {
            try {
                emailData.put(messages.get(i).getId(), new FullMessage(inbox, messages.get(i)));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Not connected to the internet?");
            }
        }
    }

    private void setMessageViewFieldEvents(int index) {
        messageViews[index].getSnippetField().setOnMousePressed(e -> {
            setScene();
        });
        messageViews[index].getSubjectField().setOnMousePressed(e -> {
            setScene();
        });
    }

    public void setScene(Message) {
        WebView
    }
}
