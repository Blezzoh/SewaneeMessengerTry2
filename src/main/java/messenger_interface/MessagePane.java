package messenger_interface;

import com.danielevans.email.FullMessage;
import com.danielevans.email.MessageParser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by iradu_000 on 6/3/2016.
 * @author Blaise Iradukunda
 */
public class MessagePane extends TilePane {

    private static final String ITEM_STYLE = "-fx-padding: 10px; "+"-fx-background-color:white;" + "-fx-border-radius: 0px;" + "-fx-border-style: solid;" + "-fx-border-color: #66007a;";
    private static final String STYLE_ON_ENTER = "-fx-background-color: aquamarine;"+"-fx-background-radius: 0px;"+"-fx-padding: 10px; " + "-fx-border-radius: 0px;" + "-fx-border-style: solid;" + "-fx-border-color: #67007c;";
    private static final String STYLE_ON_EXIT = "-fx-background-color: white;" + "-fx-padding: 10px; " + "-fx-border-radius: 0px;" + "-fx-border-style: solid;" + "-fx-border-color: #67007c;";
    private static final String BACKB_STYLE = "-fx-font-family: AR DESTINE; -fx-font-size: 26px; -fx-background-color: rgba(7, 171,202,1); -fx-border-color: rgb(255, 153, 51);" +
            "-fx-effect: dropshadow(three-pass-box, black, 2, 0, 3, 3); -fx-text-fill: white; -fx-font-weight: bolder; ";


    private NotificationIcon hoveredIcon;
    private Text senderField, subjectField, snippetField, dateField;
    private HBox firstRowOptions;
    private Button b;
    private FullMessage fm;
    private ImageView labelEmail, downloadAttach, forwardEmail, markEmail, replyEmail, addToTrash;

    private Rectangle picCanvas;

    private Stage stage;


    public MessagePane(FullMessage m, String imageUrl) throws IOException {

        super();
        this.fm = m;
//        sceneStack = stack;
        initFields(m);
        initHoveredIcon();
        addMessageOptions();

        VBox msgSummary = new VBox(subjectField, snippetField);
        setSize(msgSummary, 200.0, 160.0);

        VBox container = new VBox(msgSummary);
        container.setBackground(new Background(new BackgroundFill(Color.rgb(255, 153, 51, .8), new CornerRadii(5), new Insets(0))));
        container.setPadding(new Insets(4));
        setSize(container, 200.0, 200.0);

        VBox picField = loadImage(imageUrl);
        setSize(picField, 100.0, 200.0);

        HBox message = initMessage();
        message.getChildren().addAll(picField, container);

        StackPane stackPane = initStackPane(message);


        // MessagePane events and style handling
        this.setHgap(8);
        this.setVgap(5);
        this.setPadding(new Insets(8, 0, 8, 0));
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: rgba(7, 171,202,.4)");

        // button and image view events
        setButtonAndImageEvents();

        // the shadow behind the MessagePane
        createMessagePaneShadow();

    }

    private StackPane initStackPane(HBox message) {
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(message, hoveredIcon);
        return stackPane;
    }

    private HBox initMessage() {
        HBox message = new HBox();
        message.setStyle(ITEM_STYLE);
        setSize(message, 300.0, 200.0);
        message.setOnMouseEntered(event -> {
            message.setStyle(STYLE_ON_ENTER);
        });
        message.setOnMouseExited(event -> {
            message.setStyle(STYLE_ON_EXIT);
        });
        return message;
    }

    private void createMessagePaneShadow() {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5. * 0);
        dropShadow.setOffsetX(5.0);
        dropShadow.setOffsetY(5.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        this.setEffect(dropShadow);
    }

    private VBox loadImage(String imageUrl) {
        // add the image to the message item
        Image imageField = new Image(imageUrl, 80, 0, true, true, false);
        ImagePattern imageView = new ImagePattern(imageField);
        picCanvas = new Rectangle(imageField.getWidth(), imageField.getHeight(), imageView);
        picCanvas.setArcHeight(20);
        picCanvas.setArcWidth(20);
        return new VBox(picCanvas, senderField, dateField, firstRowOptions);
    }

    private void initFields(FullMessage m) {
        // Initialization
        b = new Button("<-");
        setSize(b, 60, 40);
        senderField = new Text(MessageParser.parseSenderFromEmail(m));
        subjectField = new Text("S: " + m.getSubject() + "\n");
        snippetField = new Text(m.getSnippet());
        dateField = new Text("Sent: " + MessageParser.parseDate(fm.getDate()));

        // Look/Feel
        b.setStyle(BACKB_STYLE);

        snippetField.setWrappingWidth(190);
        snippetField.setFont(Font.font("Trebuchet MS", 13));
        snippetField.setFill(Color.WHITE);
        snippetField.setOnMouseClicked(event -> showContent());

        dateField.setWrappingWidth(190);
        dateField.setFont(Font.font("Trebuchet MS", 13));

        senderField.setWrappingWidth(100);
        senderField.setFont(Font.font("Trebuchet MS", 13));

        subjectField.setWrappingWidth(190);
        subjectField.setFont(Font.font("Trebuchet MS", FontWeight.BLACK, 13));
        subjectField.setFill(Color.WHITE);
        subjectField.setOnMouseClicked(event -> showContent());
    }

    private void initHoveredIcon() {
        hoveredIcon = new NotificationIcon("");
        hoveredIcon.setStyle("-fx-background-color: white;");
        hoveredIcon.setTranslateY(18);
        hoveredIcon.setVisible(false);
    }

