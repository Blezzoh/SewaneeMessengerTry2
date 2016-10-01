package SophiaMessenger.Controllers;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created by daniel on 9/30/16.
 *
 * @author Daniel Evans
 */
public class SceneManager {

    private Deque<Scene> sceneStack;
    private Stage stage;

    /**
     * @param stage     the stage of the application
     * @param baseScene the base scene of the application
     */
    public SceneManager(Stage stage, Scene baseScene) {
        sceneStack = new ArrayDeque<>();
        this.stage = stage;
        sceneStack.push(baseScene);
    }

    public Scene getCurrentScene() {
        return sceneStack.peek();
    }

    public Stage getStage() {
        return stage;
    }

    /**
     * @param root root node to add to new scene
     * @return scene containing root as root node with dimensions
     * (cStage.getWidth(), cStage.getHeight())
     */
    public Scene createNewWindow(Node root) {
        double stageX = stage.getWidth(), stageY = stage.getHeight();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // make sure that the size of the of the scene is smaller than the screen size
        stageX = stageX >= screenSize.getWidth() ? stageX - 250 : stageX;
        stageY = stageY >= screenSize.getHeight() ? stageY - 250 : stageY;
        Scene newWindow = new Scene((Parent) root, stageX, stageY);
        sceneStack.push(newWindow);
        return newWindow;
    }

    public void displayCurrentScene() {
        stage.setScene(sceneStack.peek());
    }

    /**
     * destroys the last opened window and displays the next scene
     */
    public void destroyCurrentWindow() {
        sceneStack.pop();
        stage.setScene(sceneStack.peek());
    }

    /**
     * @param root root node to add to new scene
     * @param sx   scene x dimemsion
     * @param sy   scene y dimension
     * @return scene containing root as root node with dimensions (sx, sy)
     */
    public Scene createNewWindow(Node root, double sx, double sy) {
        Scene newWindow = new Scene((Parent) root, sx, sy);
        sceneStack.push(newWindow);
        return newWindow;
    }
}
