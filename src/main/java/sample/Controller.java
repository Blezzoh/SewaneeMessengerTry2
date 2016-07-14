package sample;

import com.danielevans.email.Authenticator;
import com.danielevans.email.FullMessage;
import com.danielevans.email.Inbox;
import com.danielevans.email.MessageParser;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class Controller extends Application {

    private final TextField searchField = new TextField();
    Pagination pagination;
    long searchTime;
    private TilePane center;
    private Inbox inbox;
    private String imgUrl =
"http://4.bp.blogspot.com/-SjsG6gvCasI/Ve6PJxhPEiI/AAAAAAAAFYU/dYvGfnIxPzk/s1600/Kundwa%2BDoriane%2Brwanda.jpg";
    private List<Message> messages;
    private int endMessageIndex = 10;
    private Text messageToUser;
    private ScrollPane sp;
    private long pagingTime;
    private int itemsPerPage = 15;
    /**
     * last date represents the date of the last message in
     * emailData array. It is used in the search queries
     * provided to google's search. That is, if a message we are
     * searching for based on some query from user is dated before
     * lastDate, it won't be returned in the search because that
     * implies that this message is not in the emailData array
     * <p>
     * This will need to be updated so that we aren't loading everything
     * into ram cause that takes forever
     */
    private String lastDate;
    /*
    purposefully left uninitialized
     */
    private FullMessage[] fullMessages;
    private FullMessage[] emailData;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {


        /**
         *    inbox gives access to the user's gmail messages using an authenticator
         */
        inbox = new Inbox(new Authenticator("evansdb0@sewanee.edu"));
        /**
         *  loads the user's inbox
         */
        messages = inbox.getDefaultInbox();

        emailData = new FullMessage[messages.size()];
        long initTime = System.currentTimeMillis();
        for (int i = 0; i < emailData.length; i++) {
            try {
                emailData[i] = new FullMessage(inbox, messages.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // earliest date stored in last element of emailData
        lastDate = MessageParser.parseDate
                (emailData[emailData.length - 1].getDate());

        System.out.println("last date = " + lastDate);

        System.out.println("init time: " +
                (System.currentTimeMillis() - initTime) / 1000.0);
        /*
        how many messages in the user's inbox == around 4100 for me
         */
        System.out.println("the messages size is " + messages.size());
//        List<String> emailAddresses = inbox.loadEmailAddresses(messages);
        /**
         *   root container of the interface
         */
        BorderPane root = new BorderPane();
        ComposeMessage cm = new ComposeMessage(inbox, this);
        root.setRight(cm.getRoot());

        /**
         *   creates and loads the messages for the center of the root
         */
        center = new TilePane();
        center.setHgap(5);
        center.setVgap(5);
        center.setPadding(new Insets(8, 0, 8, 8));

        sp = new ScrollPane(center);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setFitToWidth(true);

        HBox bottomMenu = makeBottomMenu(root, sp, cm);
        root.setBottom(bottomMenu);

        /**
         *      creating title for application and scene
         */
        primaryStage.setTitle("Hello World");
        Scene scene = new Scene(root, 900, 700);
        /**
         *       on key pressed gives functionality for what user types automatically
         *       showing up in the search box
         */
        scene.setOnKeyPressed
                (event ->
                {
                    try {
                /*
                // attempt to search email addresses while user types
                setMessageToUser(searchEmailAddressOnKeyPressed
                        (emailAddresses, searchField.getText()));
                */

                        // USER HIT ENTER
                        if (event.getCode() == KeyCode.ENTER) {
                    searchTime = System.currentTimeMillis();
                    userSearchForMessages(root, sp, cm);

                }
                char key = event.getText().charAt(0);
                        // USER HIT AN ALPHA-NUMERIC KEY
                if ((key >= 49 && key <= 57)
                        || (key >= 65 && key <= 90)
                        || (key >= 97 && key <= 122)) {
                    // checks if searchField is already visible, displays w/ animations if not
                    displaySearchField(searchField);
                }
                    } catch (StringIndexOutOfBoundsException e) { /* user pressed a non-alphanumeric key */ }
        });
        /**
         * creates the paginator based on the messages in the @field{messages} field
         */
        createPaginator(root, sp, cm);
        /**
         *     if the user clicks on the scroll pane,
         */
        // message to user provides feedback on what they are doing and debugging purposes
        messageToUser = new Text();
        messageToUser.setVisible(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String searchEmailAddressOnKeyPressed(List<String> emailAddresses,
                                                 String searchText) {

        int foundIndex = 0;
        for (int i = 0; i < emailAddresses.size(); i++) {
            if(emailAddresses.get(i).startsWith(searchText)) {
                foundIndex = i;
            }
        }
        return emailAddresses.get(foundIndex);
    }

    public void setMessageToUser(String text) {
        // TODO: add parameters to make this look nice
        messageToUser.setText(text);
        // might be bad to set visibility to true
        messageToUser.setVisible(true);
    }

    /**
     * this method requires that the messages field and center field not be null
     * @param root the root interface of the application
     */
    public void createPaginator(BorderPane root, ScrollPane sp, ComposeMessage cm) {
        pagination = new Pagination((int) Math.ceil(messages.size() / itemsPerPage));
        pagination.setStyle("-fx-border-color:red;");

        // set page factory is passed a new 'callable'
        // every time the user clicks to a new page this callable is called
        pagination.setPageFactory
                (
                        (Integer pageIndex) ->
                        {
                            try {
                                pagingTime = System.currentTimeMillis();
                                return createPage(pageIndex, sp);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                );
        pagination.setOnMousePressed(e ->
        {
            if (e.getClickCount() == 1)
            {
                searchField.setVisible(false);
                messageToUser.setVisible(false);
            }
        });
        /**
         *    paginaation represents a scrollPane,
         *    which obviously allows user to scroll through messages
         */
        root.setTop(messageToUser);
        root.setCenter(pagination);
    }
    /*
    the speed at which the ui updates is heavily dependent on what itemsPerPage()
    returns. items per page is  = to the number of message items displayed per page
     */

    public void userSearchForMessages(BorderPane root, ScrollPane sp,
                                      ComposeMessage cm) {
        // TODO: HANDLE THE CASE WHERE NO MESSAGES ARE RETURNED
        // only search for messages if the searchField is visible
        // and there is some text in it
        if(!searchField.isVisible() || searchField.getText().equals(""))
            return;
        // time is used to show how long search and ui update takes
        /*List<Message> searchMessages =
                inbox.listMessagesMatchingQuery(searchField.getText());*/

        /*for (int i = 0; i < messages.size(); i++) {
            String smId = searchMessages.get(i).getId();
            for (int j = 0; j < emailData.length; j++) {
                if(smId.equals(emailData[j].getId())) {
                    messages. = searchMessages.get(i);
                    break;
                }
            }
        }*/
        /**
         *  next step is to store emailData on hard drive,
         *  check the ids of the emailData returned in the search against
         *  the ones on the hardrive, show the emailData of the ones matching
         *  the ids
         */
        messages = inbox.listMessagesMatchingQuery(searchField.getText()
                + " after:" + lastDate);
        System.out.println(messages.size());
        // do search animation
        createPaginator(root, sp, cm);
    }

    private ScrollPane createPage(int pageIndex, ScrollPane sp) throws IOException {
        if(center == null || center.getChildren() == null)
            throw new NullPointerException("center or the container containing its children is null");
        if(messages == null)
            throw new NullPointerException("messages is null");

        int page = pageIndex * itemsPerPage;
        // messageItemNum used to iterate through the messageItems
        int messageItemNum = 0;
        long fmpageTime = System.currentTimeMillis();
        for (int i = page; i < page + itemsPerPage && i < messages.size(); i++) {
            // need to initialize the message items if they haven't already been initialized
            if (center.getChildren().size() == 0)
                for (int j = page; j < page + itemsPerPage && j < messages.size(); j++)
                    center.getChildren().add(new MessageItem(emailData[j], imgUrl));

            long time = System.currentTimeMillis();

            // get MesasgeItem from object pool in center.getChildren
            MessageItem mItem = (MessageItem) center.getChildren().get(messageItemNum);
            // add info to the message items
            mItem.setSenderField(MessageParser.parseNameFromEmail(emailData[i]));
            mItem.setSubjectField(emailData[i].getSubject());
            mItem.setSnippetField(emailData[i].getSnippet());
            System.out.println("1 full message: "
                    + ((System.currentTimeMillis() - time) / 1000.0)
                    + " seconds");
            ++messageItemNum;
            Message m = new Message();
            MessagePart mp = new MessagePart();

        }
        sp.setContent(center);

        // timing tests --------------------------------------------------

        /*
        System.out.println("fmpagetime: searching for messages with only FullMessage creation took "
                + ((System.currentTimeMillis() - fmpageTime) / 1000.0)
                + " seconds");
        System.out.println("paging time took "
                + ((System.currentTimeMillis() - pagingTime) / 1000.0)
                + " seconds");
        if (searchTime != 0) {
            // about 0 seconds
            System.out.println("search time: "
                    + (System.currentTimeMillis() - searchTime) / 1000.0);
            searchTime = 0;
        }
        */
        // end timing tests ----------------------------------------------------
        return sp;
    }

    private HBox makeBottomMenu(BorderPane root, ScrollPane sp, ComposeMessage cm) {
        HBox lowerMenu = new HBox();
        Rectangle menuRec = new Rectangle(100, 60);
        Rectangle searchRect = new Rectangle(100, 60);


        HBox.setHgrow(menuRec, Priority.max(Priority.SOMETIMES, Priority.ALWAYS));
        HBox.setHgrow(searchField, Priority.max(Priority.SOMETIMES, Priority.ALWAYS));
        HBox.setHgrow(searchRect, Priority.max(Priority.SOMETIMES, Priority.ALWAYS));
        HBox.setMargin(searchField, new Insets(10, 25, 10, 25));
        lowerMenu.setPrefHeight(65);
        lowerMenu.setMaxHeight(65);
        lowerMenu.setPadding(new Insets(2, 2, 2, 2));
        searchField.setPrefHeight(40);
        searchField.setStyle(""
                + "-fx-font-weight: bold;"
                + "-fx-font-size: 16;");
        lowerMenu.setStyle("" + "-fx-background-radius: 5px");
        lowerMenu.setStyle("" + "-fx-border-color: rgba(93,56,107,0.5)");

        Image search = new Image("http://hive.sewanee.edu/iradub0/webDevelopment/search.png");
        Image menu = new Image("http://hive.sewanee.edu/iradub0/webDevelopment/menu.png");


        searchRect.setFill(new ImagePattern(search));
        menuRec.setFill(new ImagePattern(menu));

        menuRec.setArcWidth(10);
        menuRec.setArcHeight(10);
        searchRect.setArcWidth(10);
        searchRect.setArcHeight(10);

        searchField.setVisible(false);

        searchRect.setOnMouseClicked(e ->
        {
            if(searchField.isVisible()) {
                userSearchForMessages(root, sp, cm);
                searchField.setVisible(false);
            } else
                displaySearchField(searchField);
        });
        lowerMenu.getChildren().addAll(menuRec, searchField, searchRect);

        return lowerMenu;
    }

    private void displaySearchField(TextField searchField) {
        if(!searchField.isVisible()) {
            searchField.setVisible(true);
            searchField.requestFocus();

            ScaleTransition st = new ScaleTransition(Duration.millis(375), searchField);
            st.setFromX(0f);
            st.setToX(1f);
            st.setCycleCount(1);
            st.setAutoReverse(true);

            st.play();
        }
    }

}