package com.danielevans.email;

import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ModifyMessageRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 6/9/16.
 * @author Daniel Evans
 */
public class LabelMaker {
    /**
     * Updates the specified label.
     *
     * @param labelId    ID of Label to patch. For example, CATEGORY_PERSONAL
     * @param labelPatch Label with properties to patch.
     */

    public static Label patchLabel(Inbox inbox, String labelId, Label labelPatch) {
        Label patchedLabel = null;
        try {
            patchedLabel = inbox.getAuth().service.users().labels()
                    .patch(inbox.getAuth().userId, labelId, labelPatch).execute();
            return patchedLabel;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add a new Label to user's inbox.
     */
    public static Label createLabel(Inbox inbox,String newLabelName,
    boolean showInMessageList, boolean showInLabelList) throws IOException {
        String messageListVisibility = showInMessageList ? "show" : "hide";
        String labelListVisibility = showInLabelList ? "labelShow" : "labelHide";
        Label label = new Label().setName(newLabelName)
                .setMessageListVisibility(messageListVisibility)
                .setLabelListVisibility(labelListVisibility);
        label = inbox.getAuth().service.users()
                .labels().create(inbox.getUser(), label).execute();

        System.out.println("Label id: " + label.getId());
        System.out.println(label.toPrettyString());

        return label;
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
            newLabel = inbox.getAuth().service.users().labels()
                    .update(inbox.getAuth().userId, labelToUpdate.getId(), newLabel).execute();
            return newLabel;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Modify the labels a message is associated with.
     * @param messageId      ID of Message to Modify.
     * @param labelsToAdd    List of label ids to add.
     * @param labelsToRemove List of label ids to remove.
     */
    public static Message modifyMessage(Authenticator auth, String messageId
            , List<String> labelsToAdd, List<String> labelsToRemove) {
        if (labelsToAdd == null && labelsToRemove == null)
            throw new IllegalArgumentException
                    ("labelsToAdd and labelsToRemove cannot both be null");
        ModifyMessageRequest mods = null;
        if (labelsToRemove == null)
            mods = new ModifyMessageRequest()
                    .setAddLabelIds(labelsToAdd);
        else if (labelsToAdd == null) {
            mods = new ModifyMessageRequest()
                    .setRemoveLabelIds(labelsToRemove);
        } else {
            mods = new ModifyMessageRequest().setAddLabelIds(labelsToAdd)
                    .setRemoveLabelIds(labelsToRemove);
        }
        Message message = null;
        try {
            message = auth.service.users().messages()
                    .modify(auth.userId, messageId, mods).execute();
            return message;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean modifyMessage(Inbox inbox,
                                        String messageId, String labelToAdd) {
        return modifyMessage(inbox.getAuth(), messageId, labelToAdd);
    }

    public static boolean modifyMessage(Authenticator auth,
                                        String messageId, String labelToAdd) {
        List<String> labelList = new ArrayList<>(1);
        labelList.add(labelToAdd);
        Message m = modifyMessage(auth, messageId, labelList, null);
        return m != null;
    }


    /**
     * @param inbox
     * @param label
     * @return message to user saying that the label was deleted successfully
     * or that it failed
     */
    public static String deleteLabel(Inbox inbox, Label label) {
        try {
            inbox.getAuth().service.users().labels()
                    .delete(inbox.getAuth().userId, label.getId()).execute();
            // TODO: notify user that the it was deleted successfully
            return "The label \"" + label.getName() + "\" was deleted successfully";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "There was a problem deleting the message. Try again";
    }

    public static String deleteLabel(Inbox inbox, String labelName) {
        List<Label> labels = LabelMaker.listLabels(inbox);
        for (int i = 0; i < labels.size(); i++) {
            if (labels.get(i).getName().equals(labelName)) {
                return LabelMaker.deleteLabel(inbox, labels.get(i));
            }
        }
        return "The label specified \"" + labelName + "\" was not found in your inbox";
    }

    public static Label getLabelPayload(Inbox inbox, Label label) {
        try {
            return inbox.getAuth().service.users().labels()
                    .get(inbox.getAuth().userId, label.getId()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * List the Labels in the user's mailbox.
     */
    public static List<Label> listLabels(Inbox inbox) {
        return listLabels(inbox.getAuth());
    }

    public static List<Label> listLabels(Authenticator auth) {

        ListLabelsResponse response = null;
        try {
            response = auth.service.users().labels()
                    .list(auth.userId).execute();
            return response.getLabels();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
