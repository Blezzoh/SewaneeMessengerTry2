package SophiaMessenger.Controllers;

import SophiaMessenger.Models.ComposerData;
import SophiaMessenger.Views.Composer;
import SophiaMessenger.Views.ComposerButton;
import com.google.api.services.gmail.model.Draft;
import de.email.core.Authenticator;
import de.email.core.FullMessage;
import de.email.core.Inbox;
import de.email.core.Mailer;
import de.email.interfaces.Mail;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import javax.mail.MessagingException;
import javax.swing.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by daniel on 7/31/16.
 *
 * @author Daniel Evans
 */
public class ComposerManager extends BorderPane {

    private static final int COMPOSER_MAX = 3;
    private static final int REPLY = 0;
    private static final int FWD = 1;
    private static final int DRAFT = 2;
    private static final String SEND_ERROR_MESSAGE = "Failed to send message. Please retry later.";
    private static final String SEND_SUCCESS_MESSAGE = "Message sent successfully.";
    private LinkedList<ComposerData> composerDataList;
    private HBox buttonContainer;
    private Composer composer;
    private ComposerButton currentButton;
    private ComposerData currentData;
    private Authenticator auth;


    public ComposerManager(Inbox inbox) {

        super();
        setBackground(Background.EMPTY);
        this.auth = inbox.getAuth();
        composer = new Composer();
        composer.setVisible(false);
        setComposerEvents();
        composerDataList = new LinkedList<>();
        buttonContainer = new HBox(8);
        this.setBottom(buttonContainer);
    }

    private void setComposerEvents() {

        // email address field event
        composer.getEmailAddress().setOnKeyReleased(e -> {
            if (currentButton != null) {
                currentButton.setText("To: " + composer.getEmailAddress().getText());
            }
        });

        // xCloser imageView event
        composer.getxCloser().setOnMousePressed(e -> {
            // >>>>>>>>>>>>>>>>>>>>>>> TEMPORARY COMMENTED OUT THE DRAFT CREATION <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
//                                             currentData.createDraft();

            // there is at least two active ComposerData
            // so I can fill the composer with the data
            // from the non-active ComposerData
            if (composerDataList.size() > 1) {
                ComposerData cd;
                // if the current ComposerData is not the first one created
                if (currentData.getComposerDataId() != 1)
                    cd = getComposerData(currentData.getComposerDataId() - 1);
                else // it is the first one created
                    cd = getComposerData(currentData.getComposerDataId() + 1);

                composerDataList.remove(currentData);
                currentData = cd;
                setComposerFields(currentData);
                buttonContainer.getChildren().remove(currentButton);
                currentButton = getButton(currentData.getComposerDataId());
                colorButtons(currentButton);
                System.out.println("composer data list size -> " + composerDataList.size());
            }
            // close the Composer because there is not other ComposerData
            else {
                // set Composer visibility false and clear all fields
                hideComposer();
                // removes currentData (ComposerData) from composerDataList
                // and currentButton (ComposerButton) from buttonContainer
                removeDataFromLists();
            }
        });
        composer.getSend().setOnMousePressed(e -> {
            try {
                updateCurrentData(); // update current data fields w/ composer data fields
                new Mailer(currentData, auth).sendMessage();
                // removes current ComposerData and currentButton from UI and from back end data containers
                hideComposer();
                removeDataFromLists();
                showComposerNotification(SEND_SUCCESS_MESSAGE);
            } catch (MessagingException | IOException e1) {
                showComposerNotification(SEND_ERROR_MESSAGE);
                e1.printStackTrace();
            }
        });
    }

    /**
     *
     * @param messageToUser some message indicating to user the success or failure of their action
     */
    public void showComposerNotification(String messageToUser) {
        composer.getNotificationsToUser().setText(messageToUser);
        composer.getNotificationsToUser().setVisible(true);
        Timer timer = new Timer(2000, e ->
                /*hide notification*/
                composer.getNotificationsToUser().setVisible(false));
        timer.start();
    }

    private void updateCurrentData() {
        currentData.setBody(composer.getBodyText().getText());
        currentData.setCc(composer.getCc().getText());
        currentData.setEmailAddress(composer.getEmailAddress().getText());
        currentData.setSubject(composer.getSubject().getText());
    }

    private void removeDataFromLists() {
        composerDataList.remove(currentData);
        buttonContainer.getChildren().remove(currentButton);
        System.out.println("composer data list size -> " + composerDataList.size());
    }

    /**
     * When calling generateId(), make sure to generate the id before
     * and set it to a ComposerData object before adding the ComposerData
     * to the ComposerDataList
     * @return a unique id for the corresponding ComposerData
     */
    private int generateId() {
        return composerDataList.size() + 1;
    }

    /**
     * Builds the composer from a draft, filling the corresponding fields of the composer
     * @param draft draft to be edited in the Composer and sent
     * @return true if the Composer was created successfully
     */
    public boolean createComposerFromDraft(Draft draft) throws IOException {
        FullMessage fm = new FullMessage(auth, draft.getMessage());
        return newComposer(fm, ComposerManager.DRAFT);
    }

    /**
     * Builds the composer from the data in the FullMessage
     *
     * @param fm needed to fill the corresponding fields of the composer
     * @return true if the Composer was created successfully
     */
    public boolean createComposerWithReplyTo(FullMessage fm) {
        return newComposer(fm, ComposerManager.REPLY);
    }

    /**
     * Builds the composer from the data in the FullMessage
     *
     * @param fm needed to fill the corresponding fields of the composer
     * @return true if the Composer was created successfully
     */
    public boolean createComposerWithFwdMessage(FullMessage fm) {
        return newComposer(fm, ComposerManager.FWD);
    }

