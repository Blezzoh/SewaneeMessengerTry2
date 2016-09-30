package SophiaMessenger.Views;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import np.com.ngopal.control.AutoFillTextBox;

/**
 * Created by evansdb0 on 8/4/16.
 *
 * @author Blaise Iradukunda and Daniel Evans
 */
public class TopMenu extends HBox {

    private TextField searchField;
    private ImageView searchImage;
    private Button searchButton;
    private Button composeButton;
    private AutoFillTextBox<String> searchBox;

    public TopMenu() {
        super();
        this.setStyle("-fx-background-color: transparent");
        initFields();
        styleFields();

        // add fields to layout and style layout
        this.setPadding(new Insets(2, 2, 2, 2));
        this.getChildren().addAll(composeButton, searchButton, searchField);
        this.getStyleClass().add("TopMenu");
        composeButton.getStyleClass().add("Buttons");
        searchButton.getStyleClass().add("Buttons");
        searchField.getStyleClass().add("SearchField");
    }

    private void initFields() {
        searchField = new TextField();
        searchImage = new ImageView();
        searchButton = new Button();
        composeButton = new Button("Compose");
    }

    private void styleFields() {
        // searchButton
        searchButton.setText("SEARCH");
        searchButton.setGraphic(searchImage);

        // compose button

        // searchField
        searchField.setStyle("-fx-font-size: 14;");
        searchField.setTranslateY(-3);
        searchField.setVisible(true);
        searchField.setPromptText("Search for anything...");
        HBox.setHgrow(searchField, Priority.max(Priority.SOMETIMES, Priority.ALWAYS));
        HBox.setMargin(searchField, new Insets(10, 25, 10, 25));

    }

    public TextField getSearchField() {
        return searchField;
    }

    public Button getComposeButton() {
        return composeButton;
    }
}
