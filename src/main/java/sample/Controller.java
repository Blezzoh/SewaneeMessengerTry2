package sample;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller extends Application {

    private final TextField searchField = new TextField();


    @Override
    public void start(Stage primaryStage) {

        //Container of the Interface
        BorderPane root = new BorderPane();

        HBox bottomMenu = makeBottomMenu();

        TilePane center = new TilePane();
        center.setHgap(5);
        center.setVgap(5);
        center.setPadding(new Insets(8, 0, 8, 8));
        for (int i = 0; i < 20; i++) {
            center.getChildren().add(new MessageItem("danny", "my bomb ass car" + i, "Hi, i wanted to ask you if you would like to do to the trip...", "http://4.bp.blogspot.com/-SjsG6gvCasI/Ve6PJxhPEiI/AAAAAAAAFYU/dYvGfnIxPzk/s1600/Kundwa%2BDoriane%2Brwanda.jpg"));

        }
        MessageItem item = new MessageItem("me", "success is a must", "Dear Blase, \n you better try harder", "http://hive.sewanee.edu/iradub0/webDevelopment/search.png");
        center.getChildren().add(item);
        //center.setStyle("" + "-fx-background-color: rgba(195, 0, 255, 0.5);");
        root.setBottom(bottomMenu);
        ScrollPane scrollPane = new ScrollPane(center);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 900, 700);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                String str = "qwertyuioplkjgfdsazxcvbnmAQWSEDRFTGYHUJIKOLPMNBVCXZ1234567890";
                if (str.contains(event.getText())) {
                    System.out.println("you pressed:" + event.getText());
                    insertText(searchField);
                }

                //System.out.println("([A-Za-z1-9])");

            }
        });
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (event.getClickCount() == 1) {
                    searchField.setVisible(false);

                }
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
        System.out.println(bottomMenu.getHeight());


    }


    private HBox makeBottomMenu() {
        HBox lowerMenu = new HBox();
        Rectangle menuRec = new Rectangle(100, 60);
        Rectangle searchRect = new Rectangle(100, 60);


        HBox.setHgrow(menuRec, Priority.max(Priority.SOMETIMES, Priority.ALWAYS));
        HBox.setHgrow(searchField, Priority.max(Priority.SOMETIMES, Priority.ALWAYS));
        HBox.setHgrow(searchRect, Priority.max(Priority.SOMETIMES, Priority.ALWAYS));
        HBox.setMargin(searchField, new Insets(10, 25, 10, 25));
        lowerMenu.setPrefHeight(65);
        lowerMenu.setMaxHeight(65);
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
        System.out.println(menuRec.xProperty().toString() + "\n" + searchRect.getArcHeight());

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
                             bool = true;
                             insertText(searchField);
                         }

                     }
                 }
                );
        lowerMenu.getChildren().addAll(menuRec, searchField, searchRect);


        return lowerMenu;

    }

    private void insertText(TextField t) {

        //searchField.setOpacity(0.1);
        t.setVisible(true);

        ScaleTransition st = new ScaleTransition(Duration.millis(500), t);
        st.setFromX(0f);
        st.setToX(1f);
        st.setCycleCount(1);
        st.setAutoReverse(true);

        st.play();


    }


    public static void main(String[] args) {
        launch(args);
    }

}