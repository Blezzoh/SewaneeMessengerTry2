package de.email.interfaces;

/**
 * Created by daniel on 8/18/16.
 * @author Daniel Evans
 * Any class that implements mail can provide the data for a
 * MessageView to be displayed
 */
public interface Mail {

    String getSubject();

    String getSnippet();

    String getDate();

    String getFromName();

    String getFromEmail();

    String getBody();

    String getId();

    String getTo();
}
