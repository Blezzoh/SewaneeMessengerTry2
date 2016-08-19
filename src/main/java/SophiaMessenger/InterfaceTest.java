package SophiaMessenger;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;


public class InterfaceTest extends Application {

    static final String COMPOSE_STYLE = "-fx-background-color: rgba(7, 171,202,.7); -fx-font-family: Trebuchet MS; -fx-font-size: 13px; -fx-font-weight: bold; -fx-border-color: white;"
            + "-fx-effect: dropshadow(gaussian, black, 2, 0, 3, 3); -fx-border-insets: 3px; -fx-border-width: 2px; -fx-text-fill: white";

    public static void main(String[] args) throws Exception {
        launch(args);
    }



    @Override
    public void start(final Stage stage) throws Exception {
        Image search = new Image(new FileInputStream(new File(System.getProperty("user.home"), "IdeaProjects/SewaneeMessengerTry2/src/main/resources/Search-white.png")),30, 15, false, true);
        ImageView view = new ImageView(search);
        Button searchButton = new Button();
        searchButton.setStyle(COMPOSE_STYLE);
        searchButton.setText("SEARCH");
        searchButton.setGraphic(view);
        Pane pane = new Pane(searchButton);
        stage.setScene(new Scene(pane, 60,60));
        stage.show();

    }
}