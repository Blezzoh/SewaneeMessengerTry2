package messenger_interface;

import com.danielevans.email.ComposerData;
import javafx.scene.control.Button;
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
    private ComposerButton currentButton;
    private ComposerData currentData;

    public ComposerManager(Composer composer) {
        super();
        this.composer = composer;
        this.composer.setVisible(false);
        composer.getEmailAddress().setOnKeyReleased(e -> {
            if (currentButton != null) {
                currentButton.setText("To: " + composer.getEmailAddress().getText());
            }
        });
        this.setCenter(this.composer);
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
        if (composerDataList.size() != COMPOSER_MAX) {

            saveCurrentComposerData();

            composer.setVisible(true);
            composer.getEmailAddress().requestFocus();
            composer.clearAllTextFields();

            // ComposerData
            ComposerData cd = new ComposerData(composer);
            currentData = cd;  // reset the current ComposerData
            cd.setComposerDataId(generateId()); // do this before adding to composerDataList
            composerDataList.add(cd);

            // ComposerButton
            ComposerButton cmButton = new ComposerButton();
            currentButton = cmButton; // reset the current ComposerButton
            cmButton = composerButtonSettings(cmButton);
            cmButton.setText("To: ");
            cmButton.setComposerDataId(cd.getComposerDataId());
            buttonContainer.getChildren().add(cmButton);
            colorButtons(cmButton);


            // ComposerButton event
            cmButton.setOnMousePressed
                    (e -> {
                        saveCurrentComposerData();
                        ComposerButton button = (ComposerButton) e.getSource();
                        currentButton = button;
                        colorButtons(button);

                        ComposerData composerData = getComposerData(button.getComposerDataId());
                        currentData = composerData;
                        composer.getBodyText().setText(composerData.getBody());
                        composer.getEmailAddress().setText(composerData.getEmailAddress());
                        composer.getCc().setText(composerData.getCc());
                        composer.getSubject().setText(composerData.getSubject());
                    });
            return true;
        }
        return false;
    }

    private void saveCurrentComposerData() {
        // take care of the data stored in the current state of the composer by
        // storing it in the currentData/currentButton field, which corresponds
        // to ComposerButton/ComposerData created on the previous call to newComposer()
        // the below condition will be true on all calls to composer such that there is
        // at least one ComposerButton on the screen
        if (currentData != null) {
            currentData.setBody(composer.getBodyText().getText());
            currentData.setCc(composer.getCc().getText());
            currentData.setEmailAddress(composer.getEmailAddress().getText());
            currentData.setSubject(composer.getSubject().getText());

            currentButton.setText("To: " + currentData.getEmailAddress());
        }
    }

    private void colorButtons(Button cmButton) {
        for (int i = 0; i < buttonContainer.getChildren().size(); i++) {
            Button button = (Button) buttonContainer.getChildren().get(i);
            if (button == cmButton)
                button.setStyle("-fx-background-color: blue; -fx-text-fill: white");
            else
                button.setStyle("-fx-background-color: black; -fx-text-fill: white");
        }
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
        button.setStyle("-fx-background-color: blue; -fx-text-fill: white");
        return button;
    }

    public int size() {
        return composerDataList.size();
    }

}
