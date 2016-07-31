package messenger_interface;

import com.danielevans.email.FullMessage;
import com.danielevans.email.LabelMaker;
import javafx.animation.FadeTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by iradu_000 on 7/19/2016.
 * @author Blaise Iradukunda
 * The purpose of this is to implement all the functionality of the message item in a user friendly way
 * It extends a StackPane in order to facilitate overlaying more than one layout on the top of another
 */
public class MessageItemInPane extends StackPane {

    private final Timer timer;
    private MessageItem msgItem;
    /**
     * repeatsPlusOne: int to control the repeat of the Timer "timer"
     * Timer: timer in charge of displaying the notificationIcon for a limited time
     * Count: int to control the the display of the labels list's VBox
     * the ImageViews are the 6 icons that appear on the message item in order to provide options to apply on the MessageItem
     */
    private int repeatsPlusOne = 2;
    private ListLabelsOnHover allLabels;
    private int count=0;
    private ListView<String> labelsList;
    private NotificationIcon hoveredIcon, notificationIcon;
    private ImageView reply, forward, mark, addToTrash, download, label;
    public MessageItemInPane(MessageItem messageItem) throws FileNotFoundException {

        super();
        msgItem = messageItem;
        label = msgItem.getLabelEmail();
        timer = new Timer();
        label.setOnMouseClicked(event -> showLabels());
        hoveredIcon = new NotificationIcon("string");
        hoveredIcon.setStyle("-fx-background-color: white;");
        notificationIcon = new NotificationIcon("DONE");
        reply = msgItem.getReplyEmail();
        forward = msgItem.getForwardEmail();
        mark = msgItem.getMarkEmail();
        addToTrash = msgItem.getAddToTrash();
        download = msgItem.getDownloadAttach();

        /**
         * a listener that controls the clicked label in the labels list's VBox
         * it moves the corresponding email in the specific label
         */
        FullMessage fm = messageItem.getFm();
        InvalidationListener listener = (Observable observable) -> {
            System.out.println(labelsList.getSelectionModel()
                    .getSelectedItem() + "\n" + messageItem);

            System.out.println(labelsList.getSelectionModel().getSelectedItem());
            LabelMaker.modifyMessage(fm, fm.getId()
                    , labelsList.getSelectionModel().getSelectedItem(),
                    true);
            hideLabels();
        };

        setIconsProperties();


        allLabels = new ListLabelsOnHover(fm);
        setLabelsProperties(allLabels);

        labelsList = allLabels.getAllLabels();
        labelsList.getSelectionModel().selectedItemProperty().addListener(listener);
        this.getChildren().addAll(msgItem, allLabels, hoveredIcon,notificationIcon);
        hoveredIcon.setTranslateY(18);
        hoveredIcon.setVisible(false);
        notificationIcon.setVisible(false);
        notificationIcon.setStyle("-fx-background-color: black;");
        notificationIcon.setTextFill(Color.WHITE);
    }

    /**
     * private method to add listeners on the MessageItem icons
     * the icons provides different set of options that can be applied to a message
     */

    private void setIconsProperties() {

        reply.setOnMouseEntered(event -> showIcon("reply"));
        forward.setOnMouseEntered(event -> showIcon("forward"));
        mark.setOnMouseEntered(event -> showIcon("mark as read"));
        addToTrash.setOnMouseEntered(event -> showIcon("add to trash"));
        download.setOnMouseEntered(event -> showIcon("add to trash"));
        label.setOnMouseEntered(event -> showIcon("label this message as"));

        reply.setOnMouseExited(event -> removeIcon());
        label.setOnMouseExited(event -> removeIcon());
        forward.setOnMouseExited(event -> removeIcon());
        mark.setOnMouseExited(event -> removeIcon());
        addToTrash.setOnMouseExited(event -> removeIcon());
        download.setOnMouseExited(event -> removeIcon());

        addToTrash.setOnMouseClicked(event -> trashEmail());
//        mark.setOnMouseClicked(event -> moveEmail("READ"));

    }

    /**
     *move the email to a label s and display to the user that the task is done
     */
    private void trashEmail() {

        boolean b = msgItem.getFm().trashMessage();

        if (b) {
            FadeTransition st = new FadeTransition(Duration.millis(375), this);
            st.setFromValue(1);
            st.setToValue(0);
            st.setCycleCount(1);
            st.setAutoReverse(true);
            FlowPane flowPane = (FlowPane) this.getParent();
            st.setOnFinished(e -> flowPane.getChildren().remove(this));
            st.play();
        }
        else {
            startNotificationCountdown();
        }

    }

    /**
     * times the duration a notification
     */
    private void startNotificationCountdown() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                notificationIcon.setText("FAILED");
                notificationIcon.setVisible(true);
                decRepeats();
            }
        },0, 2000);
    }

    /**
     * Method in charge of cancelling the notification timer
     */

    private void decRepeats() {
        repeatsPlusOne--;
        if(repeatsPlusOne == 0){
            notificationIcon.setVisible(false);
            timer.cancel();
        }
    }

    /**
     * the next two methods interact with the visibility of the hovered icon and the text displayed by this icon
     */
    private void removeIcon() {
        hoveredIcon.setVisible(false);
    }

    private void showIcon(String s) {
        hoveredIcon.setLabelText(s);
        hoveredIcon.setVisible(true);
    }

    /**
     * @param allLabels a list all the labels that the user have in a VBox
     * its adds functionality and listeners to this list
     */
    private void setLabelsProperties(ListLabelsOnHover allLabels){

        allLabels.setVisible(false);
        allLabels.setOnMouseEntered(event -> increaseCount());
        allLabels.setOnMouseExited(event -> hideLabels());
        allLabels.setScaleX(0.7);
        allLabels.setPrefHeight(msgItem.getHeight());

    }

    /**
     * hides all the labels
     */
    private void hideLabels() {
        if (count==1){
            allLabels.setVisible(false);
            count=0;
        }
    }

    /**
     * increases the count that controls the visibility of the labels list
     */
    private void increaseCount() {
        count = 1;
    }

    /**
     * displays the list of all the labels in a VBox
     */
    private void showLabels() {
        hoveredIcon.setVisible(false);
        allLabels.setVisible(true);
    }

    /**
     * @return  the MessageItem one of the nodes in this StackPane
     * for full detail Read the Message Item
     */
    public MessageItem getMsgItem() {

        return msgItem;
    }

    public void setMsgItem(MessageItem msgItem) {
        this.msgItem = msgItem;
    }




}
