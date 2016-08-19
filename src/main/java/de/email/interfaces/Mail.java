package de.email.interfaces;

/**
 * Created by daniel on 8/18/16.
 *
 * @author Daniel Evans
 */
public interface Mail {

    String getSubject();

    String getSnippet();

    String getDate();

    String getFromName();

    String getFromEmail();

    String getBody();
}
