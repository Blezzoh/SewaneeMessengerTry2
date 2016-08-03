package messenger_interface;

import com.danielevans.email.ComposerData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by daniel on 7/31/16.
 *
 * @author Daniel Evans
 */
public class ComposerManager extends BorderPane {

    private LinkedList<ComposerData> composerDataList;
    private HBox buttonContainer;
    private static final int COMPOSER_MAX = 3;
    private Composer composer;
    private int numActiveComposers;

    public ComposerManager(Composer composer) {
        super();
        this.composer = composer;
        this.composer.setVisible(false);
        this.setCenter(this.composer);
        numActiveComposers = 0;
        composerDataList = new LinkedList<>();
        buttonContainer = new HBox(8);
        this.setBottom(buttonContainer);
    }

    /*
     * When calling generateId(), make sure to generate the id before
     * and set it to a ComposerData object before adding the ComposerData
     * to the ComposerDataList
     */
    private int generateId() {
        return composerDataList.size() + 1;
    }

    /*
    1 active composer and up to 3 "waiting in the wings"
     */
    public boolean newComposer() {
        if (composerDataList.size() != COMPOSER_MAX
                    && !composer.getEmailAddress().getText().equals("")) {

            composer.setVisible(true);
            composer.getEmailAddress().requestFocus();
            ++numActiveComposers;
            if (numActiveComposers > 1) {
                // ComposerData
                ComposerData cd = new ComposerData(composer);
                cd.setComposerDataId(generateId()); // do this before adding to composerDataList
                composerDataList.add(cd);

                // ComposerButton
                ComposerButton cmButton = new ComposerButton();
                cmButton = composerButtonSettings(cmButton);
                cmButton.setText("To: " + composer.getEmailAddress().getText());
                cmButton.setComposerDataId(cd.getComposerDataId());
                buttonContainer.getChildren().add(cmButton);

                // ComposerButton event
                cmButton.setOnMousePressed
                        (e -> {

                            ComposerData currentComposerMessageData = null;
                            if (checkTextFieldsEmpty()) {
                                currentComposerMessageData = new ComposerData(composer);
                                currentComposerMessageData.setComposerDataId(generateId());
                            }
                            ComposerButton button = (ComposerButton) e.getSource();
                            ComposerData composerData = getComposerData(button.getComposerDataId());
                            composer.getBodyText().setText(composerData.getBody());
                            composer.getEmailAddress().setText(composerData.getEmailAddress());
                            composer.getCc().setText(composerData.getCc());
                            composer.getSubject().setText(composerData.getSubject());
                            if (currentComposerMessageData != null) {
                                button.setText("To: " + currentComposerMessageData.getEmailAddress());
                                button.setComposerDataId(generateId());
                                composerDataList.add(currentComposerMessageData);
                            } else {
                                composerDataList.remove(composerData);
                                buttonContainer.getChildren().remove(button);
                                --numActiveComposers;
                            }
                        });
                return true;
            }
            }
        return false;
    }

    public boolean checkTextFieldsEmpty() {
        return !composer.getEmailAddress().getText().equals("") ||
                !composer.getSubject().getText().equals("") ||
                !composer.getCc().getText().equals("") ||
                !composer.getBodyText().getText().equals("");
    }

    public Composer getComposer() {
        return composer;
    }

    private void removeOldComposerData() {
    }

    public void hideComposer() {
        composer.clearAllTextFields();
        composer.setVisible(false);
    }

    private ComposerData getComposerData(int composerId) {
        ListIterator<ComposerData> li = composerDataList.listIterator();
        while (li.hasNext()) {
            ComposerData next = li.next();
            if (next.getComposerDataId() == composerId)
                return next;
        }
        return null;
    }

    private ComposerButton composerButtonSettings(ComposerButton button) {
        button.setFont(Font.font("Trebuchet MS", 15));
        button.setStyle("-fx-background-color: black; -fx-text-fill: white");
        return button;
    }

    public int size() {
        return composerDataList.size();
    }

}
