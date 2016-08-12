package messenger_interface;

import com.danielevans.email.Email;
import com.danielevans.email.FullMessage;
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
import java.util.Stack;

/**
 * Created by iradu_000 on 6/3/2016.
 * @author Blaise Iradukunda
 */
public class MessageItem extends HBox {
    private static final String ITEM_STYLE = "-fx-padding: 10px; "+"-fx-background-color:white;" + "-fx-border-radius: 0px;" + "-fx-border-style: solid;" + "-fx-border-color: #66007a;";
    private static final String STYLE_ON_ENTER = "-fx-background-color: aquamarine;"+"-fx-background-radius: 0px;"+"-fx-padding: 10px; " + "-fx-border-radius: 0px;" + "-fx-border-style: solid;" + "-fx-border-color: #67007c;";
    private static final String STYLE_ON_EXIT = "-fx-background-color: white;" + "-fx-padding: 10px; " + "-fx-border-radius: 0px;" + "-fx-border-style: solid;" + "-fx-border-color: #67007c;";
    private static final String BACKB_STYLE = "-fx-font-family: AR DESTINE; -fx-font-size: 26px; -fx-background-color: rgba(7, 171,202,1); -fx-border-color: rgb(255, 153, 51);" +
            "-fx-effect: dropshadow(three-pass-box, black, 2, 0, 3, 3); -fx-text-fill: white; -fx-font-weight: bolder; ";

    private Text senderField, subjectField, snippetField, dateField;
    private String messageId;
    private HBox secondRowOptions;
    private HBox firstRowOptions;
    private Button b = new Button("<-");
    private ImageView labelEmail, downloadAttach, forwardEmail, markEmail, replyEmail, addToTrash;
    private Stage stage;
    private Stack<Scene> sceneStack;


    public MessageItem(BorderPane root, Stack<Scene> stack, Email email, String imageUrl) throws IOException {

        super();
        sceneStack = stack;
        initTextFields(email);
        addOptionsOnMessage(root);

        // add the image to the message item
        Image imageField = new Image(imageUrl, 80, 0, true, true, false);
        ImagePattern imageView = new ImagePattern(imageField);
        Rectangle canvas = new Rectangle(imageField.getWidth(), imageField.getHeight(), imageView);
        canvas.setArcHeight(20);
        canvas.setArcWidth(20);
        VBox picField = new VBox(canvas, senderField, dateField,firstRowOptions);

        VBox msgSummary = new VBox(subjectField, snippetField);
        VBox container = new VBox(msgSummary);
        firstRowOptions.setAlignment(Pos.BOTTOM_LEFT);

        getChildren().addAll(picField, container);

//        setSize(imageView, 100.0, 100.0);
        setSize(this, 300.0, 200.0);
        setSize(picField, 100.0, 200.0);
        setSize(msgSummary, 200.0, 160.0);
        setSize(container, 200.0, 200.0);
        setStyle(ITEM_STYLE);
        container.setBackground(new Background(new BackgroundFill(Color.rgb(255, 153, 51, .8), new CornerRadii(5), new Insets(0))));
        container.setPadding(new Insets(4));

        this.setOnMouseEntered(event -> {
            this.setStyle(STYLE_ON_ENTER);
        });
        this.setOnMouseExited(event -> {
            this.setStyle(STYLE_ON_EXIT);
        });

        setButtonAndImageEvents();

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5. * 0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        setEffect(dropShadow);

        b.setStyle(BACKB_STYLE);
        setSize(b, 60, 40);

    }

