package messenger_interface;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by daniel on 7/31/16.
 *
 * @author Daniel Evans
 */
public class ComposerManager extends BorderPane {

    private LinkedList<Composer> composerList;
    private HBox buttonContainer;
    private static final int COMPOSER_MAX = 3;

    public ComposerManager() {
        super();
        composerList = new LinkedList<>();
        buttonContainer = new HBox(8);
        this.setBottom(buttonContainer);
    }

    private int generateId() {
        return composerList.size() + 1;
    }

    /*
    1 active composer and up to 3 "waiting in the wings"
     */
    public boolean addComposer(Composer composer) {
        if (composerList.contains(composer)) {
            updateComposerList(composer);
        } else {
            composer.setComposerId(generateId());
            if (composerList.size() != COMPOSER_MAX
                    && !composer.getEmailAddress().getText().equals("")) {
                composerList.add(composer);
                composer.setVisible(false);
                ComposerButton cmButton = composer.getButtonForComposerManager();
                cmButton.setText("To: " + composer.getEmailAddress().getText());
                composer.setButtonForComposerManager(composerButtonSettings(cmButton));
                cmButton.setComposerId(composer.getComposerId());
                composer.getButtonForComposerManager().setOnMousePressed
                        (e -> {
                            Composer current = getCurrentComposer();
                            ComposerButton button = (ComposerButton) e.getSource();
                            Composer toDisplay = findComposer(button.getComposerId());
                            this.setCenter(toDisplay);
                            composerList.remove(toDisplay);
                            if (current != null) {
                                button.setText(current.getEmailAddress().getText());
                                button.setComposerId(generateId());
                                composerList.add(current);
                            } else {
                                this.getChildren().remove(button);
                            }
                            // store current composer in the linkedlist
                        });
                buttonContainer.getChildren().add(composer.getButtonForComposerManager());
                return true;
            }
        }
        return false;
    }

    private Composer getCurrentComposer() {
        return this.getCenter() == null ? null : (Composer) this.getRight();
    }

    private Composer findComposer(int composerId) {
        ListIterator<Composer> li = composerList.listIterator();
        while (li.hasNext()) {
            Composer next = li.next();
            if (next.getComposerId() == composerId)
                return next;
        }
        return null;
    }

    private ComposerButton composerButtonSettings(ComposerButton button) {
        button.setFont(Font.font("Trebuchet MS", 15));
        button.setStyle("-fx-background-color: black; -fx-text-fill: white");
        return button;
    }

    private void updateComposerList(Composer composer) {
//        this.getChildren().remove()
    }

    public boolean deleteComposer(Composer composer, Text text) {
        return composerList.remove(composer) && this.getChildren().remove(text);
    }

    public int size() {
        return composerList.size();
    }

}
