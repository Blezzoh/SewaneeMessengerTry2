package SophiaMessenger.Controllers;

import SophiaMessenger.Models.DBMessage;
import SophiaMessenger.Views.MessageView;
import com.google.api.services.gmail.model.Message;
import de.email.aux.ImageBot;
import de.email.aux.MessageParser;
import de.email.core.Authenticator;
import de.email.core.Inbox;
import de.email.database.Config;
import de.email.database.Conn;
import de.email.database.DB;
import de.email.database.MessageTableManager;
import de.email.interfaces.Auth;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by evansdb0 on 8/4/16.
 * @author Daniel Evans
 */
public class
MessagesViewManager extends Pagination implements Auth {

    // temporary change to 6, usually 12
    private static final int itemsPerPage = 6;
    private static final int REPLY_ICON = 0,
            FORWARD_ICON = 1,
            LABEL_ICON = 2,
            DOWNLOAD_ICON = 3,
            REMINDER_ICON = 4,
            TRASH_ICON = 5;
    private TilePane center;  // child of centerContainer
    private ScrollPane centerContainer;  // child of MessageManager
    private MessageView[] mvs;
    private boolean first = true;
    private Button b;
    private Connection con;
    private SceneManager sceneManager;
    // backing DBmessage array
    private DBMessage[] dbm;
    private Authenticator auth;


    public MessagesViewManager(List<Message> messages, ComposerManager composerManager) {
        super();
        this.setStyle("-fx-background-color: transparent");
        this.setPageCount(getPageCount(messages));
        this.auth = composerManager.getAuth();
        initCenter();
        initCenterContainer();
        try {
            con = Conn.makeConnection();
        } catch (SQLException e) {
            System.out.println("IMPORTANT: UNABLE TO CONNECT TO DATABASE IN MESSAGE VIEW MANAGER");
            e.printStackTrace();
        }
        b = new Button("Back");
        // hitting back button takes you back to the mail gridview page
        b.setOnMousePressed(e -> this.sceneManager.destroyCurrentWindow());

        // writing to message views from database info
        mvs = new MessageView[itemsPerPage];
        // backing dbmessage info
        dbm = new DBMessage[itemsPerPage];
        System.out.println("Initializing messageViews...");
        for (int i = 0; i < mvs.length; i++) {
            mvs[i] = new MessageView(messages.get(i).getId());
        }
        center.getChildren().addAll(mvs);
        center.setStyle("-fx-background-color: transparent");
        setPagination(messages);
        setMessageViewEvents(composerManager);
    }

    private void showContent(String content) {
        if (content != null) {
            WebView emailContent = new WebView();
            emailContent.contextMenuEnabledProperty().setValue(true);
            WebEngine engine = emailContent.getEngine();
            if (!MessageParser.testForHTML(content))
                // load plain text version
                engine.loadContent(content, "text/plain");
            else
                // load html version
                engine.loadContent(content);
            BorderPane emailContentRoot = new BorderPane();
            emailContentRoot.setTop(b);
            emailContentRoot.setCenter(emailContent);
            Scene emailContentScene = sceneManager.createNewWindow(emailContentRoot);
            emailContentScene.getStylesheets().add("MessengerStyle.css");
            sceneManager.displayCurrentScene();
        }
    }

    // TODO: display pop up window of email when clicking on snippet or subject

    private void retrieveContent(String mId) {
        String content = null;
        try {
            // SELECT body from message where message_id = ?
            content = Inbox.decodeString(DB.lookup("body", Config.MESSAGES, "message_id", "=", mId));
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        showContent(content);
    }

    public void setMessageViewEvents(ComposerManager composerManager) {
        // second screen - email content
        for (int i = 0; i < mvs.length; i++) {
            final int k = i;
            mvs[i].getReplyEmail().setOnMousePressed(e -> {

            });
            mvs[i].getRightContainer().setOnMousePressed(e -> {
                retrieveContent(mvs[k].getMessageId());
            });
            ObservableList<Node> messageViewIcons = mvs[i].getFirstRowOptions().getChildren();
            for (int j = 0; j < messageViewIcons.size(); j++) {
                // reply icon
                messageViewIcons.get(REPLY_ICON).setOnMouseEntered(e -> {
                    mvs[k].setNoifText("Reply to " + dbm[k].getFromName());
                });
                messageViewIcons.get(REPLY_ICON).setOnMousePressed(e ->
                        composerManager.createComposerWithReplyTo
                                (dbm[k])
                );
                // -----------------------------------

                // forward icon
                messageViewIcons.get(FORWARD_ICON).setOnMouseEntered(e -> {
                    mvs[k].setNoifText("Forward to " + dbm[k].getFromName());
                });
                messageViewIcons.get(FORWARD_ICON).setOnMousePressed(e -> {
                    composerManager.createComposerWithFwdMessage(dbm[k]);
                });
                // ----------------------------------------

                // label icon
                messageViewIcons.get(LABEL_ICON).setOnMousePressed(e -> {
                });
                messageViewIcons.get(LABEL_ICON).setOnMouseEntered(e -> {
                    mvs[k].setNoifText("Add label to email");
                });
                // ---------------------------------

                // Download icon
                messageViewIcons.get(DOWNLOAD_ICON).setOnMousePressed(e -> {
                    // download (attachments/open body in google docs?
                    // place email body to file? logic
                });
                messageViewIcons.get(DOWNLOAD_ICON).setOnMouseEntered(e -> {
                    mvs[k].setNoifText("Download email to a file");
                });
                // ----------------------

                // Reminder icon
                messageViewIcons.get(REMINDER_ICON).setOnMousePressed(e -> {

                });
                messageViewIcons.get(REMINDER_ICON).setOnMouseEntered(e -> {
                    mvs[k].setNoifText("Set reminder in calendar");
                });
                // ------------------------

                // Trash icon
                messageViewIcons.get(TRASH_ICON).setOnMouseEntered(e -> {
                    mvs[k].setNoifText("Trash email");
                });
                messageViewIcons.get(TRASH_ICON).setOnMousePressed(e -> {
                    System.out.println("Deleting message");
                    // trash email
                    /*
                    try {
//                        dbm[k].deleteMessage();
                        // delete from gmail server
//                    FullMessage.trashMessage(this,mvs[k].getMessageId());
                    // visually delete it
                    } catch (SQLException e1) {
                        System.out.println("Problem deleting message from db");
                        e1.printStackTrace();
                    }*/
                });
            }
        }
    }

    /**
     * UPDATES TO MESSAGE DISPLAY HAPPEN WITH THIS METHOD
     * Updates the message views with the messages parameter
     *
     * @param messages the updated messages list
     */
    public void setMessagesInfo(List<Message> messages) {
        long t = System.currentTimeMillis();
        System.out.println("Loading new message info...");
        setPagination(messages);
        System.out.println("Time to update message views = " + (System.currentTimeMillis() - t));
    }

    private void setPagination(List<Message> messages) {
        setIndexToFirstPage();
        this.setPageCount(getPageCount(messages));
        this.setPageFactory(
                (Integer pageIndex) ->
                {
                    System.out.println("Writing to message views...");
                    return createPage(pageIndex, messages);
                });
    }

    private void setIndexToFirstPage() {
        if (first) this.setCurrentPageIndex(0);
        else this.setCurrentPageIndex(1);
    }

    private ScrollPane createPage(Integer pageIndex, List<Message> messages) {

        int page = pageIndex * itemsPerPage;

        int mvsIndex = 0;
        for (int msgIndex = page;
             msgIndex < page + itemsPerPage && msgIndex < messages.size();
             msgIndex++) {
            DBMessage dbm = null;
            try {
                dbm = new DBMessage(messages.get(msgIndex));
                // backing db message
                this.dbm[mvsIndex] = dbm;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            reloadMessageViewInfo(messages, dbm, mvsIndex, msgIndex);
            ++mvsIndex;
        }
        centerContainer.setContent(center);
        return centerContainer;
    }

    /*
    writes information described by mvs[x] methods to the messageView
     */
    private void reloadMessageViewInfo(List<Message> messages,
                                       DBMessage dbm,
                                       int mvsIndex,
                                       int msgIndex) {
        if (dbm != null) {
            mvs[mvsIndex].setMessageId(messages.get(msgIndex).getId());
            mvs[mvsIndex].setMessageFields(dbm);
            ImageBot ib = null;
            try {
                ib = new ImageBot(con);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (ib != null)
                mvs[mvsIndex].setPic(MessageView.makeImage
                        (ib.parseSenderImage(dbm.getFromEmail())));
        }
    }


    private int getPageCount(List<Message> messages) {
        int pageCount = 0;
        try {
            pageCount = MessageTableManager.numberOfMatchingMessages(messages) / itemsPerPage;
        } catch (SQLException e) {
            e.printStackTrace();
        }
//        int pageCount = (int) Math.ceil(messages.size() / itemsPerPage);
        return pageCount == 0 ? 1 : pageCount;
    }

    private void initCenterContainer() {
        centerContainer = new ScrollPane(center);
        centerContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        centerContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        centerContainer.setFitToWidth(true);
        centerContainer.setStyle("-fx-background-color: transparent");
    }

    private void initCenter() {
        // MessagePane events and style handling
        center = new TilePane(8,5);
        center.setPadding(new Insets(15, 0, 15, 0));
        center.setAlignment(Pos.CENTER);
        // rgba(104,179,191,.2)
        center.setStyle("-fx-background-color: transparent");
    }

    public String getBodyText(String mId) throws SQLException {

        String retVal = "";
        PreparedStatement ps = con.prepareStatement("SELECT body from message where message_id = ?");
        ps.setString(1, mId);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            retVal = rs.getString(1);

        return retVal;
    }

    void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public Authenticator getAuth() {
        return auth;
    }
}
