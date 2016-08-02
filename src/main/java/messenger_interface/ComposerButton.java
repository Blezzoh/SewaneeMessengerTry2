package messenger_interface;

import javafx.scene.control.Button;

/**
 * Created by evansdb0 on 8/2/16.
 *
 * @author Daniel Evans
 */
public class ComposerButton extends Button {

    private int composerId;

    public ComposerButton() {
        super();
    }

    public void setComposerId(int composerId) {
        this.composerId = composerId;
    }

    public int getComposerId() {
        return composerId;
    }
}
