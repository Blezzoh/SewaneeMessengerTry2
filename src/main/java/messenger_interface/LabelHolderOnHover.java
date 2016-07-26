package messenger_interface;

import com.danielevans.email.Authenticator;
import com.danielevans.email.LabelMaker;
import com.google.api.services.gmail.model.Label;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;

/**
 * Created by Blase & Danny on 6/8/2016.
 * Class that displays all the labels to allow the user to add a message to a label
 * @author Blaise Iradukunda and Daniel Evans
 */
public class LabelHolderOnHover extends VBox {

    private TextField newLabel;


    public LabelHolderOnHover  (Authenticator auth){

        super();
        String [] allLabels = getAllTheLabels(auth);
        Text title = new Text("Add To Label");
        VBox vBox = new VBox();
        MessageItem.setSize(vBox, 60, 110);
        this.getChildren().add(title);
        ScrollPane scrollPane = new ScrollPane(vBox);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
       /* if (allLabels!= null){
            for (int i = 0; i < allLabels.length; i++) {
                NotificationIcon label = new NotificationIcon(allLabels[i]);
                label.setOnMouseClicked(event -> {
                    System.out.println(label.getText());
                    label.getParent().setVisible(false);
                });
                vBox.getChildren().add(label);
                setMargin(label, new Insets(0,0,3,0));
            }
        }*/
        System.out.println(scrollPane.isResizable());
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);
        MessageItem.setSize(scrollPane, 62, 112);
        HBox addNewLabel = new HBox();
        Rectangle plus = new Rectangle(20,20, new ImagePattern(new Image("https://image.freepik.com/free-icon/add-button-with-plus-symbol-in-a-black-circle_318-48599.png", 20,20, true,false,false)));
        plus.setStyle("-fx-border-color: black");
        newLabel =new TextField();
        MessageItem.setSize(newLabel, 75.0, 20.0);
        addNewLabel.getChildren().addAll(plus, newLabel);
        plus.setOnMouseEntered(e -> plus.setStyle("-fx-border-color: aqua"));
        plus.setOnMouseExited(e-> plus.setStyle("-fx-border-color: black"));
        plus.setOnMouseClicked(e-> addToLabel(auth));

        MessageItem.setSize(addNewLabel, 100, 0);
        this.getChildren().addAll(scrollPane,addNewLabel);
        this.setPadding(new Insets(0,3,0,3));
        this.setStyle("-fx-background-color: beige");
    }


    private void addToLabel(Authenticator auth) {

        int counter = 0;

        if (newLabel.getText().length() != 1){
            for(String t :getAllTheLabels(auth)) {

                if (newLabel.getText().equals(t)) {
                    counter++;
                    break;
                }
            }
        }

        if (counter == 0){
            createLabel (newLabel.getText());
        }
        moveMessageToLabel(newLabel.getText());
        newLabel.setText(" ");
        this.setVisible(false);

    }

    private void createLabel(String text) {
    }

    private void moveMessageToLabel(String text) {
    }

    private String[] getAllTheLabels(Authenticator authenticator) {

        List<Label> allLabel = LabelMaker.listLabels(authenticator);
        String[] labels = new String[allLabel.size()];
        for (int i = 0; i < allLabel.size(); i++) {
            labels[i] = allLabel.get(i).getName();
        }
        return labels;
    }
    private String getLabelText(NotificationIcon label){
        return label.getText();
    }

}
