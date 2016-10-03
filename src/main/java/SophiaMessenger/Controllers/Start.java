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
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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
        TextInputDialog dialog = new TextInputDialog("evansdb0@sewanee.edu");
        dialog.setTitle("Gmail Authentication");
        // TODO: CREATE MAPPING BETWEEN THE USER'S EMAILS
        // TODO: AND THE USER'S EMAIL ADDRESS IN THE DATABASE CACHE
        // TODO: CREATE STORED MYSQL PROCEDURE TO DELETE EMAILS
        // TODO: THAT HAVE BEEN DELETED ON THE GMAIL SERVER
        dialog.setHeaderText("Enter you Gmail address");
        Inbox i = null;
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            i = new Inbox(result.get());
        }
        if (i != null) {
            Connection conn = null;
            List<Message> messages = null;
            long t = System.currentTimeMillis();
            messages = i.getInbox();
            System.out.println(System.currentTimeMillis() - t);

            try {
                conn = Conn.makeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                MessageTableManager.createMessageTable();
                MessageTableManager.fillTable(i);
                // TEMPORARILY DISABLING UPDATE TO DECREASE ITERATION TIME
//            MessageTableManager.updateMessageTable(i, true);
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
                for (int j = 0; j < 80; j++) {
                    DBMessage m = null;
                    try {
                        System.out.println(messages.get(j).getId());//              <------ print out id so that I can check when email address is null
                        m = new DBMessage(messages.get(j));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (m != null) {
                        boolean failed = false;
                        try {
                            String suffix = ib.getEmailSuffix(m.getFromEmail());
                        } catch (NullPointerException e) {
                            failed = true;
                            e.printStackTrace();
                            try {
                                MessageTableManager.updateMessageTable(i, true);
                            } catch (IOException | SQLException e1) {
                                e1.printStackTrace();
                            }
                        }
                        if (failed) {
                            System.out.println("Failed trying to sleep and wait");
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        String suffix = ib.getEmailSuffix(m.getFromEmail());
                        failed = false;
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
            BaseController bc = new BaseController(i, messages);
            Scene scene = new Scene(bc);
            SceneManager sceneManager = new SceneManager(stage, scene);
        /* Allows bc and all subManagers (controllers) to create new windows */
            bc.setSceneManager(sceneManager);

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
}