    private void initTextFields(Email email) {
        // Initialization
        senderField = new Text(email.getFromName());
        subjectField = new Text("S: " + email.getSubject() + "\n");
        snippetField = new Text(email.getSnippet());
        dateField = new Text("Sent: " + email.getDate());


        // Look/Feel
        subjectField.setWrappingWidth(190);
        snippetField.setWrappingWidth(190);
        dateField.setWrappingWidth(190);
        senderField.setWrappingWidth(100);

        senderField.setFont(Font.font("Trebuchet MS", 13));
        snippetField.setFont(Font.font("Trebuchet MS", 13));
        snippetField.setFill(Color.WHITE);
        dateField.setFont(Font.font("Trebuchet MS", 13));
        subjectField.setFont(Font.font("Trebuchet MS", FontWeight.BLACK, 13));
        subjectField.setFill(Color.WHITE);

        subjectField.setOnMouseClicked(event -> showContent(email.getBody()));
        snippetField.setOnMouseClicked(event -> showContent(email.getBody()));
        b.setOnMouseClicked(event -> goBack());
    }

    public void setButtonAndImageEvents() {
        // EVENTS
        b.setOnMouseEntered(e -> this.getScene().setCursor(Cursor.HAND));
        b.setOnMouseExited(e -> this.getScene().setCursor(Cursor.DEFAULT));
        snippetField.setOnMouseEntered(event -> {
            underline(snippetField);
            this.getScene().setCursor(javafx.scene.Cursor.HAND);
        });
        subjectField.setOnMouseEntered(event -> {
            underline(subjectField);
            this.getScene().setCursor(javafx.scene.Cursor.HAND);

        });
        subjectField.setOnMouseExited(event -> {
            removeUnderline(subjectField);
            this.getScene().setCursor(Cursor.DEFAULT);
        });
        snippetField.setOnMouseExited(event -> {
            removeUnderline(snippetField);
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
    protected static void setSize(Node node, double w, double h) {

        node.prefHeight(h);
        node.maxHeight(h);
        node.minHeight(h);

        node.maxWidth(w);
        node.minWidth(w);
        node.prefWidth(w);
    }

    private void goBack() {
        // pop() the seen created in showContent()
        sceneStack.pop();
        // sceneStack already has the original scene saved (controller pushed its scene)
        // so it is available here
        stage.setScene(sceneStack.peek());
    }

    private void showContent(String body) {
        WebView wv = new WebView();

        // loadContent(String) will return and do nothing if bodyText is null
        // therefore the old bodyText from previous message Item will be loaded when the
        // user clicks on the snippet or the subject
        WebEngine engine = wv.getEngine();
        if (!FullMessage.testForHTML(body))
            // load plain text version
            engine.loadContent(body, "text/plain");
        else
            // load html version
            engine.loadContent(body);
        stage = (Stage) this.getScene().getWindow();
        double stageX = stage.getWidth(), stageY = stage.getHeight();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // make sure that the size of the of the scene is smaller than the screen size
        stageX = stageX >= screenSize.getWidth() ? stageX - 250 : stageX;
        stageY = stageY >= screenSize.getHeight() ? stageY - 250 : stageY;
        BorderPane p = new BorderPane();
        p.setTop(b);
        p.setCenter(wv);
        Scene content = new Scene(p, stageX, stageY);
        sceneStack.push(content);
        content.getStylesheets().add("MessengerStyle.css");
        stage.setScene(sceneStack.peek());
    }


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
     *
     * @param root
     */
    private void addOptionsOnMessage(BorderPane root) throws FileNotFoundException {

        firstRowOptions = new HBox();
        secondRowOptions = new HBox();

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
//        secondRowOptions.getChildren().add(allLabels);
        secondRowOptions.setVisible(false);


        markEmail = new ImageView( mark);
        HBox.setMargin(markEmail, new Insets(1, 5, 1, 1));
        // TODO: set the replyEmail event to interact with ComposerManger
        replyEmail= new ImageView(reply);
        HBox.setMargin(replyEmail, new Insets(1, 5, 1, 1));
        addToTrash = new ImageView(trash);
        HBox.setMargin(addToTrash, new Insets(1, 5, 1, 1));

        firstRowOptions.getChildren().addAll(replyEmail,forwardEmail, labelEmail, downloadAttach, markEmail,addToTrash );
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


    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Text getDateField() {
        return dateField;
    }

    public void setDateField(String text) {
        dateField.setText("Sent: " + text);
    }
}

