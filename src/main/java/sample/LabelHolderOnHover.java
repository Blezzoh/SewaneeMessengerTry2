package sample;

import com.danielevans.email.Authenticator;
import com.danielevans.email.LabelMaker;
import com.google.api.services.gmail.model.Label;
import javafx.geometry.Insets;
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
 */
public class LabelHolderOnHover extends VBox{

    private TextField newLabel;

    public LabelHolderOnHover  (Authenticator auth){

        super();

        String [] allLabels = getAllTheLabels(auth);

        Text title = new Text("Add To Label");
        this.getChildren().add(title);
        if (allLabels!= null){
            for (int i = 0; i < allLabels.length; i++) {
                MessengerLabel label = new MessengerLabel(allLabels[i]);
                this.getChildren().add(label);
                setMargin(label, new Insets(0,0,3,0));
            }
        }
        HBox addNewLabel = new HBox();
        Rectangle plus = new Rectangle(20,20, new ImagePattern(new Image("https://image.freepik.com/free-icon/add-button-with-plus-symbol-in-a-black-circle_318-48599.png", 20,20, true,false,false)));
        plus.setStyle("-fx-border-color: black");
        newLabel =new TextField();
        MessageItem.setSize(newLabel, 75.0, 75.0);
        addNewLabel.getChildren().addAll(plus, newLabel);
        plus.setOnMouseEntered(e -> plus.setStyle("-fx-border-color: aqua"));
        plus.setOnMouseExited(e-> plus.setStyle("-fx-border-color: black"));
        plus.setOnMouseClicked(e-> addToLabel(auth));

        MessageItem.setSize(addNewLabel, 100.0, 100.0);
        this.getChildren().add(addNewLabel);
        this.setPadding(new Insets(5));

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
}
