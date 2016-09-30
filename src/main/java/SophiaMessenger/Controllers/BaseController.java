package SophiaMessenger.Controllers;

import SophiaMessenger.Views.LeftSideMenu;
import SophiaMessenger.Views.TopMenu;
import com.google.api.services.gmail.model.Message;
import de.email.core.Inbox;
import de.email.core.MessageQuery;
import de.email.core.SearchQueries;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Created by daniel on 9/6/16.
 *
 * @author Daniel Evans
 */
public class BaseController extends BorderPane {

    private TopMenu topMenu;
    private ComposerManager composerManager;
    private MessagesViewManager messagesViewManager;
    private Inbox inbox;
    private List<Message> messages;
    private Deque<Scene> sceneStack;
    private LeftSideMenu leftSideMenu;

    public BaseController(String accountEmailAddress) {
        super();
//        this.setStyle("-fx-background-color: black");
        this.getStyleClass().add("BaseController");
        inbox = new Inbox(accountEmailAddress);
        MessageQuery mqm = new MessageQuery(inbox, SearchQueries.INBOX);
        messages = mqm.retrieveMessages();

        topMenu = new TopMenu();
        leftSideMenu = new LeftSideMenu();
        this.setTop(topMenu);
        composerManager = new ComposerManager(inbox);
        topMenu.getComposeButton()
                .setOnMousePressed(e -> composerManager.createNewMessage());
        this.setRight(composerManager);

        sceneStack = new ArrayDeque<>();

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


        leftSideMenu.setEvents(messagesViewManager, inbox);

        this.setCenter(messagesViewManager);
        this.getCenter().setStyle("-fx-background-color: transparent");
        this.setStyle("-fx-background-color: transparent");

        this.setLeft(leftSideMenu);
    }

    public void pushStack(Scene scene) {
        sceneStack.push(scene);
    }

    public void popStack() {
        sceneStack.pop();
    }
}
