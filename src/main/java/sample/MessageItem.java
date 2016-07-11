package sample;

import com.danielevans.email.Authenticator;
import com.danielevans.email.FullMessage;
import com.danielevans.email.MessageParser;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by iradu_000 on 6/3/2016.
 */
public class MessageItem extends HBox {
    private HBox msgItem;
    private VBox picField;
    private VBox msgSummary;
    private VBox options;
    private VBox container;
    private Text senderField;
    private Text subjectField;
    private Text snippetField;
    private Image imageField;
    private String messageId;
    private static final String filePath = "/home/daniel/IdeaProjects/SewaneeMessengerTry2/src/main/resources/";
    private FullMessage m;

    public MessageItem() {
        super();
    }

    public MessageItem(FullMessage m, String imageUrl, ComposeMessage composer) throws FileNotFoundException {

        this();
        Authenticator auth = m.getAuth();
        this.m = m;

        messageId = m.getId();
        senderField = new Text(m.getFrom());
        subjectField = new Text("S: " + m.getSubject() + "\n");
        snippetField = new Text(m.getSnippet());
        subjectField.setWrappingWidth(190);
        //font families
        subjectField.setFont(Font.font("Constantia", FontWeight.BLACK, 13));
        ///subjectField.setStyle("" + "-fx-font: bold;" +" -fx-font-family: ");
        snippetField.setWrappingWidth(190);
        senderField.setWrappingWidth(100);


        /**
         * set of options that can be applied to a Message Item
         **/
        HBox firstRowOptions = new HBox();
        HBox secondRowOptions = new HBox();

        Image download = new Image(new FileInputStream(new File(filePath + "download.png")), 20, 20, true, true);
        Image forward = new Image(new FileInputStream(new File(filePath + "forward.png")), 20, 20, true, true);
        Image label = new Image(new FileInputStream(new File(filePath + "label.png")), 20, 20, true, true);
        Image mark = new Image(new FileInputStream(new File(filePath + "mark.png")), 20, 20, true, true);
        Image reply = new Image(new FileInputStream(new File(filePath + "reply.png")), 20, 20, true, true);
        Image trash = new Image(new FileInputStream(new File(filePath + "trash.png")), 20, 20, true, true);

        ImageView downloadAttach = new ImageView(download);
        setMargin(downloadAttach, new Insets(1,5,1,1));
        ImageView forwardEmail = new ImageView(forward);
        setMargin(forwardEmail, new Insets(1,5,1,1));
        ImageView labelEmail = new ImageView(label);
        setMargin(labelEmail, new Insets(1,5,1,1));
        ImageView markEmail = new ImageView( mark);
        setMargin(markEmail, new Insets(1,5,1,1));
        ImageView replyEmail= new ImageView(reply);
        setMargin(replyEmail, new Insets(1,5,1,1));
        ImageView addToTrash = new ImageView(trash);
        setMargin(addToTrash, new Insets(1,5,1,1));

        firstRowOptions.getChildren().addAll(replyEmail,forwardEmail, labelEmail, downloadAttach, markEmail,addToTrash );
        /**
         *----------------
         */


        replyEmail.setOnMouseClicked(e ->
                {
                    composer.getBodyText().setText(m.getMessageBody());
                    System.out.println(m.getMessageBody());
                    composer.getEmailAddress().setText
                            (MessageParser.parseEmailAddress(m.getFrom()));
                    composer.getSubject().setText("REPLY: " + m.getSubject());
                }
        );

        forwardEmail.setOnMouseClicked(e ->
                {

                }
        );


        imageField = new Image(imageUrl, 80, 0, true, true, false);
        ImagePattern imageView = new ImagePattern(imageField);
//        ImageView imageView = new cd ImageView(imageField);


        Rectangle canvas = new Rectangle(imageField.getWidth(), imageField.getHeight(), imageView);
        canvas.setArcHeight(20);
        canvas.setArcWidth(20);
        picField = new VBox(canvas, senderField);
        msgSummary = new VBox(subjectField, snippetField);
        options = new VBox();
        options.getChildren().addAll(firstRowOptions);
        setMargin(options, new Insets(10,10,10,10));
        container = new VBox(msgSummary, options);

         msgItem = new HBox(picField, container);

        getChildren().addAll(picField, container);

//        setSize(imageView, 100.0, 100.0);
        setSize(this, 300.0, 200.0);
        setSize(picField, 100.0, 200.0);
        setSize(msgSummary, 200.0, 160.0);
        setSize(container, 200.0, 200.0);
        setStyle("-fx-padding: 10px; " + "-fx-border-radius: 10px;" + "-fx-border-style: solid;" + "-fx-border-color: #67007c;");
        container.setBackground(new Background(new BackgroundFill(Color.rgb(208, 0, 255, 0.4), new CornerRadii(5), new Insets(0))));
        container.setPadding(new Insets(4));


    }
    public void setSenderField(String text) {
        senderField.setText(text);
    }
    public void setSubjectField(String text) {
        subjectField.setText(text);
    }
    public void setSnippetField(String text) {
        snippetField.setText(text);
    }

    protected static void setSize(Node node, Double w, Double h) {

        node.prefHeight(h);
        node.maxHeight(h);
        node.minHeight(h);

        node.maxWidth(w);
        node.minWidth(w);
        node.prefWidth(w);
    }
}

