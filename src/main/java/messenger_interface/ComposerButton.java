package messenger_interface;

import javafx.scene.control.Button;

/**
 * Created by evansdb0 on 8/2/16.
 *
 * @author Daniel Evans
 */
public class ComposerButton extends Button {

    private int composerDataId;

    public ComposerButton() {
        super();
    }


    public int getComposerDataId() {
        return composerDataId;
    }

    public void setComposerDataId(int composerDataId) {
        this.composerDataId = composerDataId;
    }
}
