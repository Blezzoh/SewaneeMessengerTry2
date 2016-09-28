package SophiaMessenger.Views;

import SophiaMessenger.Controllers.MessagesViewManager;
import de.email.core.Inbox;
import de.email.core.MessageQuery;
import de.email.core.SearchQueries;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Created by daniel on 9/28/16.
 *
 * @author Daniel Evans
 */
public class LeftSideMenu extends VBox {

    private static final String STYLE_ON_ENTER = "-fx-background-color: aquamarine;" + "-fx-background-radius: 0px;" + "-fx-padding: 10px; " + "-fx-border-radius: 0px;" + "-fx-border-style: solid;" + "-fx-border-color: #67007c;";
    private Text inbox;
    private Text sent;
    private Text important;
    private Text drafts;
    private Text starred;

    public LeftSideMenu() {
        initFields();
        this.getChildren()
                .addAll(inbox, sent, important, drafts, starred);
        setStyle();
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
        inbox.setOnMousePressed(
                e -> mvm.setMessagesInfo(
                        new MessageQuery(in, SearchQueries.INBOX)
                                .retrieveMessages())
        );
        sent.setOnMousePressed(
                e -> mvm.setMessagesInfo(
                        new MessageQuery(in, SearchQueries.SENT)
                                .retrieveMessages())
        );
        important.setOnMousePressed(
                e -> mvm.setMessagesInfo(
                        new MessageQuery(in, SearchQueries.IMPORTANT)
                                .retrieveMessages())
        );
        starred.setOnMousePressed(
                e -> mvm.setMessagesInfo(
                        new MessageQuery(in, SearchQueries.STARRED)
                                .retrieveMessages())
        );
        drafts.setOnMousePressed(
                e ->
                        mvm.setMessagesInfo(
                                new MessageQuery(in, SearchQueries.DRAFTS)
                                        .retrieveMessages())
        );
    }
}
