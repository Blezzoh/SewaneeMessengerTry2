package sample;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * this class provides the layout and functionality of a label menu when the user hovers on a label icon inside a message item
 * Created by iradu_000 on 6/7/2016.
 * @author Blaise Iradukunda
 */
public class MessengerLabel extends TextFlow {

    private Text label;

    public MessengerLabel(){
        super();
        label = new Text();
        label.setFont(new Font("verdana", 12));
        label.setTextAlignment(TextAlignment.CENTER);
        MessageItem.setSize(this, 100.0, 100.0);
        this.getChildren().add(label);
        this.setBackground(new Background(new BackgroundFill(Color.rgb(208, 0, 255, 1), new CornerRadii(5), new Insets(0))));
        this.setOnMouseClicked(e-> {
            addToLabelByName(label.getText());
            this.getParent().setVisible(false);
        });

    }
    public  MessengerLabel(String text){
        this();
        label.setText(text);
        /*label = new Text(text);
        MessageItem.setSize(this, 100.0, 20.0);
        this.getChildren().add(label);
        this.setBackground(new Background(new BackgroundFill(Color.rgb(208, 0, 255, 1), new CornerRadii(5), new Insets(0))));
        this.setOnMouseClicked(e-> addToLabelByName(label.getText()));*/
    }

    private void addToLabelByName(String text) {

    }

    public void setLabelText (String text) {
        label.setText(text);
    }

    public double getLableWidth(){

        return this.getWidth();
    }
    public double getLableHeight(){

        return this.getHeight();
    }
}
