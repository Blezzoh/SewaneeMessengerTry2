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
    private static final String NULL_AUTH = "param auth is null";

    /**
     * Updates the specified label.
     *
     * @param labelId    ID of Label to patch. For example, CATEGORY_PERSONAL
     * @param labelPatch Label with properties to patch.
     */

    public static Label patchLabel(Auth auth, String labelId, Label labelPatch) {
        Label patchedLabel = null;
        try {
            patchedLabel = auth.getAuth().service.users().labels()
                    .patch(auth.getAuth().userId, labelId, labelPatch).execute();
            return patchedLabel;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add a new Label to user's inbox.
     */
    public static Label createLabel(Auth auth, String newLabelName,
                                    boolean showInMessageList, boolean showInLabelList) throws IOException {
        Preconditions.objectNotNull(auth, NULL_AUTH);
        Preconditions.objectNotNull(auth, newLabelName);

        String messageListVisibility = showInMessageList ? "show" : "hide";
        String labelListVisibility = showInLabelList ? "labelShow" : "labelHide";
        Label label = new Label().setName(newLabelName)
                .setMessageListVisibility(messageListVisibility)
                .setLabelListVisibility(labelListVisibility);
        label = auth.getAuth().service.users()
                .labels().create(auth.getAuth().userId, label).execute();
        System.out.println("The label \"" + label.getName() + "\" was created.");

        return label;
    }

    public static Label updateLabel(Auth auth,
                                    String oldLabelName,
                                    String newLabelName, boolean showInMessageList,
                                    boolean showInLabelList) {
        Label label = findLabel(auth, oldLabelName);
        if (label != null)
            return updateLabel(auth, label, newLabelName, showInMessageList, showInLabelList);
        return null;
    }

    /**
     * Update an existing label.
     * @param labelToUpdate     The label that will be updated
     * @param newLabelName      New name of the label.
     * @param showInMessageList Show or hide label in message.
     * @param showInLabelList   Show or hide label in label list.
     */
    public static Label updateLabel(Auth auth,
                                    Label labelToUpdate,
                                    String newLabelName, boolean showInMessageList,
                                    boolean showInLabelList) {
        Preconditions.objectNotNull(auth, NULL_AUTH);
        Preconditions.objectNotNull(auth, "param newLabelName is null");

        String messageListVisibility = showInMessageList ? "show" : "hide";
        String labelListVisibility = showInLabelList ? "labelShow" : "labelHide";
        Label newLabel = new Label().setName(newLabelName)
                .setMessageListVisibility(messageListVisibility)
                .setLabelListVisibility(labelListVisibility);
        try {
            newLabel = auth.getAuth().service.users().labels()
                    .update(auth.getAuth().userId, labelToUpdate.getId(), newLabel).execute();
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

    /**
     * @param auth               any class that implements Auth, used for account access/modification
     * @param messageId          the id of the message whose labels will be modified
     * @param labelToAddOrRemove the names of the labels to add or remove
     * @param addOrRemove        true if you want to add the label, false if you want to
     *                           remove the label
     * @return returns true if the message's labelList was successfully modified
     * false otherwise
     */

    public static boolean modifyMessage(Auth auth,
                                        String messageId, String labelToAddOrRemove,
                                        boolean addOrRemove) {
        Preconditions.objectNotNull(auth, NULL_AUTH);
        return modifyMessage(auth.getAuth(), messageId
                , labelToAddOrRemove, addOrRemove);
    }

    private static boolean modifyMessage(Authenticator auth,
                                         String messageId
            , String labelToRemove,
                                         boolean addOrRemove) {
        Preconditions.objectNotNull(auth, NULL_AUTH);
        List<String> labelList = new ArrayList<>(1);
        labelList.add(labelToRemove);
        Message m;
        if (addOrRemove)
            m = modifyMessage(auth, messageId, labelList, null);
        else
            m = modifyMessage(auth, messageId, null, labelList);
        return m != null;
    }

    private static boolean deleteLabel(Auth auth, Label label) {
        Preconditions.objectNotNull(label, "label is null and was not found " +
                "by the findLabel(Inbox, String) method");
        Preconditions.objectNotNull(auth, NULL_AUTH);
        try {
            auth.getAuth().service.users().labels()
                    .delete(auth.getAuth().userId, label.getId()).execute();
            // TODO: notify user that the it was deleted successfully
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static Label findLabel(Auth auth, String labelName) {
        Preconditions.objectNotNull(auth, NULL_AUTH);
        Preconditions.objectNotNull(auth, "param labelName is null");
        List<Label> labels = LabelMaker.listLabels(auth);
        if (labels != null) {
            for (int i = 0; i < labels.size(); i++)
                if (labels.get(i).getName().equals(labelName))
                    return labels.get(i);
        }
        return null;
    }

    /**
     * @param auth any class that implements Auth  (needed for account access)
     * @param labelName the name of the label to delete
     * @return message to user saying that the label was deleted successfully
     * or that it failed
     */
    public static boolean deleteLabel(Auth auth, String labelName) {
        return LabelMaker.deleteLabel(auth, findLabel(auth, labelName));
    }

    public static Label getLabelPayload(Auth auth, Label label) {
        try {
            return auth.getAuth().service.users().labels()
                    .get(auth.getAuth().userId, label.getId()).execute();
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * List the Labels in the user's mailbox.
     */
    public static List<Label> listLabels(Auth auth) {
        Preconditions.objectNotNull(auth, NULL_AUTH);
        return listLabels(auth.getAuth());
    }

    private static List<Label> listLabels(Authenticator auth) {
        ListLabelsResponse response = null;
        try {
            response = auth.service.users().labels()
                    .list(auth.userId).execute();
            return response.getLabels();
        } catch (IOException e) { e.printStackTrace(); }
        return null;
    }
}