    private void setButtonAndImageEvents() {
        // EVENTS
        b.setOnMouseClicked(event -> goBack());
        b.setOnMouseEntered(e -> this.getScene().setCursor(Cursor.HAND));
        b.setOnMouseExited(e -> this.getScene().setCursor(Cursor.DEFAULT));

        snippetField.setOnMouseEntered(event -> {
            underline(snippetField);
            this.getScene().setCursor(javafx.scene.Cursor.HAND);
        });
        snippetField.setOnMouseExited(event -> {
            removeUnderline(snippetField);
            this.getScene().setCursor(Cursor.DEFAULT);
        });

        subjectField.setOnMouseEntered(event -> {
            underline(subjectField);
            this.getScene().setCursor(javafx.scene.Cursor.HAND);

        });
        subjectField.setOnMouseExited(event -> {
            removeUnderline(subjectField);
            this.getScene().setCursor(Cursor.DEFAULT);
        });

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
        replyEmail.setOnMouseEntered(event -> {
            showMessage("reply");
        });
        replyEmail.setOnMouseExited(event -> {
            removeMessage();
        });

        markEmail.setOnMouseEntered(event -> {
            showMessage("mark as read");
        });
        markEmail.setOnMouseExited(event -> {
            removeMessage();
        });

        addToTrash.setOnMouseEntered(event -> {
            showMessage("add to trash");
        });
        addToTrash.setOnMouseExited(event -> {
            removeMessage();
        });

        labelEmail.setOnMouseEntered(event -> {
            showMessage("label this message as");
        });
        labelEmail.setOnMouseExited(event -> {
            removeMessage();
        });

        forwardEmail.setOnMouseEntered(event -> {
            showMessage("forward");
        });
        forwardEmail.setOnMouseExited(event -> {
            removeMessage();
        });

        downloadAttach.setOnMouseEntered(event -> {
            showMessage("add to trash");
        });
        downloadAttach.setOnMouseExited(event -> {
            removeMessage();
        });

//        addToTrash.setOnMouseClicked(event -> trashEmail());
//        mark.setOnMouseClicked(event -> moveEmail("READ"));
    }

    public FullMessage getFm() {
        return fm;
    }

    // controller method
    public void setFm(FullMessage fm) {
        this.fm = fm;
        snippetField.setText(fm.getSnippet());
        subjectField.setText(fm.getSubject());
        senderField.setText(MessageParser.parseSenderFromEmail(fm));
        dateField.setText("Sent: " + MessageParser.parseDate(fm.getDate()));

    }

    private static void setSize(Node node, double w, double h) {

        node.prefHeight(h);
        node.maxHeight(h);
        node.minHeight(h);

        node.maxWidth(w);
        node.minWidth(w);
        node.prefWidth(w);
    }


    // controller method
    private void goBack() {
        // pop() the seen created in showContent()

//        sceneStack.pop();

        // sceneStack already has the original scene saved (controller pushed its scene)
        // so it is available here

//        stage.setScene(sceneStack.peek());
    }

    // controller method
    private void showContent() {
        WebView wv = new WebView();
        String bodyText = fm.getBestMessageBody();

        // loadContent(String) will return and do nothing if bodyText is null
        // therefore the old bodyText from previous message Item will be loaded when the
        // user clicks on the snippet or the subject
        WebEngine engine = wv.getEngine();
        if (!FullMessage.testForHTML(bodyText))
            // load plain text version
            engine.loadContent(bodyText, "text/plain");
        else
            // load html version
            engine.loadContent(bodyText);
        stage = (Stage) this.getScene().getWindow();
        double stageX = stage.getWidth(), stageY = stage.getHeight();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // make sure that the size of the of the scene is smaller than the screen size
        stageX = stageX >= screenSize.getWidth() ? stageX - 250 : stageX;
        stageY = stageY >= screenSize.getHeight() ? stageY - 250 : stageY;
        BorderPane p = new BorderPane();
        p.setTop(b);
        p.setCenter(wv);
        Scene body = new Scene(p, stageX, stageY);
//        sceneStack.push(body);
        body.getStylesheets().add("MessengerStyle.css");
//        stage.setScene(sceneStack.peek());
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
    private void addMessageOptions() throws FileNotFoundException {

        firstRowOptions = new HBox();
        firstRowOptions.setAlignment(Pos.BOTTOM_LEFT);

        Image download = new Image(new FileInputStream(new File(System.getProperty("user.home"), "IdeaProjects/SewaneeMessengerTry2/src/main/resources/download.png")), 20, 20, true, true);
        Image forward = new Image(new FileInputStream(new File(System.getProperty("user.home"), "IdeaProjects/SewaneeMessengerTry2/src/main/resources/forward.png")), 20, 20, true, true);
        Image label = new Image(new FileInputStream(new File(System.getProperty("user.home"), "IdeaProjects/SewaneeMessengerTry2/src/main/resources/label.png")), 20, 20, true, true);
        Image mark = new Image(new FileInputStream(new File(System.getProperty("user.home"), "IdeaProjects/SewaneeMessengerTry2/src/main/resources/mark.png")), 20, 20, true, true);
        Image reply = new Image(new FileInputStream(new File(System.getProperty("user.home"), "IdeaProjects/SewaneeMessengerTry2/src/main/resources/reply.png")), 20, 20, true, true);
        Image trash = new Image(new FileInputStream(new File(System.getProperty("user.home"), "IdeaProjects/SewaneeMessengerTry2/src/main/resources/trash.png")), 20, 20, true, true);

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

    public Rectangle getPicCanvas() {
        return picCanvas;
    }

    public void setPicCanvas(Rectangle picCanvas) {
        this.picCanvas = picCanvas;
    }


}

