package SophiaMessenger.Controllers;

import SophiaMessenger.Views.TopMenu;
import com.google.api.services.gmail.model.Message;
import de.email.core.Inbox;
import de.email.core.MessageQuery;
import de.email.core.SearchQueries;
import de.email.database.MessageTableManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
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
    private Inbox inbox;
    private List<Message> messages;
    private BorderPane root;
    private Stack<Scene> sceneStack;
    private MessagesViewManager messagesViewManager;
    private ComposerManager right;
    private TopMenu topMenu;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {

        // inbox gives access to the user's gmail messages using an authenticator
        inbox = new Inbox("evansdb0@sewanee.edu");
        MessageQuery mqm = new MessageQuery(inbox, SearchQueries.INBOX);
        // loads the user's inbox
        messages = mqm.retrieveMessages();

        try {
            MessageTableManager.createMessageTable();
            MessageTableManager.fillTable(inbox);
            MessageTableManager.updateMessageTable(inbox);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        long initTime = System.currentTimeMillis();
        root = new BorderPane();
        topMenu = new TopMenu();
        topMenu.getComposeButton().setOnMousePressed(event -> right.createNewMessage());
        root.setTop(topMenu);
        right = new ComposerManager(inbox);
        root.setRight(right);

        // creating title for application and scene
        primaryStage.setTitle("Sophia Messenger");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("MessengerStyle.css");

        sceneStack = new Stack<>();
        sceneStack.push(scene);

        messagesViewManager = new MessagesViewManager(inbox, messages, sceneStack);

        topMenu.getSearchField().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                javafx.scene.control.TextField searchField = (javafx.scene.control.TextField) e.getSource();
                MessageQuery messageQuery = new MessageQuery(inbox, searchField.getText());
                if (messageQuery.emptyMessages()) {
                    // display search returns 0 messages
                } else {
                    messages = messageQuery.retrieveMessages();
                    messagesViewManager.setMessagesInfo(messages);
                }
            }
        });

        root.setCenter(messagesViewManager);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNIFIED);
        primaryStage.setWidth(Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 50);
        primaryStage.setHeight(Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 100);
        System.out.println("init time: " +
                (System.currentTimeMillis() - initTime) / 1000.0);
        primaryStage.show();
    }
}