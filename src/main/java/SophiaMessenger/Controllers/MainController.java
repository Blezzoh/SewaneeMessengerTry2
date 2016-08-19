package SophiaMessenger.Controllers;

import com.google.api.services.gmail.model.Message;
import de.email.Inbox;
import de.email.database.MessageTableManager;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Daniel Evans and Blaise Iradukunda
 */

public class MainController extends Application {

    static final String COMPOSE_STYLE = "-fx-background-color: rgba(7, 171,202,.7); -fx-font-family: Trebuchet MS; -fx-font-size: 13px; -fx-font-weight: bold; -fx-border-color: white;"
            +"-fx-effect: dropshadow(gaussian, black, 2, 0, 3, 3); -fx-border-insets: 3px; -fx-border-width: 2px; -fx-text-fill: white";
    private static final String TOP_STYLE = "-fx-background-color: rgba(7, 171,202,.7); -fx-padding: 15px; -fx-spacing: 15px; -fx-start-margin: 40px; -fx-border-color:rgba(255, 153, 51, .8);" +
            "-fx-border-radius: 3px" ;
    private final TextField searchField = new TextField();
    private long searchTime;
    private Inbox inbox;
    private List<Message> messages;
    private int itemsPerPage = 15;
    private BorderPane root;
    private Stack<Scene> sceneStack;
    private ComposerManager right;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {

        // inbox gives access to the user's gmail messages using an authenticator
        inbox = new Inbox("iradub0@sewanee.edu");

        // loads the user's inbox
        messages = inbox.getInbox();
        try {
            MessageTableManager.createMessageTable();
            MessageTableManager.fillTable(inbox);
            MessageTableManager.updateMessageTable(inbox);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        long initTime = System.currentTimeMillis();


        // root container of the interface
        root = new BorderPane();

        HBox top = makeTopMenu(root);
        Button composeButton = new Button("Compose");
        composeButton.setStyle(COMPOSE_STYLE);
        top.getChildren().add(0, composeButton);
        composeButton.setOnMousePressed(event -> right.createNewMessage(inbox));
        root.setTop(top);
        right = new ComposerManager();
        root.setRight(right);

        // creating title for application and scene
        primaryStage.setTitle("Sophia Messenger");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("MessengerStyle.css");

        sceneStack = new Stack<>();
        sceneStack.push(scene);
        /*

     _._ _..._ .-',     _.._(`))
    '-. `     '  /-._.-'    ',/
       )         \            '.
      / _    _    |  Mr. Safety \
     |  a    a    /     Pig      |
     \   .-.                     ;
      '-('' ).-'       ,'       ;
         '-;           |     .'
            \           \    /
            | 7  .__  _.-\   \
            | |  |  ``/  /`  /
           /,_|  |   /,_/   /
              /,_/      '`-'

         */

        // on key pressed gives functionality for what user types automatically
        //showing up in the search box
        scene.setOnKeyPressed
                (event ->
                {
                    try {

                        // USER HIT ENTER
                        if (event.getCode() == KeyCode.ENTER) {
                            searchTime = System.currentTimeMillis();
                            userSearchForMessages(root);
                        }
                        char key = event.getText().charAt(0);
                        // USER HIT AN ALPHA-NUMERIC KEY
                        if (Character.isLetterOrDigit(key)) {
                            // checks if searchField is already visible, displays w/ animations if not
                            displaySearchField(searchField);
                        }
                    } catch (StringIndexOutOfBoundsException e) { /* user pressed a non-alphanumeric key */ }
                });
        MessagesManager messagesManager = new MessagesManager(inbox, messages);
        root.setCenter(messagesManager);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNIFIED);
        primaryStage.setWidth(Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 50);
        primaryStage.setHeight(Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 100);
        System.out.println("init time: " +
                (System.currentTimeMillis() - initTime) / 1000.0);
        primaryStage.show();
    }

    public void userSearchForMessages(BorderPane root) {
        // TODO: HANDLE THE CASE WHERE NO MESSAGES ARE RETURNED
        // only search for messages if the searchField is visible
        // and there is some text in it
        if(!searchField.isVisible() || searchField.getText().equals(""))
            return;
        // time is used to show how long search and ui update takes
        /*List<DBMessage> searchMessages =
                inbox.listMessagesMatchingQuery(searchField.getText());*/

        List<Message> tempMessages = inbox.listMessagesMatchingQuery(searchField.getText());
        // this search does not provide any messages, so return from search
        // TODO: display something to user saying the search returned no messages
        searchField.setVisible(false);
        tempMessages = searchReturnsMessages(tempMessages);
        if (tempMessages.size() == 0) {
            return;
        }

        /**
         *  next step is to store emailData on hard drive,
         *  check the ids of the emailData returned in the search against
         *  the ones on the hardrive, show the emailData of the ones matching
         *  the ids
         */
        messages = tempMessages;
    }

    private List<Message> searchReturnsMessages(List<Message> tempMessages) {
        // if tempMessages.size() < 0 return false, if not at least one message
        // returned by search which are stored in tempMessages are not in emailData hashMap return false
        List<Message> filteredMessages = new ArrayList<>();
      /*  for (int i = 0; i < tempMessages.size(); i++) {
            if (emailData.get(tempMessages.get(i).getId()) != null)
                filteredMessages.add(tempMessages.get(i));
        }*/
        return filteredMessages;
    }

    private HBox makeTopMenu(BorderPane root) throws FileNotFoundException {
        HBox topMenu = new HBox();
        Image search = new Image(new FileInputStream(new File(System.getProperty("user.home"), "proj/IdeaProjects/SewaneeMessengerTry2/src/main/resources/Search-white.png")), 30, 15, false, true);
        ImageView view = new ImageView(search);
        Button searchButton = new Button();
        searchButton.setStyle(COMPOSE_STYLE);
        searchButton.setText("SEARCH");
        searchButton.setGraphic(view);



        HBox.setHgrow(searchField, Priority.max(Priority.SOMETIMES, Priority.ALWAYS));
        HBox.setMargin(searchField, new Insets(10, 25, 10, 25));

        topMenu.setPadding(new Insets(2, 2, 2, 2));
        searchField.setStyle("-fx-font-size: 14;");


        searchField.setTranslateY(-3);
        searchField.setVisible(false);

        searchButton.setOnMouseClicked(e ->
        {
            if(searchField.isVisible()) {
                userSearchForMessages(root);
                searchField.setVisible(false);
            } else
                displaySearchField(searchField);
        });

        topMenu.getChildren().addAll( searchButton,searchField);
        topMenu.setStyle(TOP_STYLE);

        return topMenu;
    }

    private void displaySearchField(TextField searchField) {
        if(!searchField.isVisible()) {
            searchField.setVisible(true);
            searchField.requestFocus();

            ScaleTransition st = new ScaleTransition(Duration.millis(375), searchField);
            st.setFromX(0f);
            st.setToX(1f);
            st.setCycleCount(1);
            st.setAutoReverse(true);

            st.play();
        }
    }


}