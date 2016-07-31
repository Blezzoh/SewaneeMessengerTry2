package messenger_interface;

import com.danielevans.email.FullMessage;
import com.danielevans.email.MessageParser;
import javafx.geometry.Insets;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by iradu_000 on 6/3/2016.
 * @author Blaise Iradukunda
 */
public class MessageItem extends HBox {
    private Text senderField, subjectField, snippetField, dateField;
    private HBox secondRowOptions;
    private HBox firstRowOptions;
    private Button b = new Button("BACK");
    private BorderPane p = new BorderPane();
    private Stage stage;
    private static final String STYLE_ON_ENTER = "-fx-background-color: aquamarine;"+"-fx-background-radius: 0px;"+"-fx-padding: 10px; " + "-fx-border-radius: 0px;" + "-fx-border-style: solid;" + "-fx-border-color: #67007c;";
    private static final String STYLE_ON_EXIT = "-fx-background-color: white;"+"-fx-padding: 10px; " + "-fx-border-radius: 0px;" + "-fx-border-style: solid;" + "-fx-border-color: #67007c;" ;

    private FullMessage fm;
    private ImageView downloadAttach, forwardEmail, markEmail, replyEmail, addToTrash;
    private Scene bodyScene, originalScene;
    private WebView messageBody = new WebView();
    private WebEngine engine = messageBody.getEngine();
    private int originalInt = 0;
    private ImageView labelEmail;


    public MessageItem(FullMessage m, String imageUrl) throws IOException {

        super();
        this.fm = m;
        senderField = new Text(MessageParser.parseSenderFromEmail(m));
        subjectField = new Text("S: " + m.getSubject() + "\n");
        snippetField = new Text(m.getSnippet());
        dateField = new Text("Sent: " + MessageParser.parseDate(fm.getDate()));
        subjectField.setWrappingWidth(190);
        subjectField.setFont(Font.font("Constantia", FontWeight.BLACK, 13));
        snippetField.setWrappingWidth(190);
        dateField.setWrappingWidth(190);
        senderField.setWrappingWidth(100);
        addOptionsOnMessage();
        // add the image to the message item
        Image imageField = new Image(imageUrl, 80, 0, true, true, false);
        ImagePattern imageView = new ImagePattern(imageField);
//        ImageView imageView = new ImageView(imageField);


        Rectangle canvas = new Rectangle(imageField.getWidth(), imageField.getHeight(), imageView);
        canvas.setArcHeight(20);
        canvas.setArcWidth(20);
        VBox picField = new VBox(canvas, senderField, dateField);
        VBox msgSummary = new VBox(subjectField, snippetField);

        VBox options = new VBox();
        options.getChildren().addAll(firstRowOptions);
        setMargin(options, new Insets(10,10,10,10));
        VBox container = new VBox(msgSummary, options);

        getChildren().addAll(picField, container);

//        setSize(imageView, 100.0, 100.0);
        setSize(this, 300.0, 200.0);
        setSize(picField, 100.0, 200.0);
        setSize(msgSummary, 200.0, 160.0);
        setSize(container, 200.0, 200.0);
        setStyle("-fx-padding: 10px; "+"-fx-background-color:white;" + "-fx-border-radius: 0px;" + "-fx-border-style: solid;" + "-fx-border-color: #66007a;");
        container.setBackground(new Background(new BackgroundFill(Color.rgb(208, 0, 255, 0.4), new CornerRadii(5), new Insets(0))));
        container.setPadding(new Insets(4));

        setOnMouseEntered(event -> this.setStyle(STYLE_ON_ENTER));
        setOnMouseExited(event -> this.setStyle(STYLE_ON_EXIT));



        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5. * 0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
        setEffect(dropShadow);


        snippetField.setOnMouseEntered(event -> underline(snippetField));
        subjectField.setOnMouseEntered(event -> underline(subjectField));
        subjectField.setOnMouseExited(event -> removeUnderline(subjectField));
        snippetField.setOnMouseExited(event -> removeUnderline(snippetField));
        engine.loadContent(fm.getBestMessageBody());

        subjectField.setOnMouseClicked(event -> showContent());
        snippetField.setOnMouseClicked(event -> showContent());
        b.setOnMouseClicked(event -> goBack());

        p.setCenter(messageBody);
        p.setTop(b);
    }

    public FullMessage getFm() {
        return fm;
    }

    public void setFm(FullMessage fm) {
        this.fm = fm;
        snippetField.setText(fm.getSnippet());
        subjectField.setText(fm.getSubject());
        senderField.setText(MessageParser.parseSenderFromEmail(fm));
        dateField.setText("Sent: " + MessageParser.parseDate(fm.getDate()));

        String bodyText = fm.getBestMessageBody();

        // loadContent(String) will return and do nothing if bodyText is null
        // therefore the old bodyText from previous message Item will be loaded when the
        // user clicks on the snippet or the subject
        if(!FullMessage.testForHTML(bodyText))
            // load plain text version
            engine.loadContent(bodyText, "text/plain");
        else
            // load html version
            engine.loadContent(bodyText);
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
        getStage().setScene(getOriginalScene());
    }

    private void showContent() {
        getStage().setScene(bodyScene);
    }

    private Stage getStage(){

        if (originalInt == 0){
            stage = (Stage) this.getScene().getWindow();
            originalScene = this.getScene();
            bodyScene = new Scene(p, stage.getX() + 500, stage.getY() + 500);
            originalInt++;
        }
       return stage;
    }

    public Scene getOriginalScene() {
        return originalScene;
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
     **/
    private void addOptionsOnMessage() throws FileNotFoundException {

        firstRowOptions = new HBox();
        secondRowOptions = new HBox();

        Image download = new Image(new FileInputStream(new File(System.getProperty("user.home"), "IdeaProjects/SewaneeMessengerTry2/src/main/resources/download.png")), 20, 20, true, true);
        Image forward = new Image(new FileInputStream(new File(System.getProperty("user.home"), "IdeaProjects/SewaneeMessengerTry2/src/main/resources/forward.png")), 20, 20, true, true);
        Image label = new Image(new FileInputStream(new File(System.getProperty("user.home"), "IdeaProjects/SewaneeMessengerTry2/src/main/resources/label.png")), 20, 20, true, true);
        Image mark = new Image(new FileInputStream(new File(System.getProperty("user.home"), "IdeaProjects/SewaneeMessengerTry2/src/main/resources/mark.png")), 20, 20, true, true);
        Image reply = new Image(new FileInputStream(new File(System.getProperty("user.home"), "IdeaProjects/SewaneeMessengerTry2/src/main/resources/reply.png")), 20, 20, true, true);
        Image trash = new Image(new FileInputStream(new File(System.getProperty("user.home"), "IdeaProjects/SewaneeMessengerTry2/src/main/resources/trash.png")), 20, 20, true, true);

        downloadAttach = new ImageView(download);
        setMargin(downloadAttach, new Insets(1,5,1,1));
        forwardEmail = new ImageView(forward);
        setMargin(forwardEmail, new Insets(1,5,1,1));
        labelEmail = new ImageView(label);
        setMargin(labelEmail, new Insets(1,5,1,1));
//        secondRowOptions.getChildren().add(allLabels);
        secondRowOptions.setVisible(false);


        markEmail = new ImageView( mark);
        setMargin(markEmail, new Insets(1,5,1,1));
        replyEmail= new ImageView(reply);
        setMargin(replyEmail, new Insets(1,5,1,1));
        addToTrash = new ImageView(trash);
        setMargin(addToTrash, new Insets(1,5,1,1));

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


}

