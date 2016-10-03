package SophiaMessenger.Controllers;

import SophiaMessenger.Views.LeftSideMenu;
import SophiaMessenger.Views.TopMenu;
import com.google.api.services.gmail.model.Message;
import de.email.aux.MessageParser;
import de.email.core.Inbox;
import de.email.core.MessageQuery;
import de.email.database.Config;
import de.email.database.DB;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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
    //    private Deque<Scene> sceneStack;
    private LeftSideMenu leftSideMenu;
    private SceneManager sceneManager;
    private boolean processingSearch = false;


    public BaseController(Inbox inbox, List<Message> messages) {
        super();
        System.out.println("Initializing base controller...");
        this.getStyleClass().add("BaseController");
        this.inbox = inbox;
        this.messages = messages;
        SortedSet<String> eas = new TreeSet<>();
        ResultSet rs = null;
        try {
            rs = DB.selectAll("fromEmail", "DISTINCT", Config.MESSAGES);
        } catch (SQLException e) {
            System.out.println("Unable to retrieve email address from message table");
            e.printStackTrace();
        }
        if (rs != null) {
            try {
                while (rs.next()) {
                    String rsText = rs.getString(1);
                    // extend the AutoSuggest to search the email address and name separately
                    eas.add(MessageParser.
                            parseNameFromMessage(rsText).toLowerCase().trim());
                    // add email address to eas ????
                    /*eas.add(MessageParser.parseEmailAddress(rsText).toLowerCase().trim());*/
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Loading menus...");
        topMenu = new TopMenu(eas);
        leftSideMenu = new LeftSideMenu();
        this.setTop(topMenu);
        composerManager = new ComposerManager(inbox, eas);
        topMenu.getComposeButton()
                .setOnMousePressed(e -> composerManager.createNewMessage());
        this.setRight(composerManager);

//        sceneStack = new ArrayDeque<>();
        System.out.println("Loading messageManager...");
        messagesViewManager = new MessagesViewManager(messages, composerManager);

        // SEARCHING HAPPENS HERE
        topMenu.getSearchBox().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                javafx.scene.control.TextField searchField = (javafx.scene.control.TextField)
                        event.getSource();
                MessageQuery messageQuery = new MessageQuery(this.inbox, searchField.getText(), false);
                if (messageQuery.emptyMessages()) {
                    // display search returns 0 messages
                } else {
                    this.messages = messageQuery.retrieveMessages();
                    messagesViewManager.setMessagesInfo(this.messages);
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
//        sceneStack.push(scene);
    }

    public void popStack() {
//        sceneStack.pop();
    }

    void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        messagesViewManager.setSceneManager(sceneManager);
        topMenu.setSceneManager(sceneManager);
        composerManager.setSceneManager(sceneManager);
        leftSideMenu.setSceneManager(sceneManager);
    }


}
