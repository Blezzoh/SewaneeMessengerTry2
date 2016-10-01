package SophiaMessenger.Views;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

/**
 * Created by daniel on 9/30/16.
 *
 * @author Daniel Evans
 */
public class Settings extends BorderPane {

    TreeView<String> settingsMenu;

    /*
        VBox vbox = new VBox();
        vbox.setLayoutX(20);
        vbox.setLayoutY(20);

        TreeItem<String> root = new TreeItem<String>("Root Node");
        root.setExpanded(false);
        root.getChildren().addAll(
                new TreeItem<String>("Item 1"),
                new TreeItem<String>("Item 2"),
                new TreeItem<String>("Item 3")
        );
        TreeView<String> treeView = new TreeView<String>(root);


        vbox.getChildren().add(treeView);
        vbox.setSpacing(10);
        ((Group) scene.getRoot()).getChildren().add(vbox);
        */

    public Settings() {
        TreeItem<String> root = new TreeItem<>("Root Node");
        root.setExpanded(true);
        root.getChildren().addAll(
                new TreeItem<>("Themes"),
                new TreeItem<>("Stuff 1"),
                new TreeItem<>("Stuff 2")
        );
        settingsMenu = new TreeView<>(root);
        this.setLeft(settingsMenu);
    }
}
