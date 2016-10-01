package SophiaMessenger.Views;

import SophiaMessenger.LabelHolderOnHover;
import SophiaMessenger.Models.DBMessage;
import SophiaMessenger.NotificationIcon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by iradu_000 on 6/3/2016.
 * @author Blaise Iradukunda
 */
public class
MessageView extends StackPane {

    public static final String RESOURCES_PATH = "IdeaProjects/SewaneeMessengerTry2/src/main/resources/";
    private static final String PIC_HOVER_STYLE = "-fx-padding: 10px; -fx-border-radius: 0px";
    private static final String RIGHT_CONTAINER_HOVER_STYLE = "-fx-background-insets: -10, -10;";
    private static final String BACKB_STYLE = "-fx-font-family: AR DESTINE; -fx-font-size: 26px; -fx-background-color: rgba(7, 0,202,1); -fx-border-color: rgb(255, 153, 51);" +
            "-fx-effect: dropshadow(three-pass-box, black, 2, 0, 3, 3); -fx-text-fill: white; -fx-font-weight: bolder; ";


    private NotificationIcon hoveredIcon;
    private Text senderField, subjectField, snippetField, dateField;
    private HBox firstRowOptions;
    private Button b;
    private ImageView labelEmail, downloadAttach, forwardEmail, markEmail, replyEmail, addToTrash;
    private VBox picContainer;
    private VBox rightContainer;
    private Rectangle pic;
    private String messageId;

    public MessageView(String mId) {

        super();
        initFields();
        senderField.getStyleClass().add("fields");
        this.messageId = mId;
        initHoveredIcon();
        addMessageOptions();

        VBox msgSummary = new VBox(subjectField, snippetField);
        msgSummary.setSpacing(20);
        setSize(msgSummary, 300, 160.0);

        rightContainer = new VBox(msgSummary);
        rightContainer.getStyleClass().add("RightMessage");
        rightContainer.setPadding(new Insets(4));
        setSize(rightContainer, 200.0, 200.0);
        pic = new Rectangle();
        picContainer = new VBox(3, pic, senderField, dateField, firstRowOptions);
        setSize(picContainer, 100.0, 200.0);

        HBox message = initMessage();
        message.getChildren().addAll(picContainer, rightContainer);

        this.getChildren().addAll(message, hoveredIcon);

        // button and image view events
        setButtonAndImageEvents();

    }

    public static Rectangle makeImage(String imageUrl) {
        // add the image to the message item
        Image imageField = new Image(imageUrl, 100, 50, true, true, false);
        ImagePattern imageView = new ImagePattern(imageField);
        Rectangle image = new Rectangle(imageField.getWidth(), imageField.getHeight(), imageView);
        image.setArcHeight(10);
        image.setArcWidth(10);
        return image;
    }

    public static void setSize(Node node, double w, double h) {

        node.prefHeight(h);
        node.maxHeight(h);
        node.minHeight(h);

        node.maxWidth(w);
        node.minWidth(w);
        node.prefWidth(w);
    }

    /**
     * sets the fields of this message view
     * to the information as described in dbm
     *
     * @param dbm the database message containing email info
     */
    public void setMessageFields(DBMessage dbm) {
        senderField.setText(dbm.getFromName());
        subjectField.setText(dbm.getSubject());
        dateField.setText("Sent: " + dbm.getDate());
        snippetField.setText(dbm.getSnippet());
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    private HBox initMessage() {
        HBox message = new HBox();
        message.getStyleClass().add("MessageView");
        setSize(message, 300.0, 200.0);
        message.setOnMouseEntered(event -> {
            // rgba(153,153,255,.6)
            // #6666ff
            message.setStyle(PIC_HOVER_STYLE + ";-fx-background-color: rgba(136,136,255,.7)");
            rightContainer.setStyle(RIGHT_CONTAINER_HOVER_STYLE + "-fx-background-color: rgba(40,57,158,.7)");
        });
        message.setOnMouseExited(event -> {
            message.setStyle(PIC_HOVER_STYLE + ";-fx-background-color: rgba(153,153,255,.7)");
            // rgba(59,108,158,.6)
            // 3B6C9E
            rightContainer.setStyle(RIGHT_CONTAINER_HOVER_STYLE + "-fx-background-color: rgba(59,108,158,.7)");
        });
        return message;
    }

    public NotificationIcon getHoveredIcon() {
        return hoveredIcon;
    }

    public void setHoveredIcon(NotificationIcon hoveredIcon) {
        this.hoveredIcon = hoveredIcon;
    }

    public void setSenderField(Text senderField) {
        this.senderField = senderField;
    }

    public void setSubjectField(Text subjectField) {
        this.subjectField = subjectField;
    }

    public void setSnippetField(Text snippetField) {
        this.snippetField = snippetField;
    }

    public Text getDateField() {
        return dateField;
    }

    public void setDateField(Text dateField) {
        this.dateField = dateField;
    }

    public Button getB() {
        return b;
    }

    public void setB(Button b) {
        this.b = b;
    }

    private void initFields() {
        // Initialization
        b = new Button("<-");
        setSize(b, 60, 40);
        senderField = new Text();
        subjectField = new Text();
        snippetField = new Text();
        dateField = new Text();

        // Look/Feel
        b.setStyle(BACKB_STYLE);
        snippetField.getStyleClass().add("messagefields");
        senderField.getStyleClass().add("messagefields");
        subjectField.getStyleClass().add("messagefields");
        dateField.getStyleClass().add("messagefields");
        snippetField.setWrappingWidth(190);
        snippetField.setFont(Font.font(13));
        snippetField.setLineSpacing(5);

        dateField.setWrappingWidth(190);
        dateField.setFont(Font.font(13));

        senderField.setWrappingWidth(190);
        senderField.setFont(Font.font(14));
        senderField.setCache(true);
        senderField.setX(10.0f);
        senderField.setY(270.0f);
        senderField.setFont(Font.font(null, FontWeight.SEMI_BOLD, 15));

        subjectField.setWrappingWidth(195);
        subjectField.setFont(Font.font(null, FontWeight.SEMI_BOLD, 15));
        subjectField.setTextAlignment(TextAlignment.CENTER);
        subjectField.setLineSpacing(5);

    }

    private void initHoveredIcon() {
        hoveredIcon = new NotificationIcon("");
        hoveredIcon.setStyle("-fx-background-color: white;");
        hoveredIcon.setTranslateY(18);
        hoveredIcon.setVisible(false);
    }

    private void setButtonAndImageEvents() {
        // EVENTS
        b.setOnMouseEntered(e -> this.getScene().setCursor(Cursor.HAND));
        b.setOnMouseExited(e -> this.getScene().setCursor(Cursor.DEFAULT));

        this.setOnMouseEntered(e -> this.getScene().setCursor(javafx.scene.Cursor.HAND));
        this.setOnMouseExited(e -> this.getScene().setCursor(Cursor.DEFAULT));

        for (int i = 0; i < firstRowOptions.getChildren().size(); i++) {
            firstRowOptions.getChildren().get(i).setOnMouseEntered(e -> {
                this.getScene().setCursor(javafx.scene.Cursor.HAND);
            });
            firstRowOptions.getChildren().get(i).setOnMouseExited(e -> {
                this.getScene().setCursor(javafx.scene.Cursor.HAND);
            });
        }
    }

    private void showMessage(String s) {
        hoveredIcon.setLabelText(s);
        hoveredIcon.setVisible(true);
    }

    private void removeMessage() {
        hoveredIcon.setVisible(false);
    }

    // labelEmail, downloadAttach, forwardEmail, markEmail, replyEmail, addToTrash
    private void setIconsProperties() {
        final String[] iconNames = {"reply", "mark as read", "add to trash",
                "label this message as", "forward", "add to trash"};

        for (int i = 0; i < firstRowOptions.getChildren().size(); i++) {
            ImageView iv = (ImageView) firstRowOptions.getChildren().get(i);
            final String iconName = iconNames[i];
            iv.setOnMouseEntered(e -> {
                showMessage(iconName);
            });
            iv.setOnMouseExited(e -> {
                removeMessage();
            });
        }
//        addToTrash.setOnMouseClicked(event -> trashEmail());
//        mark.setOnMouseClicked(event -> moveEmail("READ"));
    }

    // all mItem methods
    private void removeUnderline(Text node) {
        node.setUnderline(false);
    }

    private void underline(Text node) {
        node.setUnderline(true);
    }

    private void removeLabels(LabelHolderOnHover allLabels) {
        this.getParent();
    }

    /**
     * GUIs that represents the set of options that can be applied to a MessageItem
     */
    private void addMessageOptions() {

        firstRowOptions = new HBox();
        firstRowOptions.setAlignment(Pos.BOTTOM_LEFT);


        Image download = null;
        Image trash = null;
        Image reply = null;
        Image mark = null;
        Image label = null;
        Image forward = null;
        try {
            download = new Image(new FileInputStream(new File(System.getProperty("user.home"), RESOURCES_PATH + "download.png")), 20, 20, true, true);
        } catch (FileNotFoundException e) { e.printStackTrace();}
        try {
            forward = new Image(new FileInputStream(new File(System.getProperty("user.home"), RESOURCES_PATH + "forward.png")), 20, 20, true, true);
        } catch (FileNotFoundException e) {e.printStackTrace();}
        try {
            label = new Image(new FileInputStream(new File(System.getProperty("user.home"), RESOURCES_PATH + "label.png")), 20, 20, true, true);
        } catch (FileNotFoundException e) {e.printStackTrace();}
        try {
            mark = new Image(new FileInputStream(new File(System.getProperty("user.home"), RESOURCES_PATH + "mark.png")), 20, 20, true, true);
        } catch (FileNotFoundException e) {e.printStackTrace();}
        try {
            reply = new Image(new FileInputStream(new File(System.getProperty("user.home"), RESOURCES_PATH + "reply.png")), 20, 20, true, true);
        } catch (FileNotFoundException e) {e.printStackTrace();}
        try {
            trash = new Image(new FileInputStream(new File(System.getProperty("user.home"), RESOURCES_PATH + "trash.png")), 20, 20, true, true);
        } catch (FileNotFoundException e) {e.printStackTrace();}

        // TODO: what if the above image local vars are null?????
        downloadAttach = new ImageView(download);
        HBox.setMargin(downloadAttach, new Insets(1, 5, 1, 1));
        // TODO: set the forwardEmail event to interact with ComposerManger
        forwardEmail = new ImageView(forward);
        HBox.setMargin(forwardEmail, new Insets(1, 5, 1, 1));
        labelEmail = new ImageView(label);
        HBox.setMargin(labelEmail, new Insets(1, 5, 1, 1));


        markEmail = new ImageView( mark);
        HBox.setMargin(markEmail, new Insets(1, 5, 1, 1));
        // TODO: set the replyEmail event to interact with ComposerManger
        replyEmail= new ImageView(reply);
        HBox.setMargin(replyEmail, new Insets(1, 5, 1, 1));
        addToTrash = new ImageView(trash);
        HBox.setMargin(addToTrash, new Insets(1, 5, 1, 1));

        firstRowOptions.getChildren().addAll(replyEmail, forwardEmail, labelEmail,
                downloadAttach, markEmail, addToTrash);
    }

    public ImageView getLabelEmail() {
        return labelEmail;
    }

    public Text getSenderField() {
        return senderField;
    }

    public void setSenderField(String text) {
        senderField.setText(text);
    }

    public Text getSubjectField() {
        return subjectField;
    }

    public void setSubjectField(String text) {
        subjectField.setText(text);
    }

    public Text getSnippetField() {
        return snippetField;
    }

    public void setSnippetField(String text) {
        snippetField.setText(text);
    }

    public ImageView getDownloadAttach() {
        return downloadAttach;
    }

    public ImageView getForwardEmail() {
        return forwardEmail;
    }

    public ImageView getMarkEmail() {
        return markEmail;
    }

    public ImageView getReplyEmail() {
        return replyEmail;
    }

    public ImageView getAddToTrash() {
        return addToTrash;
    }

    public VBox getPicField() {
        return picContainer;
    }

    public void setPicField(VBox picContainer) {
        this.picContainer = picContainer;
    }

    public Rectangle getPic() {
        return pic;
    }

    public void setPic(Rectangle pic) {
        this.pic = pic;
        picContainer.getChildren().remove(0);
        picContainer.getChildren().add(0, pic);
    }
}

