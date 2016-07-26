package messenger_interface;

import com.danielevans.email.Authenticator;
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
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class Controller extends Application {

    private final TextField searchField = new TextField();
    private FlowPane center;
    private Inbox inbox;
    private String imgUrl =
"http://4.bp.blogspot.com/-SjsG6gvCasI/Ve6PJxhPEiI/AAAAAAAAFYU/dYvGfnIxPzk/s1600/Kundwa%2BDoriane%2Brwanda.jpg";
    private List<Message> messages;
    private int endMessageIndex = 10;
    Pagination pagination;
    long searchTime;
    private Text messageToUser;
    private ScrollPane sp;
    private long pagingTime;
    /*
    purposefully left uninitialized
     */
    private FullMessage[] fullMessages;
    private Pane pane = new Pane();
    private Stage stage;
    private Scene contentScence;
    private Scene scene;
    private WebView messageBody = new WebView();
    private WebEngine engine = messageBody.getEngine();




    @Override
    public void start(Stage primaryStage) {

        stage = primaryStage;
        pane.getChildren().add(messageBody);
        Button b = new Button("back ");
        pane.getChildren().add(b);
        b.setOnMouseClicked(event -> goBack());
        contentScence = new Scene(pane, stage.getX(),stage.getY());
        engine.loadContent(" ");

        /**
         *    inbox gives access to the user's gmail messages using an authenticator
         */
        inbox = new Inbox(new Authenticator("me"));
        /**
         * loads the user's inbox
         */
        messages = inbox.getDefaultInbox();
        /*
        how many messages in the user's inbox == around 4100 for me
         */
        System.out.println("the messages size is " + messages.size());
//        List<String> emailAddresses = inbox.loadEmailAddresses(messages);
        /**
         *
         *   root container of the interface
         */
        BorderPane root = new BorderPane();
        ComposeMessage cm = new ComposeMessage(inbox, this);
        root.setRight(cm.getRoot());

        /**
         *        creates and loads the messages for the center of the root
         */
        center = new FlowPane();
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
        stage.setTitle("Hello World");
        scene = new Scene(root, 900, 700);
        /**
         *       on key pressed gives functionality for what user types automatically
         *       showing up in the search box
         */
        scene.setOnKeyPressed(event -> {
            try {
 /*               setMessageToUser(searchEmailAddressOnKeyPressed
                        (emailAddresses, searchField.getText()));*/
                if(event.getCode() == KeyCode.ENTER) {
                    searchTime = System.currentTimeMillis();
                    userSearchForMessages(root, sp, cm);
                }
                char key = event.getText().charAt(0);
                // if the key is either an upper/lowercase alpha-numeric key
                if ((key >= 49 && key <= 57)
                        || (key >= 65 && key <= 90)
                        || (key >= 97 && key <= 122)) {
                    // checks if searchField is already visible, displays w/ animations if not
                    displaySearchField(searchField);
                }
            } catch (StringIndexOutOfBoundsException e) {
                // user pressed a non-alphanumeric key
            }
        });
        /**
         * creates the paginator based on the messages in the @field{messages} field
         */
        createPaginator(root, sp, cm);
        /**
         *     if the user clicks on the scroll pane,
         */
        stage.setScene(scene);
        stage.show();
    }

    private void goBack() {
        stage.setScene(scene);
        stage.show();
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
        // might be bad to set this visible to true
        messageToUser.setVisible(true);
    }

    /**
     * this method requires that the messages field and center field not be null
     * @param root the root interface of the application
     */
    public void createPaginator(BorderPane root, ScrollPane sp, ComposeMessage cm) {
        pagination = new Pagination((int)Math.ceil(messages.size()/itemsPerPage()));
        pagination.setStyle("-fx-border-color:red;");

        // set page factory is passed a new 'callable'
        // every time the user picks a new page this callable is called
        pagination.setPageFactory
                (
                        (Integer pageIndex) ->
                        {
                            try {
                                pagingTime = System.currentTimeMillis();
                                return createPage(pageIndex, sp, cm);

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
        messageToUser = new Text();
        messageToUser.setVisible(false);
        /**
         *    paginaation represents a scrollPane,
         *    which obviously allows user to scroll through messages
         */
        /*
        Todo: need to move messageToUser business to the start method
         */
        root.setTop(messageToUser);
        root.setCenter(pagination);
    }

    public void userSearchForMessages(BorderPane root, ScrollPane sp,
                                      ComposeMessage cm) {
        // TODO: HANDLE THE CASE WHERE NO MESSAGES ARE RETURNED
        // only search for messages if the searchField is visible
        // and there is some text in it
        if(!searchField.isVisible() || searchField.getText().equals(""))
            return;
        // time is used to show how long search and ui update takes
        messages = inbox.listMessagesMatchingQuery(searchField.getText());
        // do search animation
        createPaginator(root, sp, cm);
    }
    /*
    the speed at which the ui updates is heavily dependent on what itemsPerPage()
    returns. items per page is  = to the number of message items displayed per page
     */
    private double itemsPerPage() {
        return 15.0;
    }

    private ScrollPane createPage(int pageIndex, ScrollPane sp, ComposeMessage cm) throws IOException {
        if(center == null || center.getChildren() == null)
            throw new NullPointerException("center or the container containing its children is null");
        if(messages == null)
            throw new NullPointerException("messages is null");

        int page = (int) (pageIndex * itemsPerPage());
        int arrayIndex = 0;
        if(fullMessages == null) {
            fullMessages = new FullMessage[25];
/*            // remove all previous children
            center.getChildren().remove(0, center.getChildren().size());*/

            // get messages until we hit the max items per page or until we run out of messages
            for (int i = page; i < page + itemsPerPage() && i < fullMessages.length; i++) {

                // TODO: each message Item must store their respective id = message.get(i).getId()

                fullMessages[arrayIndex] = new FullMessage
                        (inbox, messages.get(i));
                MessageItem msgItem = new MessageItem(fullMessages[arrayIndex], imgUrl);
                String str = fullMessages[arrayIndex].getMessageBody();
                System.out.println(str);
                MessageItemInPane msgInPane= new MessageItemInPane(msgItem);
                center.getChildren().add(msgInPane);

            }
            sp.setContent(center);
/*            sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            sp.setFitToWidth(true);*/
            System.out.println("searching for messages with only FullMessage creation took "
                    + ((System.currentTimeMillis() - searchTime) / 1000.0)
                    + " seconds");
            return sp;
        } else {
            for (int i = page; i < page + itemsPerPage() && i < messages.size(); i++) {
                // TODO: each message Item must store their respective id = message.get(i).getId()
                fullMessages[arrayIndex] = new FullMessage
                        (inbox, messages.get(i));
                MessageItemInPane msgInPane = (MessageItemInPane) center.getChildren().get(arrayIndex);
                MessageItem mItem = msgInPane.getMsgItem();
                String str = fullMessages[arrayIndex].getMessageBodyAsHTML();
                if(i == 1)
                    System.out.println(str);
                mItem.setSenderField(fullMessages[arrayIndex].getFrom());
                mItem.setSubjectField(fullMessages[arrayIndex].getSubject());
                mItem.setSnippetField(fullMessages[arrayIndex].getSnippet());
            }
            sp.setContent(center);
/*
            sp.s
            sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            sp.setFitToWidth(true);*/
            System.out.println("searching for messages with only FullMessage creation took "
                    + ((System.currentTimeMillis() - searchTime) / 1000.0)
                    + " seconds");
            System.out.println("paging time took "
                    + ((System.currentTimeMillis() - pagingTime) / 1000.0)
                    + " seconds");
            return sp;
        }
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

    public static void main(String[] args) {
        launch(args);
    }

}