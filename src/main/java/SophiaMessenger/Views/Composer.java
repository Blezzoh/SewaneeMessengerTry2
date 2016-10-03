package SophiaMessenger.Views;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.web.HTMLEditor;

import java.util.SortedSet;

/**
 * Created by daniel on 6/8/16.
 * @author Daniel Evans
 */
public class Composer extends BorderPane {

    private static int COMPOSER_WIDTH = 350;
    private final String XImageUrl = "https://cdn3.iconfinder.com/data/icons/virtual-notebook/16/button_close-128.png";
    private HTMLEditor editor;
    private AutoSuggestTextBox emailAddress;
    private TextField Cc;
    private Button send;
    private Button attachments;
    private TextField subject;
    private VBox vbox;
    private Rectangle xCloser;
    private Text notificationsToUser;

    public Composer(SortedSet<String> eas) {

        initFields(eas);
        HBox sendAndExtrasContainer = new HBox(8, send, attachments,
                notificationsToUser);

        setButtonEvents();
        xCloser = xCloserSettings
                (makeImage(XImageUrl
                        , 15, 15));
        vbox = new VBox(8);
        vboxSettings();
        vbox.getChildren().addAll(xCloser, emailAddress,
                Cc, subject, editor, sendAndExtrasContainer);

        editor.setPrefWidth(COMPOSER_WIDTH);
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

    public Text getNotificationsToUser() {
        return notificationsToUser;
    }

    private void initFields(SortedSet<String> eas) {
        subject = new TextField();
        subject.setPromptText("Subject");
        editor = new HTMLEditor();
        emailAddress = new AutoSuggestTextBox(eas);
        emailAddress.setPromptText("To: ");
        Cc = new TextField();
        Cc.setPromptText("Cc: ");
        send = new Button("Send");
        attachments = new Button("Attachment");
        notificationsToUser = new Text();
        notificationsToUser.setVisible(false);
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


    public HTMLEditor getEditor() {
        return editor;
    }

    public TextField getCc() {
        return Cc;
    }

    public void setCc(String text) {
        Cc.setText(text);
    }

    public TextField getSubject() {
        return subject;
    }

    public void setSubject(String text) {
        subject.setText(text);
    }

    public void clearAllTextFields() {
        emailAddress.setText("");
        editor.setHtmlText("");
        Cc.setText("");
        subject.setText("");
    }

    public boolean emailAddressIsEmpty() {
        return emailAddress.getText().equals("");
    }

    // Helper methods for setting composer's data fields
    public void setEmail(String text) {
        emailAddress.setText(text);
    }

    // Helper methods for setting composer's data fields
    public String getEmailText() {
        return emailAddress.getText();
    }

    public String getCcText() {
        return Cc.getText();
    }

    public String getSubjectText() {
        return subject.getText();
    }

    public String getEditorText() {
        return editor.getHtmlText();
    }

    public void setEditorText(String text) {
        editor.setHtmlText(text);
    }

}
