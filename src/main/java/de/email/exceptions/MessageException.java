package de.email.exceptions;

import javax.mail.MessagingException;

/**
 * Created by daniel on 10/2/16.
 *
 * @author Daniel Evans
 */
public class MessageException extends MessagingException {

    public MessageException() {
        super();
        initCause(null);
    }
}
