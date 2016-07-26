package messenger_interface;

import com.danielevans.email.Inbox;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

/**
 * Created by daniel on 6/8/16.
 * @author Daniel Evans
 */
public class ComposeMessage {

    private TextArea bodyText;
    private TextField emailAddress;
    private TextField Cc;
    private HBox sendAndExtrasContainer;
    private Button send;
    private Button attachments;
    private TextField subject;
    private VBox root;

    public ComposeMessage(Inbox inbox, Controller controller) {

        subject = new TextField();
        subject.setPromptText("Subject");
        bodyText = new TextArea();
        bodyText.setPromptText("Body Text");
        bodyText.setWrapText(true);
        bodyText.setMaxWidth(200);
        emailAddress = new TextField();
        emailAddress.setPromptText("To: ");
        Cc = new TextField();
        Cc.setPromptText("Cc: ");
        send = new Button("Send");
        attachments = new Button("Attachment");

        attachments.setOnMouseClicked(e ->
        {
           // open file manager, load attachment into message
        });

        send.setOnMouseClicked(e ->
        {
            System.out.println(bodyText.getText().equals(""));
            if (bodyText.getText().equals(""))
                controller.setMessageToUser("The message body is empty");
            else if (emailAddress.getText().equals(""))
                controller.setMessageToUser("The To: field is empty");
                // else if check if it is a valid email address
            else {
                // upload attachment if any
                // ...
                // make message and send
                MimeMessage mimeMessage = null;
                try {
                    Session s = inbox.getSessionWithDefaultProps();
                    mimeMessage = inbox.composeMessage
                    (
                        s, emailAddress.getText(), subject.getText(), bodyText.getText()
                    );
                    inbox.sendMessage(mimeMessage);
                } catch (MessagingException | IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        sendAndExtrasContainer = new HBox(8,send,attachments);
        root = new VBox(8);
        root.setPadding(new Insets(4));
        root.getChildren().addAll(emailAddress,Cc,subject,bodyText,sendAndExtrasContainer);
    }
    public VBox getRoot() {
        return root;
    }

    public TextField getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(TextField emailAddress) {
        this.emailAddress = emailAddress;
    }

    public TextArea getBodyText() {
        return bodyText;
    }

    public void setBodyText(TextArea bodyText) {
        this.bodyText = bodyText;
    }

    public TextField getCc() {
        return Cc;
    }

    public void setCc(TextField cc) {
        Cc = cc;
    }

    public TextField getSubject() {
        return subject;
    }

    public void setSubject(TextField subject) {
        this.subject = subject;
    }
}
