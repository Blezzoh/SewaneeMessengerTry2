package com.danielevans.email;

import java.io.IOException;

/**
 * Created by evansdb0 on 7/15/16.
 */
public class ThreadParser {

    /**
     * @param thread       thread to take the message from
     * @param whichMessage number of the message in the thread, 0 = first message
     *                     1 = second, etc
     * @return the body of the specified message in the thread, or the body
     * of the last message if whichMessage greater than thread.getMessages().size()
     * @throws IOException
     */
    /*public static String getBodyOfMessageInThread(Inbox inbox,
                                                  Thread thread,
                                                  int whichMessage)
            throws IOException {

        if (thread == null) throw new NullPointerException("thread is null");


        FullThread t = new FullThread(inbox, thread);

        // if user requests a message that doesn't exist in thread (ex: only 2 messages
        // in the thread, user requests the third), then return the last message
        if (t.getMessages().size() <= whichMessage) {
            // return the last message in the thread
            return MessageParser.getMessageBody(t.getMessages().get(t.getMessages().size() - 1));
        }
        // return the message queried for
        return MessageParser.getMessageBody(t.getMessages().get(whichMessage));
    }*/

}
