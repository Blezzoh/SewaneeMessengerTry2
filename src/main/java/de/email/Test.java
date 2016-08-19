package de.email;

import de.email.database.MessageTableManager;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Daniel Evans on 7/13/16.
 * @author Daniel Evans
 */
public class Test {


    private static final String MESSAGE_ID = "1562ca4c4997819d";

    public static void main(String[] args) throws IOException, InterruptedException {

        Inbox inbox = new Inbox("iradub0@sewanee.edu");
        try {
            MessageTableManager.createMessageTable();
//            System.out.println(MessageTableManager.update(inbox));
//            MessageTableManager.fillTable(inbox);
            MessageTableManager.updateMessageTable(inbox);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
