package com.danielevans.email;

import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;

import java.io.IOException;
import java.util.List;

/**
 * Created by daniel on 6/9/16.
 */
public class LabelMaker {
    /**
     * Updates the specified label.
     *
     * @param labelId    ID of Label to patch.
     * @param labelPatch Label with properties to patch.
     */

    public static Label patchLabel(Inbox inbox, String labelName,
                                   String labelId, Label labelPatch) {
        Label patchedLabel = null;
        try {
            patchedLabel = inbox.getService().users().labels()
                    .patch(inbox.getUser(), labelId, labelPatch).execute();
            return patchedLabel;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add a new Label to user's inbox.
     * <p>
     * can be used to indicate the authenticated user.
     *
     * @param labelName Name of the new label.
     */

    public static Label createLabel(Inbox inbox, String labelName) {
        Label label = new Label().setName(labelName);
        try {
            label = inbox.getService().users().labels()
                    .create(inbox.getUser(), label).execute();
            return label;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update an existing label.
     * <p>
     * can be used to indicate the authenticated user.
     *
     * @param labelToUpdate     The label that will be updated
     * @param newLabelName      New name of the label.
     * @param showInMessageList Show or hide label in message.
     * @param showInLabelList   Show or hide label in label list.
     */
    public static Label updateLabel(Inbox inbox,
                                    Label labelToUpdate,
                                    String newLabelName, boolean showInMessageList,
                                    boolean showInLabelList) {
        String messageListVisibility = showInMessageList ? "show" : "hide";
        String labelListVisibility = showInLabelList ? "labelShow" : "labelHide";
        Label newLabel = new Label().setName(newLabelName)
                .setMessageListVisibility(messageListVisibility)
                .setLabelListVisibility(labelListVisibility);
        try {
            newLabel = inbox.getService().users().labels()
                    .update(inbox.getUser(), labelToUpdate.getId(), newLabel).execute();
            return newLabel;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param inbox
     * @param label
     * @return message to user saying that the label was deleted successfully
     * or that it failed
     */
    public static String deleteLabel(Inbox inbox, Label label) {
        try {
            inbox.getService().users().labels()
                    .delete(inbox.getUser(), label.getId()).execute();
            // TODO: notify user that the it was deleted successfully
            return "The label was deleted successfully";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "There was a problem deleting the message. Try again";
    }

    public static Label getLabelPayload(Inbox inbox, Label label) {
        try {
            return inbox.getService().users().labels()
                    .get(inbox.getUser(), label.getId()).execute();
        } catch (IOException e) {
            System.out.println("unable to getLabels(Inbox,List<Label>");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * List the Labels in the user's mailbox.
     */
    public static List<Label> listLabels(Inbox i) {

        ListLabelsResponse response = null;
        try {
            response = i.getService().users().labels()
                    .list(i.getUser()).execute();
            return response.getLabels();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
