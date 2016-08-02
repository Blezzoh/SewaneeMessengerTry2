package messenger_interface;

import com.danielevans.email.FullMessage;
import com.danielevans.email.Inbox;
import com.google.api.services.gmail.model.Message;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

/**
 * @author Daniel Evans and Blaise Iradukunda
 */

public class Controller extends Application {

    private final TextField searchField = new TextField();
    Pagination pagination;
    long searchTime;
    private TilePane center;
    private Inbox inbox;
    private String imgUrl =
            "http://4.bp.blogspot.com/-SjsG6gvCasI/Ve6PJxhPEiI/AAAAAAAAFYU/dYvGfnIxPzk/s1600/Kundwa%2BDoriane%2Brwanda.jpg";
    private List<Message> messages;
    private ScrollPane sp;
    private long pagingTime;
    private int itemsPerPage = 15;
    private Hashtable<String, FullMessage> emailData;
    private BorderPane root;
    private ComposerManager cm;
    private Stack<Scene> sceneStack;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // inbox gives access to the user's gmail messages using an authenticator
        inbox = new Inbox("iradub0@sewanee.edu");

        // loads the user's inbox
        messages = inbox.getDefaultInbox();

        long initTime = System.currentTimeMillis();
        initEmailData();


        // root container of the interface
        root = new BorderPane();

        HBox top = new HBox(5);
        Button composeButton = new Button("Compose");
        top.getChildren().add(composeButton);
        composeButton.setOnMousePressed(event -> compose());
        root.setTop(top);
        cm = new ComposerManager();
        BorderPane right = new BorderPane();
        root.setRight(right);
        right.setTop(cm);

        initSpAndCenter();

        HBox bottomMenu = makeBottomMenu(root, sp);
        root.setBottom(bottomMenu);

        // creating title for application and scene
        primaryStage.setTitle("Sewanee Messenger");
        Scene scene = new Scene(root, 1100, 800);
        scene.getStylesheets().add("MessengerStyle.css");

        sceneStack = new Stack<>();
        sceneStack.push(scene);

