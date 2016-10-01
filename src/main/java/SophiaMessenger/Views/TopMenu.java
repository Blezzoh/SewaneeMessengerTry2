package SophiaMessenger.Views;

import SophiaMessenger.Controllers.SceneManager;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.SortedSet;

/**
 * Created by evansdb0 on 8/4/16.
 *
 * @author Blaise Iradukunda and Daniel Evans
 */
public class TopMenu extends HBox {

    private ImageView searchImage;
    private Button settingsButton;
    private Button composeButton;
    private AutoSuggestTextBox searchTextBox;
    private SceneManager sceneManager;

    public TopMenu(SortedSet<String> eas) {
        super();
        this.setStyle("-fx-background-color: transparent");
        initFields(eas);
        styleFields();
        setEvents();

        // add fields to layout and style layout
        this.setPadding(new Insets(2, 2, 2, 2));
        this.getChildren().addAll(composeButton, settingsButton);
        this.getStyleClass().add("TopMenu");
        composeButton.getStyleClass().add("Buttons");
        settingsButton.getStyleClass().add("Buttons");
        searchTextBox.getStyleClass().add("SearchField");
        this.getChildren().add(searchTextBox);
    }

    private void initFields(SortedSet<String> eas) {
        searchTextBox = new AutoSuggestTextBox(eas);
        searchImage = new ImageView();
        settingsButton = new Button();
        composeButton = new Button("Compose");
    }

    private void styleFields() {
        // settingsButton
        settingsButton.setText("Settings");

        // compose button

        // searchField
        searchTextBox.setStyle("-fx-font-size: 14;");
        searchTextBox.setTranslateY(-3);
        searchTextBox.setVisible(true);
        searchTextBox.setPromptText("Search for anything...");
        HBox.setHgrow(searchTextBox, Priority.max(Priority.SOMETIMES, Priority.ALWAYS));
        HBox.setMargin(searchTextBox, new Insets(10, 25, 10, 25));
    }

    private void setEvents() {
        settingsButton.setOnMousePressed(e -> {
            Settings settingsView = new Settings();
            sceneManager.createNewWindow(settingsView);
            sceneManager.displayCurrentScene();
        });
    }

    public AutoSuggestTextBox getSearchBox() {
        return searchTextBox;
    }

    public Button getComposeButton() {
        return composeButton;
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

}
