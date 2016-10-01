package SophiaMessenger.Views;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

/**
 * Created by daniel on 9/30/16.
 *
 * @author Daniel Evans
 */
public class AutoSuggestTextBox extends TextField {
    /**
     * The existing autocomplete eas.
     */
    private final SortedSet<String> eas;
    /**
     * The popup used to select an entry.
     */
    private ContextMenu easPopup;

    /**
     * Construct a new AutoCompleteTextField.
     */
    public AutoSuggestTextBox(SortedSet<String> eas) {
        super();
        this.eas = eas;
        easPopup = new ContextMenu();
        textProperty().addListener((observableValue, s, s2) -> {
            String text = getText().toLowerCase().trim();
            if (text.length() == 0) {
                easPopup.hide();
            } else {
                LinkedList<String> searchResult = new LinkedList<>();
                searchResult.addAll(eas.subSet(text, text + Character.MAX_VALUE));
                if (eas.size() > 0) {
                    populatePopup(searchResult);
                    if (!easPopup.isShowing()) {
                        easPopup.show(AutoSuggestTextBox.this, Side.BOTTOM, 0, 0);
                    }
                } else {
                    easPopup.hide();
                }
            }
        });

        focusedProperty().addListener((observableValue, aBoolean, aBoolean2) ->
                easPopup.hide());
    }

    /**
     * Get the existing set of autocomplete eas.
     *
     * @return The existing autocomplete eas.
     */
    public SortedSet<String> getEntries() {
        return eas;
    }

    /**
     * Populate the entry set with the given search results.  Display is limited to 10 eas, for performance.
     *
     * @param searchResult The set of matching strings.
     */
    private void populatePopup(List<String> searchResult) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        // If you'd like more eas, modify this line.
        int maxEntries = 10;
        int count = Math.min(searchResult.size(), maxEntries);
        for (int i = 0; i < count; i++) {
            final String result = searchResult.get(i);
            Label entryLabel = new Label(result);
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            item.setOnAction(actionEvent -> {
                setText(result);
                easPopup.hide();
            });
            menuItems.add(item);
        }
        easPopup.getItems().clear();
        easPopup.getItems().addAll(menuItems);

    }
}
