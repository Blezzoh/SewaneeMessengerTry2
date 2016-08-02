package messenger_interface;

import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.LinkedList;

/**
 * Created by daniel on 7/31/16.
 *
 * @author Daniel Evans
 */
public class ComposerManager {

    private HBox composerGraphicalContainer;
    private LinkedList<Composer> composerList;
    private static final int COMPOSER_MAX = 3;

    public ComposerManager() {

        composerList = new LinkedList<>();
        composerGraphicalContainer = new HBox();
    }

    /*
    1 active composer and up to 3 "waiting in the wings"
     */
    public boolean addComposer(Composer composer) {
        if (composerList.size() != COMPOSER_MAX
                && !composer.getEmailAddress().getText().equals("")) {
            composerList.add(composer);
            Text text = new Text(composer.getEmailAddress().getText());
//            text.setOnMouseClicked(e -> /* show composer */);
            composerGraphicalContainer.getChildren().add(text);
            return true;
        }
        return false;
    }

    public boolean deleteComposer(Composer composer) {
        composerGraphicalContainer.getChildren().remove(/*not sure yet*/0);
        return composerList.remove(composer);
    }
}
