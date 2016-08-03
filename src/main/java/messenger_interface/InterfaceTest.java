package messenger_interface;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;

import static messenger_interface.Controller.COMPOSE_STYLE;

public class InterfaceTest extends Application {



    public static void main(String[] args) throws Exception {
        launch(args);
    }



    @Override
    public void start(final Stage stage) throws Exception {
        Image search = new Image(new FileInputStream(new File(System.getProperty("user.home"), "IdeaProjects/SewaneeMessengerTry2/src/main/resources/Search-white.png")),30, 15, false, true);
        ImageView view = new ImageView(search);
        Button searchButton = new Button();
        searchButton.setStyle(COMPOSE_STYLE );
        searchButton.setText("SEARCH");
        searchButton.setGraphic(view);
        Pane pane = new Pane(searchButton);
        stage.setScene(new Scene(pane, 60,60));
        stage.show();

    }
}