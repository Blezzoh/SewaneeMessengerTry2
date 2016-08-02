package com.danielevans.email;

/**
 * Created by daniel on 7/31/16.
 *
 * @author Daniel Evans
 *         Any class that implements this interface must have access
 *         to the Authenticator associated with the user's account
 *         As of now, it is best to isolate this ability to FullMessage,
 *         Inbox, and FullThread so that the user's credentials aren't accessible
 *         by client applications and the client can only make modifications to the
 *         account allowed by the associated Authenticator
 */
public interface Auth {

    /**ff
     * @return An Authenticator that gives access to modify the user's account
     * base on the GmailScopes params
     */
    Authenticator getAuth();
}
