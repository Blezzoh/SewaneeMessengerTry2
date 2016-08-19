package SophiaMessenger.Controllers;

import SophiaMessenger.Views.MessageView;
import com.google.api.services.gmail.model.Message;
import de.email.Authenticator;
import de.email.FullMessage;
import de.email.database.EmailDate;
import de.email.interfaces.Auth;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;

import java.io.IOException;
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
        for (int i = 0; i < mvs.length; i++) {
            try {
                mvs[i] = new MessageView(
                        new FullMessage(auth, messages.get(i)), imgUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            FullMessage fm = null;
            try {
                fm = new FullMessage(auth, messages.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fm != null) {
                mvs[mvsIndex].getSenderField().setText(fm.getFromName());
                EmailDate ed = new EmailDate(fm.getDate());
                mvs[mvsIndex].getDateField().setText(ed.format("yyyy/mm/dd"));
                mvs[mvsIndex].getSnippetField().setText(fm.getSnippet());
                mvs[mvsIndex].getSubjectField().setText(fm.getSubject());
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
