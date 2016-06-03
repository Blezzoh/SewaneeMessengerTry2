package sample;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

    public MessageItem(){
        super();
    }

    public MessageItem(String sender, String subject, String snippet, String imageUrl){
        this();
        senderField = new Text(sender);
        subjectField = new Text(subject);
        snippetField = new Text(snippet);
        subjectField.setWrappingWidth(190);
        ///subjectField.setStyle("" + "-fx-font: bold;" +" -fx-font-family: ");
        snippetField.setWrappingWidth(190);
        senderField.setWrappingWidth(100);

        imageField = new Image(imageUrl, 50, 50, true, true, true);
        ImageView imageView = new ImageView(imageField);


        picField = new VBox(imageView, senderField);
        msgSummary = new VBox(subjectField, snippetField);
        options = new HBox();
        container = new VBox(msgSummary, options);

       // msgItem = new HBox(picField, container);

        getChildren().addAll(picField,container);

        setSize(imageView, 100.0, 100.0);
        setSize(this, 300.0,200.0);
        setSize(picField, 100.0,200.0);
        setSize(msgSummary,200.0, 160.0);
        setSize(container,200.0,200.0);
        setStyle("" + "-fx-background-color: rgba(195, 0, 255, 1);");

    }

    private static void  setSize (Node node, Double w, Double h){

        node.prefHeight(h);
        node.maxHeight(h);
        node.minHeight(h);

        node.maxWidth(w);
        node.minWidth(w);
        node.prefWidth(w);
    }
}

