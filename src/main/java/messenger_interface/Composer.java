package messenger_interface;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

/**
 * Created by daniel on 6/8/16.
 * @author Daniel Evans
 */
public class Composer extends BorderPane {

    private TextArea bodyText;
    private TextField emailAddress;
    private TextField Cc;
    private Button send;
    private Button attachments;
    private TextField subject;
    private VBox vbox;
    private Rectangle xCloser;

    public Composer() {

        initFields();

        HBox sendAndExtrasContainer = new HBox(8, send, attachments);
        setButtonEvents();
        xCloser = xCloserSettings
                (makeImage("https://cdn3.iconfinder.com/data/icons/virtual-notebook/16/button_close-128.png"
                        , 15, 15));
        vbox = new VBox(8);
        vboxSettings();
        vbox.getChildren().addAll(xCloser, emailAddress, Cc, subject, bodyText, sendAndExtrasContainer);
        this.setCenter(vbox);
        this.setVisible(false);
    }

    public Button getSend() {
        return send;
    }

    public Button getAttachments() {
        return attachments;
    }

    public Rectangle getxCloser() {
        return xCloser;
    }

    private void setButtonEvents() {
        send.setOnMouseEntered(e -> this.getScene().setCursor(Cursor.HAND));
        send.setOnMouseExited(e -> this.getScene().setCursor(Cursor.DEFAULT));
    }

    private void vboxSettings() {
        vbox.setPadding(new Insets(4));
    }

    private Rectangle xCloserSettings(Rectangle xCloser) {
        return xCloser;
    }

    private void initFields() {
        subject = new TextField();
        subject.setPromptText("Subject");
        bodyText = new TextArea();
        bodyText.setPromptText("Body Text");
        bodyText.setWrapText(true);
        emailAddress = new TextField();
        emailAddress.setPromptText("To: ");
        Cc = new TextField();
        Cc.setPromptText("Cc: ");
        send = new Button("Send");
        attachments = new Button("Attachment");
    }

    private Rectangle makeImage(String imageUrl,
                                double requestedWidth,
                                double requestedHeight) {
        Image imageField = new Image(imageUrl, requestedWidth, requestedHeight
                , true, true, false);
        ImagePattern imageView = new ImagePattern(imageField);

        return new Rectangle(imageField.getWidth(), imageField.getHeight(), imageView);
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

    public void clearAllTextFields() {
        emailAddress.setText("");
        bodyText.setText("");
        Cc.setText("");
        subject.setText("");
    }
}
