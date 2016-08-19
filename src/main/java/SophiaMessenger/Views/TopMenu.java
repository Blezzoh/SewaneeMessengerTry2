package SophiaMessenger.Views;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Created by evansdb0 on 8/4/16.
 *
 * @author Blaise Iradukunda and Daniel Evans
 */
public class TopMenu extends HBox {

    private static final String TOP_STYLE = "-fx-background-color: rgba(7, 171,202,.7);" +
            " -fx-padding: 15px; -fx-spacing: 15px; -fx-start-margin: 40px;" +
            " -fx-border-color:rgba(255, 153, 51, .8); -fx-border-radius: 3px";

    private static final String COMPOSE_STYLE = "-fx-background-color: rgba(7, 171,202,.7);" +
            " -fx-font-family: Trebuchet MS; -fx-font-size: 13px; -fx-font-weight: bold;" +
            " -fx-border-color: white; -fx-effect: dropshadow(gaussian, black, 2, 0, 3, 3);" +
            " -fx-border-insets: 3px; -fx-border-width: 2px; -fx-text-fill: white";

    private TextField searchField;
    private ImageView searchImage;
    private Button searchButton;

    public TopMenu() {
        initFields();
        styleFields();

        // add fields to layout and style layout
        this.setPadding(new Insets(2, 2, 2, 2));
        this.getChildren().addAll(searchButton, searchField);
        this.setStyle(TOP_STYLE);
    }

    private void initFields() {
        searchField = new TextField();
        searchImage = new ImageView();
        searchButton = new Button();
    }

    private void styleFields() {
        // searchButton
        searchButton.setStyle(COMPOSE_STYLE);
        searchButton.setText("SEARCH");
        searchButton.setGraphic(searchImage);

        // searchField
        searchField.setStyle("-fx-font-size: 14;");
        searchField.setTranslateY(-3);
        searchField.setVisible(false);
        HBox.setHgrow(searchField, Priority.max(Priority.SOMETIMES, Priority.ALWAYS));
        HBox.setMargin(searchField, new Insets(10, 25, 10, 25));

    }

    private void setFieldEvents() {
/*        searchButton.setOnMouseClicked(e ->
        {
            if(searchField.isVisible()) {
                userSearchForMessages(root, sp);
                searchField.setVisible(false);
            } else
                displaySearchField(searchField);
        });*/
    }
}
