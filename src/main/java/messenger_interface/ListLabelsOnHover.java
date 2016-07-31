package messenger_interface;

import com.danielevans.email.Auth;
import com.danielevans.email.LabelMaker;
import com.google.api.services.gmail.model.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;

/**
 * Created by iradu_000 on 7/19/2016.
 * Lists all the users' labels in a VBox
 */
public class ListLabelsOnHover extends VBox{

    private ListView<String> allLabels;

    public ListLabelsOnHover(Auth auth) {
        super();
        Text title = new Text("Add To Label");
        this.getChildren().add(title);

        String[] labels = getAllTheLabels(auth);
        allLabels = new ListView<>();
        ObservableList<String> itemLabels = FXCollections.observableArrayList(labels);
        allLabels.setItems(itemLabels);
        allLabels.setPrefWidth(80);
        allLabels.setOrientation(Orientation.VERTICAL);
        HBox addNewLabel = new HBox();
        Rectangle plus = new Rectangle(30,20, new ImagePattern(new Image("https://image.freepik.com/free-icon/add-button-with-plus-symbol-in-a-black-circle_318-48599.png", 20,20, true,false,false)));
        TextField newLabel =new TextField();
        MessageItem.setSize(newLabel,75, 20.0);
        HBox.setHgrow(newLabel, Priority.SOMETIMES);
        addNewLabel.getChildren().addAll(newLabel,plus);
        this.getChildren().addAll(allLabels, addNewLabel);
        this.setStyle("-fx-background-color: aliceblue");
    }

    /**
     * @param auth any class that implements getAuth(), provides account access/modification
     * @return an array of all the Strings
     */

    private String[] getAllTheLabels(Auth auth) {

        List<Label> allLabel = LabelMaker.listLabels(auth);
        String[] labels = new String[allLabel.size() + 1];
        for (int i = 0; i < allLabel.size(); i++) {
            labels[i] = allLabel.get(i).getName();
        }
        return labels;
    }

    public ListView<String> getAllLabels() {
        return allLabels;
    }

}
