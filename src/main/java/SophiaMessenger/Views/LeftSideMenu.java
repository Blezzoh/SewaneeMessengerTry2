package SophiaMessenger.Views;

import SophiaMessenger.Controllers.MessagesViewManager;
import SophiaMessenger.Controllers.SceneManager;
import de.email.core.Inbox;
import de.email.core.MessageQuery;
import de.email.core.SearchQueries;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Created by daniel on 9/28/16.
 *
 * @author Daniel Evans
 */
public class LeftSideMenu extends VBox {

    private Text inbox;
    private Text sent;
    private Text important;
    private Text drafts;
    private Text starred;
    private SceneManager sceneManager;

    public LeftSideMenu() {
        initFields();
        this.getChildren()
                .addAll(inbox, sent, important, drafts, starred);
        setBackground(Background.EMPTY);
    }

    private void setStyle() {
        this.setSpacing(10);
        this.setPadding(new Insets(10));
        starred.setFont(Font.font(18));
        inbox.setFont(Font.font(18));
        important.setFont(Font.font(18));
        drafts.setFont(Font.font(18));
        sent.setFont(Font.font(18));
    }

    private void initFields() {
        inbox = new Text("Inbox");
        sent = new Text("Sent");
        important = new Text("Important");
        drafts = new Text("Drafts");

        starred = new Text("Starred");
    }

    public void setEvents(MessagesViewManager mvm, Inbox in) {

        /*for (int i = 0; i < this.getChildren().size(); i++) {
            Text text = (Text) this.getChildren().get(i);
            Scene s = sceneManager.getCurrentScene();
            System.out.println(text);
            inbox.setOnMouseEntered(e-> s.setCursor(Cursor.HAND));
            inbox.setOnMouseExited(e-> s.setCursor(Cursor.DEFAULT));
        }*/
        inbox.setOnMousePressed(
                e -> mvm.setMessagesInfo(
                        new MessageQuery(in, SearchQueries.INBOX, true)
                                .retrieveMessages())
        );
        sent.setOnMousePressed(
                e -> mvm.setMessagesInfo(
                        new MessageQuery(in, SearchQueries.SENT, true)
                                .retrieveMessages())
        );
        important.setOnMousePressed(
                e -> mvm.setMessagesInfo(
                        new MessageQuery(in, SearchQueries.IMPORTANT, true)
                                .retrieveMessages())
        );
        starred.setOnMousePressed(
                e -> mvm.setMessagesInfo(
                        new MessageQuery(in, SearchQueries.STARRED, true)
                                .retrieveMessages())
        );
        drafts.setOnMousePressed(
                e ->
                        mvm.setMessagesInfo(
                                new MessageQuery(in, SearchQueries.DRAFTS, true)
                                        .retrieveMessages())
        );
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
}
