package sample;

import javafx.animation.*;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller extends Application {

    @Override
    public void start(Stage primaryStage) {

        //Container of the Interface
        BorderPane root = new BorderPane();

        HBox bottomMenu = makeBottomMenu();

        TilePane center = new TilePane();
        center.setHgap(5);
        center.setVgap(5);
        center.setPadding(new Insets(8,0,8,8));
        for (int i = 0; i <20 ; i++) {
            center.getChildren().add(new MessageItem("danny", "my bomb ass car"+ i, "in Nafgbjsabsfiugbsubvduiobsoisfubsoib oisbo nobsh,...", "http://hive.sewanee.edu/iradub0/webDevelopment/search.png"));

        }
        MessageItem item = new MessageItem("me", "success is a must", "Dear Blase, \n you better try harder", "http://hive.sewanee.edu/iradub0/webDevelopment/search.png");
        center.getChildren().add(item);
        center.setStyle("" + "-fx-background-color: rgba(195, 0, 255, 0.5);");
        root.setCenter(center);
        root.setBottom(bottomMenu);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 900, 700));
        primaryStage.show();


    }


    private HBox makeBottomMenu(){
        HBox lowerMenu = new HBox();
        Rectangle menuRec = new Rectangle(100, 60);
        Rectangle searchRect = new Rectangle(100, 60);
        TextField searchField = new TextField();


        HBox.setHgrow(menuRec, Priority.max(Priority.SOMETIMES, Priority.ALWAYS));
        HBox.setHgrow(searchField, Priority.max(Priority.SOMETIMES, Priority.ALWAYS));
        HBox.setHgrow(searchRect, Priority.max(Priority.SOMETIMES, Priority.ALWAYS));
        HBox.setMargin(searchField, new Insets(10, 25, 10, 25));
        lowerMenu.setPrefHeight(65);
        lowerMenu.setPadding(new Insets(2, 2, 2, 2));
        searchField.setPrefHeight(40);
        searchField.setStyle(""
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 16;");
        lowerMenu.setStyle("" + "-fx-background-radius: 5px");
        lowerMenu.setStyle("" + "-fx-border-color: rgba(93,56,107,0.5)");

        Image search = new Image("http://hive.sewanee.edu/iradub0/webDevelopment/search.png");
        Image menu = new Image("http://hive.sewanee.edu/iradub0/webDevelopment/menu.png");


        searchRect.setFill(new ImagePattern(search));
        menuRec.setFill(new ImagePattern(menu));

        menuRec.setArcWidth(10);
        menuRec.setArcHeight(10);
        searchRect.setArcWidth(10);
        searchRect.setArcHeight(10);
        System.out.println(menuRec.xProperty().toString() +"\n" +searchRect.getArcHeight());

        searchField.setVisible(false);


        searchRect.setOnMouseClicked
                (new EventHandler<MouseEvent>() {

                     Boolean bool = true;

                     @Override
                     public void handle(MouseEvent event) {


                         if (bool) {
                             searchField.setVisible(false);
                             bool = false;
                         } else {
                             //searchField.setOpacity(0.1);
                             searchField.setVisible(true);
                             bool = true;
                             ScaleTransition st = new ScaleTransition(Duration.millis(500), searchField);
                             st.setFromX(0f);
                             st.setToX(1f);
                             st.setCycleCount(1);
                             st.setAutoReverse(true);

                             st.play();
                         }


                     }
                 }
                );
        lowerMenu.getChildren().addAll(menuRec, searchField, searchRect);


        return lowerMenu;

    }


    public static void main(String[] args) {
        launch(args);
    }

}