    /**
     * Builds a new composer with all data fields empty and ready for
     * editing of a new message
     * @return true if the Composer was created successfully
     */
    public boolean createNewMessage() {
        return newComposer(null, ComposerManager.FWD);
    }

    /**
     * @param mailData pass either a FullMessage, ComposerData, or null if there is no data to initialize the Composer with
     * @param typeOfComposer pass one of the predefined public ints: REPLY, FWD, NEW_MESSAGE, DRAFT;
     *                       the ComposerManager will automatically fill the correct fields in the Composer
     * @return returns true if the composer was correctly assembled, false otherwise
     */
    private boolean newComposer(Mail mailData, int typeOfComposer) {
        if (composerDataList.size() != COMPOSER_MAX) {

            saveCurrentComposerData();
            showComposer();
            setComposerTypeAndFillComposerFields(mailData, typeOfComposer);

            // ComposerData
            ComposerData cd = new ComposerData(composer);
            currentData = cd;  // reset the current ComposerData
            cd.setComposerDataId(generateId()); // do this before adding to composerDataList
            composerDataList.add(cd);

            // ComposerButton
            ComposerButton cmButton = new ComposerButton();
            currentButton = cmButton; // reset the current ComposerButton
            cmButton = composerButtonSettings(cmButton);
            cmButton.setText("To: ");
            cmButton.setComposerDataId(cd.getComposerDataId());
            buttonContainer.getChildren().add(cmButton);
            colorButtons(cmButton);


            // ComposerButton event
            cmButton.setOnMousePressed
                    (e -> {
                        saveCurrentComposerData();
                        ComposerButton button = (ComposerButton) e.getSource();
                        currentButton = button;
                        colorButtons(button);

                        currentData = getComposerData(button.getComposerDataId());
                        setComposerFields(currentData);
                    });
            System.out.println("composer data list size -> " + composerDataList.size());
            return true;
        }
        return false;
    }

    private ComposerButton getButton(int composerButtonId) {
        for (int i = 0; i < buttonContainer.getChildren().size(); i++) {
            ComposerButton button = (ComposerButton) buttonContainer.getChildren().get(i);
            if (button.getComposerDataId() == composerButtonId)
                return button;
        }
        return null;
    }

    private void saveCurrentComposerData() {
        // take care of the data stored in the current state of the composer by
        // storing it in the currentData/currentButton field, which corresponds
        // to ComposerButton/ComposerData created on the previous call to newComposer()
        // the below condition will be true on all calls to composer such that there is
        // at least one ComposerButton on the screen
        if (currentData != null) {
            currentData.setBody(composer.getBodyText().getText());
            currentData.setCc(composer.getCc().getText());
            currentData.setEmailAddress(composer.getEmailAddress().getText());
            currentData.setSubject(composer.getSubject().getText());

            currentButton.setText("To: " + currentData.getEmailAddress());
        }
    }

    private void colorButtons(Button cmButton) {
        for (int i = 0; i < buttonContainer.getChildren().size(); i++) {
            Button button = (Button) buttonContainer.getChildren().get(i);
            if (button == cmButton)
                button.setStyle("-fx-background-color: rgba(7, 171,202,.7); -fx-text-fill: white");
            else
                button.setStyle("-fx-background-color: rgba(7, 171,202,1); -fx-text-fill: white");
        }
    }

    private void showComposer() {
        this.setCenter(composer);
        composer.setVisible(true);
        composer.clearAllTextFields();
    }

    private void setComposerTypeAndFillComposerFields(Mail mailData, int composerType) {
        if (mailData instanceof FullMessage) {
            FullMessage fm = (FullMessage) mailData;
            if (composerType == ComposerManager.REPLY) {
                composer.getSubject().setText("Reply: " + fm.getSubject());
                composer.getEmailAddress().setText(fm.getFromEmail());
                composer.getBodyText().requestFocus();
            } else if (composerType == ComposerManager.FWD) {
                composer.getSubject().setText("Fwd: " + fm.getSubject());
                // TODO: what if the best message body is HTML?????
                composer.getBodyText().setText(fm.getBody());
                composer.getEmailAddress().requestFocus();
            } else if (composerType == ComposerManager.DRAFT) {
                // create composer from a draft
            }
        } else  // user is creating a new message
            composer.getEmailAddress().requestFocus();
    }

    public boolean checkTextFieldsEmpty() {
        return !composer.getEmailAddress().getText().equals("") ||
                !composer.getSubject().getText().equals("") ||
                !composer.getCc().getText().equals("") ||
                !composer.getBodyText().getText().equals("");
    }

    public void hideComposer() {
        composer.clearAllTextFields();
        composer.setVisible(false);
        this.setCenter(null);
    }

    private ComposerData getComposerData(int composerId) {
        ListIterator<ComposerData> li = composerDataList.listIterator();
        while (li.hasNext()) {
            ComposerData next = li.next();
            if (next.getComposerDataId() == composerId)
                return next;
        }
        return null;
    }

    private void setComposerFields(ComposerData data) {
        composer.getEmailAddress().setText(data.getEmailAddress());
        composer.getSubject().setText(data.getSubject());
        composer.getBodyText().setText(data.getBody());
        composer.getCc().setText(data.getCc());
    }

    private ComposerButton composerButtonSettings(ComposerButton button) {
        button.setFont(Font.font("Trebuchet MS", 15));
        button.setStyle("-fx-background-color: blue; -fx-text-fill: white");
        return button;
    }
}
