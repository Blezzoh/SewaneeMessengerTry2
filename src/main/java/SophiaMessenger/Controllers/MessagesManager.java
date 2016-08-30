package SophiaMessenger.Controllers;

import SophiaMessenger.Models.DBMessage;
import SophiaMessenger.Views.MessageView;
import com.google.api.services.gmail.model.Message;
import de.email.core.Authenticator;
import de.email.interfaces.Auth;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by evansdb0 on 8/4/16.
 * @author Daniel Evans
 */
public class MessagesManager extends Pagination {

    private static final int itemsPerPage = 16;
    private final String imgUrl = "http://4.bp.blogspot.com/-SjsG6gvCasI/Ve6PJxhPEiI/AAAAAAAAFYU/dYvGfnIxPzk/s1600/Kundwa%2BDoriane%2Brwanda.jpg";
    private TilePane center;  // child of centerContainer
    private ScrollPane centerContainer;  // child of MessageManager
    private MessageView[] mvs;
    private Authenticator auth;


    public MessagesManager(Auth auth, List<Message> messages) {
        super();
        this.setPageCount(getPageCount(messages));
        this.auth = auth.getAuth();
        initCenter();
        initCenterContainer();

        mvs = new MessageView[itemsPerPage];
        System.out.print("Initializing messageViews...");
        for (int i = 0; i < mvs.length; i++) {
            try {
                mvs[i] = new MessageView(
                        new DBMessage(messages.get(i)), imgUrl);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("  Done.");
        center.getChildren().addAll(mvs);
        setPagination(messages);
        setPaginationEvents();

    }

    private void setPagination(List<Message> messages) {
        this.setPageFactory(
                (Integer pageIndex) ->
                {
                    System.out.println("Creating page...");
                    return createPage(pageIndex, messages);
                });
    }

    private void setPaginationEvents() {
/*        this.setOnMousePressed(e ->
        {
            if (e.getClickCount() == 1)
            {
                searchField.setVisible(false);
            }
        });
*/
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
        int pageCount = (int) Math.ceil(messages.size() / itemsPerPage);
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
}