        // on key pressed gives functionality for what user types automatically
        //showing up in the search box
        scene.setOnKeyPressed
                (event ->
                {
                    try {

                        // USER HIT ENTER
                        if (event.getCode() == KeyCode.ENTER) {
                            searchTime = System.currentTimeMillis();
                            userSearchForMessages(root, sp);
                        }
                        char key = event.getText().charAt(0);
                        // USER HIT AN ALPHA-NUMERIC KEY
                        if (Character.isLetter(key)) {
                            // checks if searchField is already visible, displays w/ animations if not
                            displaySearchField(searchField);
                        }
                    } catch (StringIndexOutOfBoundsException e) { /* user pressed a non-alphanumeric key */ }
                });
        // creates the paginator based on the messages in the @field{messages} field
        createPaginator(root, sp);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNIFIED);
        System.out.println("init time: " +
                (System.currentTimeMillis() - initTime) / 1000.0);
        primaryStage.show();
    }

    public void compose() {
        BorderPane bp = (BorderPane) root.getRight();
        if (bp.getCenter() != null) {
            System.out.println("dflksdfldsjflkdjflkdsjfldsjf");
            Composer center = (Composer) bp.getCenter();
            cm.addComposer(center);
        }
        Composer composer = new Composer(inbox);
        bp.setCenter(composer);
        composer.setVisible(true);
    }

    private void initSpAndCenter() {
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
    }

    private void initEmailData() {
        System.out.println("Initializing email data...");
        emailData = new Hashtable<>(messages.size() * 2);
        for (int i = 0; i < messages.size(); i++) {
            try {
                emailData.put(messages.get(i).getId(), new FullMessage(inbox, messages.get(i)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean startsWith(String prefix, String str) {
        if (prefix.length() > str.length()) return false;
        for (int i = 0; i < prefix.length(); i++) {
            if (prefix.charAt(i) != str.charAt(i))
                return false;
        }
        return true;
    }

    private boolean sw(String prefix, String str) {
        if (prefix.length() > str.length()) return false;
        return prefix.equals(str.substring(0, prefix.length() + 1));
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

    private int getPageCount(List<Message> messages) {
        int pageCount = (int) Math.ceil(messages.size() / itemsPerPage);
        return pageCount == 0 ? 1 : pageCount;
    }

    /**
     * this method requires that the messages field and center field not be null
     * @param root the root interface of the application
     */
    public void createPaginator(BorderPane root, ScrollPane sp) {
        pagination = new Pagination(getPageCount(messages));

        // set page factory is passed a new 'callable'
        // every time the user clicks to a new page this callable is called
        pagination.setPageFactory
                (
                        (Integer pageIndex) ->
                        {
                            try {
                                pagingTime = System.currentTimeMillis();
                                System.out.println("Creating page...");
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
            }
        });

        // pagination represents a scrollPane
        // which obviously allows user to scroll through messages
        root.setCenter(pagination);
    }

    public void userSearchForMessages(BorderPane root, ScrollPane sp) {
        // TODO: HANDLE THE CASE WHERE NO MESSAGES ARE RETURNED
        // only search for messages if the searchField is visible
        // and there is some text in it
        if(!searchField.isVisible() || searchField.getText().equals(""))
            return;
        // time is used to show how long search and ui update takes
        /*List<Message> searchMessages =
                inbox.listMessagesMatchingQuery(searchField.getText());*/

        List<Message> tempMessages = inbox.listMessagesMatchingQuery(searchField.getText());
        // this search does not provide any messages, so return from search
        // TODO: display something to user saying the search returned no messages
        searchField.setVisible(false);
        tempMessages = searchReturnsMessages(tempMessages);
        if (tempMessages.size() == 0) {
            return;
        }

        /**
         *  next step is to store emailData on hard drive,
         *  check the ids of the emailData returned in the search against
         *  the ones on the hardrive, show the emailData of the ones matching
         *  the ids
         */
        messages = tempMessages;
        // do search animation
        createPaginator(root, sp);
    }

    private List<Message> searchReturnsMessages(List<Message> tempMessages) {
        // if tempMessages.size() < 0 return false, if not at least one message
        // returned by search which are stored in tempMessages are not in emailData hashMap return false
        List<Message> filteredMessages = new ArrayList<>();
        for (int i = 0; i < tempMessages.size(); i++) {
            if (emailData.get(tempMessages.get(i).getId()) != null)
                filteredMessages.add(tempMessages.get(i));
        }
        return filteredMessages;
    }

    /**
     * handles the restructuring of the message items and fills them with the relevant info
     * when application begins and on search
     *
     * @param pageIndex
     * @param sp
     * @return a restructured scroll pane of message items with correct emails
     * @throws IOException
     */
    private ScrollPane createPage(int pageIndex, ScrollPane sp) throws IOException {
        // TESTING
        if(center == null || center.getChildren() == null)
            throw new NullPointerException("center or the container containing its children is null");
        if(messages == null)
            throw new NullPointerException("messages is null");

        int page = pageIndex * itemsPerPage;
        // messageItemNum used to iterate through the messageItems
        int messageItemNum = 0;
//        long fmpageTime = System.currentTimeMillis();
        for (int i = page; i < page + itemsPerPage && i < messages.size(); i++) {
            // need to initialize the message items if they haven't already been initialized
            if (center.getChildren().size() < itemsPerPage) {
                center.getChildren().add(new MessageItemInPane(new MessageItem
                        (root, sceneStack, emailData.get(messages.get(i).getId()), imgUrl)));
                // message items have been init'd so just change the information in the objects
            } else {
                long time = System.currentTimeMillis();
                String mId = messages.get(i).getId();
                // if the search returned messages, that's the only case when we want to reset info in the mItem fields
                MessageItemInPane item = (MessageItemInPane) center.getChildren().get(messageItemNum);
                MessageItem mItem = item.getMsgItem();
                mItem.setFm(emailData.get(mId));
                // add info to the message items
//                mItem.setMessageId(mId);
/*                mItem.setSenderField(MessageParser.parseSenderFromEmail(emailData.get(mId)));
                String body = emailData.get(mId).getBestMessageBody();
                mItem.setBodyText(body);
                mItem.setSubjectField(emailData.get(mId).getSubject());
                mItem.setSnippetField(emailData.get(mId).getSnippet());*/
                ++messageItemNum;
            }
        }
        sp.setContent(center);
        return sp;
    }

    private HBox makeBottomMenu(BorderPane root, ScrollPane sp) {
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
                userSearchForMessages(root, sp);
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