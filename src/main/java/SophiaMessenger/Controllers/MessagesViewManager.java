package SophiaMessenger.Controllers;

import SophiaMessenger.Models.DBMessage;
import SophiaMessenger.Views.MessageView;
import com.google.api.services.gmail.model.Message;
import de.email.aux.MessageParser;
import de.email.core.Authenticator;
import de.email.core.Inbox;
import de.email.database.Conn;
import de.email.database.MessageTableManager;
import de.email.interfaces.Auth;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Stack;

/**
 * Created by evansdb0 on 8/4/16.
 * @author Daniel Evans
 */
public class MessagesViewManager extends Pagination {

    private static final int itemsPerPage = 16;
    private final String imgUrl = "http://4.bp.blogspot.com/-SjsG6gvCasI/Ve6PJxhPEiI/AAAAAAAAFYU/dYvGfnIxPzk/s1600/Kundwa%2BDoriane%2Brwanda.jpg";
    private TilePane center;  // child of centerContainer
    private ScrollPane centerContainer;  // child of MessageManager
    private MessageView[] mvs;
    private boolean first = true;
    private Authenticator auth;
    private Stack<Scene> sceneStack;
    private javafx.scene.control.Button b;
    private Stage stage;


    public MessagesViewManager(Auth auth, List<Message> messages, Stack<Scene> sceneStack) {
        super();
        this.setPageCount(getPageCount(messages));
        this.auth = auth.getAuth();
        this.sceneStack = sceneStack;
        initCenter();
        initCenterContainer();

        b = new javafx.scene.control.Button("Back");
        // hitting back button takes you back to the mail gridview page
        b.setOnMousePressed(e -> goBack());

        mvs = new MessageView[itemsPerPage];
        System.out.print("Initializing messageViews...");
        for (int i = 0; i < mvs.length; i++) {
            try {
                // create message views with the database messages
                mvs[i] = new MessageView(
                        new DBMessage(messages.get(i)), imgUrl);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        center.getChildren().addAll(mvs);
        setPagination(messages);
        setMessageViewEvents();
    }

    private void goBack() {
        // pop() the seen created in showContent()
        sceneStack.pop();
        // sceneStack already has the original scene saved (controller pushed its scene)
        // so it is available here
        stage.setScene(sceneStack.peek());
    }

    private void showContent(String content) {
        if (content != null) {
            WebView wv = new WebView();

            WebEngine engine = wv.getEngine();
            if (!MessageParser.testForHTML(content))
                // load plain text version
                engine.loadContent(content, "text/plain");
            else
                // load html version
                engine.loadContent(content);
            stage = (Stage) this.getScene().getWindow();
            double stageX = stage.getWidth(), stageY = stage.getHeight();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            // make sure that the size of the of the scene is smaller than the screen size
            stageX = stageX >= screenSize.getWidth() ? stageX - 250 : stageX;
            stageY = stageY >= screenSize.getHeight() ? stageY - 250 : stageY;
            BorderPane p = new BorderPane();
            p.setTop(b);
            p.setCenter(wv);
            Scene body = new Scene(p, stageX, stageY);
            sceneStack.push(body);
            body.getStylesheets().add("MessengerStyle.css");
            stage.setScene(sceneStack.peek());
        }
    }

    // TODO: display pop up window of email when clicking on snippet or subject

    private void retrieveContent(String mId) {
        String content = null;
        try {
            content = Inbox.decodeString(getBodyText(mId));
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        showContent(content);
    }

    public void setMessageViewEvents() {
        for (int i = 0; i < mvs.length; i++) {
            final int temp = i;
            mvs[i].getSnippetField().setOnMousePressed(e -> {
                retrieveContent(mvs[temp].getMessageId());
            });
            mvs[i].getSubjectField().setOnMousePressed(e -> {
                retrieveContent(mvs[temp].getMessageId());
            });
        }
    }

    public void setMessagesInfo(List<Message> messages) {
        System.out.println("Loading new message info...");
        setPagination(messages);
    }

    // TODO: set up search logic in main controller, add update views button, and inbox labels on left side
    private void setPagination(List<Message> messages) {
        setPageIndex();
        this.setPageCount(getPageCount(messages));
        this.setPageFactory(
                (Integer pageIndex) ->
                {
                    System.out.println("Creating page...");
                    return createPage(pageIndex, messages);
                });
    }

    private void setPageIndex() {
        if (first) this.setCurrentPageIndex(0);
        else this.setCurrentPageIndex(1);
    }

    private ScrollPane createPage(Integer pageIndex, List<Message> messages) {

        int page = pageIndex * itemsPerPage;

        int mvsIndex = 0;
        for (int i = page; i < page + itemsPerPage && i < messages.size(); i++) {
            DBMessage dbm = null;
            try {
                dbm = new DBMessage(messages.get(i));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (dbm != null) {
                mvs[mvsIndex].getSenderField().setText(dbm.getFromName());
                mvs[mvsIndex].getDateField().setText(dbm.getDate());
                mvs[mvsIndex].getSnippetField().setText(dbm.getSnippet());
                mvs[mvsIndex].getSubjectField().setText(dbm.getSubject());
            }
            ++mvsIndex;
        }
        centerContainer.setContent(center);
        return centerContainer;
    }


    private int getPageCount(List<Message> messages) {
        int pageCount = MessageTableManager.numberOfMatchingMessages(messages) / itemsPerPage;
//        int pageCount = (int) Math.ceil(messages.size() / itemsPerPage);
        return pageCount == 0 ? 1 : pageCount;
    }

    private void initCenterContainer() {
        centerContainer = new ScrollPane(center);
        centerContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        centerContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        centerContainer.setFitToWidth(true);
    }

    private void initCenter() {
        // MessagePane events and style handling
        center = new TilePane(8,5);
        center.setPadding(new Insets(8, 0, 8, 0));
        center.setAlignment(Pos.CENTER);
        center.setStyle("-fx-background-color: rgba(7, 171,202,.4)");
    }

    public String getBodyText(String mId) throws SQLException {
        Connection con = Conn.makeConnection();
        PreparedStatement ps = con.prepareStatement("SELECT body from message where message_id = ?");
        ps.setString(1, mId);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            return rs.getString(1);
        else
            return null;
    }
}
