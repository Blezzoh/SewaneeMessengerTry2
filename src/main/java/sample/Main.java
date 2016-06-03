package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        HBox msgItem = new HBox();
        VBox picField = new VBox();
        VBox msgSummary = new VBox();
        HBox menus = new HBox();
        VBox container = new VBox();

        msgItem.setMinWidth(200);
        msgItem.setPrefWidth(200);
        msgItem.setMaxWidth(200);

        msgItem.setMaxHeight(100);
        msgItem.setPrefWidth(100);
        msgItem.setMinWidth(100);

        picField.setMinWidth(50);
        picField.setPrefWidth(50);
        picField.setMaxWidth(50);

        picField.setMaxHeight(100);
        picField.setPrefHeight(100);
        picField.setMinHeight(100);

        msgSummary.setMinWidth(150);
        msgSummary.setPrefWidth(150);
        msgSummary.setMaxWidth(150);

        msgSummary.setMaxHeight(80);
        msgSummary.setPrefWidth(80);
        msgSummary.setMinHeight(80);

        msgSummary.setStyle(""+ "-fx-border-color: crimson");
        picField.setStyle(""+ "-fx-border-color: darkorchid");
        menus.setStyle("" + "-fx-border-color: aqua");
        msgItem.setStyle("" + "-fx-border-color: black");



        Image img = new Image("http://hive.sewanee.edu/iradub0/webDevelopment/search.png", 40 , 40, true, true);
        Text name = new Text("from Blase");
        setSize(name, 150.0, 20.0);
        Text subject = new Text("Partners Meeting");
        setSize(subject, 150.0, 20.0);
        Text hint = new Text("We have a partner meeting at 6:30. there is gonna be .....");
        setSize(hint, 150.0, 40.0);
        hint.setWrappingWidth(150);
        System.out.println(hint.getWrappingWidth());



        picField.getChildren().add(new ImageView(img));
        msgSummary.getChildren().addAll(name, subject, hint);

        container.getChildren().addAll(msgSummary,menus);

        msgItem.getChildren().addAll(picField, container);
        StackPane root = new StackPane(msgItem);


        System.out.println(img.getHeight() +" " + img.getWidth());


        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    private void  setSize (Node node, Double w, Double h){

        node.prefHeight(h);
        node.maxHeight(h);
        node.minHeight(h);

        node.maxWidth(w);
        node.minWidth(w);
        node.prefWidth(w);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
