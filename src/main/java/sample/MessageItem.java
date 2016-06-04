package sample;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Created by iradu_000 on 6/3/2016.
 */
public class MessageItem extends HBox {
    private HBox msgItem;
    private VBox picField;
    private VBox msgSummary;
    private HBox options;
    private VBox container;
    private Text senderField;
    private Text subjectField;
    private Text snippetField;
    private Image imageField;

    public MessageItem() {
        super();
    }

    public MessageItem(String sender, String subject, String snippet, String imageUrl) {
        this();

        senderField = new Text(sender);
        subjectField = new Text("S: " + subject + "\n");
        snippetField = new Text(snippet);
        subjectField.setWrappingWidth(190);
        //font families
        System.out.println(javafx.scene.text.Font.getFamilies());
        subjectField.setFont(Font.font("Constantia", FontWeight.BLACK, 13));
        ///subjectField.setStyle("" + "-fx-font: bold;" +" -fx-font-family: ");
        snippetField.setWrappingWidth(190);
        senderField.setWrappingWidth(100);

        imageField = new Image(imageUrl, 80, 0, true, true, false);
        ImagePattern imageView = new ImagePattern(imageField);
        //ImageView imageView = new ImageView(imageField);


        Rectangle canvas = new Rectangle(imageField.getWidth(), imageField.getHeight(), imageView);
        canvas.setArcHeight(20);
        canvas.setArcWidth(20);
        picField = new VBox(canvas, senderField);
        msgSummary = new VBox(subjectField, snippetField);
        options = new HBox();
        container = new VBox(msgSummary, options);

        // msgItem = new HBox(picField, container);

        getChildren().addAll(picField, container);

        //setSize(imageView, 100.0, 100.0);
        setSize(this, 300.0, 200.0);
        setSize(picField, 100.0, 200.0);
        setSize(msgSummary, 200.0, 160.0);
        setSize(container, 200.0, 200.0);
        setStyle("-fx-padding: 10px; " + "-fx-border-radius: 10px;" + "-fx-border-style: solid;" + "-fx-border-color: #67007c;");
        container.setBackground(new Background(new BackgroundFill(Color.rgb(208, 0, 255, 0.4), new CornerRadii(5), new Insets(0))));
        container.setPadding(new Insets(4));


    }

    private static void setSize(Node node, Double w, Double h) {

        node.prefHeight(h);
        node.maxHeight(h);
        node.minHeight(h);

        node.maxWidth(w);
        node.minWidth(w);
        node.prefWidth(w);
    }
}

