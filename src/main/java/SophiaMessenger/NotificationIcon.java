package SophiaMessenger;

import javafx.geometry.Insets;
import javafx.scene.control.Label;

/**
 * Created by iradu_000 on 6/7/2016.
 * @author Blaise Iradukunda
 * this Class provides the notification label layout
 */
public class NotificationIcon extends Label {


    public NotificationIcon(String text){
        super();
        this.setText(text);
        this.setPadding(new Insets(3));
    }

    private void addToLabelByName(String text) {

    }

    public double getLableWidth(){

        return this.getWidth();
    }

    public double getLableHeight(){

        return this.getHeight();
    }

    public String getLabelText() {
        return this.getText();
    }

    public void setLabelText(String text) {
        this.setText(text);
    }

}
