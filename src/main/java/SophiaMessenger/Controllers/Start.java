package SophiaMessenger.Controllers;

import SophiaMessenger.Models.DBMessage;
import com.google.api.services.gmail.model.Message;
import de.email.aux.ImageBot;
import de.email.core.Inbox;
import de.email.database.Config;
import de.email.database.Conn;
import de.email.database.DB;
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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws FileNotFoundException {

        stage.initStyle(StageStyle.TRANSPARENT);
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
            ib = new ImageBot(conn);
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
                        // "SELECT url FROM " + imageTable + " WHERE suffix = ?"
                        if (DB.lookup("url", Config.IMAGE_LOOKUP, "suffix", "=", suffix) == null)
                            ib.store(suffix, ib.parseSenderImage(m.getFromEmail()));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        BaseController bc = new BaseController("evansdb0@sewanee.edu");
        Scene scene = new Scene(bc);
        bc.pushStack(scene);

        scene.getStylesheets()
                .addAll("MessengerStyle.css",
                        "MessageView.css",
                        "TopMenu.css");
        scene.getStylesheets().add("http://fonts.googleapis.com/css?family=Lora");

        stage.setScene(scene);
        stage.initStyle(StageStyle.UNIFIED);
        stage.setWidth(Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 200);
        stage.setHeight(Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 300);
        stage.show();
    }
}