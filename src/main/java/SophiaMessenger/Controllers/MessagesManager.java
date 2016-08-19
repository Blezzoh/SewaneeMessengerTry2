package SophiaMessenger.Controllers;

import com.google.api.services.gmail.model.Message;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;

import java.util.List;

/**
 * Created by evansdb0 on 8/4/16.
 *
 * @author Daniel Evans
 */
public class MessagesManager extends Pagination {


    private static final int itemsPerPage = 16;
    private TilePane center;  // child of centerContainer
    private ScrollPane centerContainer;  // child of MessageManager



    public MessagesManager(List<Message> messages) {

        initCenter();
        initCenterContainer();

        setPagination();
        setPaginationEvents();

    }

    private void setPagination() {
        this.setPageFactory
                (
                        (Integer pageIndex) ->
                        {
                                System.out.println("Creating page...");
                                return createPage(pageIndex);
                        }
                );
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

    private ScrollPane createPage(Integer pageIndex) {



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
