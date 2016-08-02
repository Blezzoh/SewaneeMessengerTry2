package messenger_interface;

import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.LinkedList;

/**
 * Created by daniel on 7/31/16.
 *
 * @author Daniel Evans
 */
public class ComposerManager extends HBox {

    private LinkedList<Composer> composerList;
    private static final int COMPOSER_MAX = 3;

    public ComposerManager() {

        composerList = new LinkedList<>();
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
                Text text = new Text(composer.getEmailAddress().getText());
//            text.setOnMouseClicked(e -> /* show composer */);
                this.getChildren().add(text);
                // TODO: MAKE THE HBOX VISIBLE THAT CONTAINS TEXT
                return true;
            }
        }
        return false;
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
