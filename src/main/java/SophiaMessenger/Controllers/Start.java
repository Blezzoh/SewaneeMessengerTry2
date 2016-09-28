package SophiaMessenger.Controllers;

import SophiaMessenger.Models.DBMessage;
import com.google.api.services.gmail.model.Message;
import de.email.aux.ImageBot;
import de.email.core.Inbox;
import de.email.database.Conn;
import de.email.database.MessageTableManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Daniel Evans and Blaise Iradukunda
 */

public class Start extends Application {

    static final String COMPOSE_STYLE = "-fx-background-color: rgba(7, 171,202,.7); -fx-font-family: Trebuchet MS; -fx-font-size: 13px; -fx-font-weight: bold; -fx-border-color: white;"
            +"-fx-effect: dropshadow(gaussian, black, 2, 0, 3, 3); -fx-border-insets: 3px; -fx-border-width: 2px; -fx-text-fill: white";
    private static final String TOP_STYLE = "-fx-background-color: rgba(7, 171,202,.7); -fx-padding: 15px; -fx-spacing: 15px; -fx-start-margin: 40px; -fx-border-color:rgba(255, 153, 51, .8);" +
            "-fx-border-radius: 3px" ;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        Connection conn = null;
        Inbox i = new Inbox("evansdb0@sewanee.edu");
        try {
            conn = Conn.makeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            MessageTableManager.createMessageTable();
            MessageTableManager.fillTable(i);
            MessageTableManager.updateMessageTable(i);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        ImageBot.createLookupTable(conn);
        ImageBot ib = null;
        try {
            ib = new ImageBot();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ib != null) {
            List<Message> messages = i.getInbox();
            for (int j = 0; j < 80; j++) {
                DBMessage m = null;
                try {
                    m = new DBMessage(messages.get(j));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (m != null) {
                    String suffix = ib.getEmailSuffix(m.getFromEmail());
                    try {
                        if (ib.lookupURL(suffix) == null)
                            ib.store(suffix, ib.parseSenderImage(m.getFromEmail()));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        BaseController bc = new BaseController("evansdb0@sewanee.edu");
        Scene scene = new Scene(bc);
        bc.pushStack(scene);
        scene.getStylesheets().add("MessengerStyle.css");

        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNIFIED);
        primaryStage.setWidth(Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 50);
        primaryStage.setHeight(Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 100);
        primaryStage.show();
    }
}