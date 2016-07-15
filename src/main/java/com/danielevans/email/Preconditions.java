package com.danielevans.email;

/**
 * Created by evansdb0 on 7/15/16.
 *
 * @author Daniel Evans
 */
public class Preconditions {

    public static void objectNotNull(Object obj, String message) {
        if (obj != null)
            throw new NullPointerException(message);
    }

    public void checkBounds(int start, int end, int size) {
        if (size < 0)
            throw new IllegalArgumentException("negative size " + size);
        if (start < 0 || end < start)
            throw new IndexOutOfBoundsException
                    ("start < 0 or end < start, start=" + start + ", end=" + end);
        if (end > size)
            throw new IndexOutOfBoundsException
                    ("end > size, end=" + end + ", size=" + size);
    }

    public void checkElementIndex(int index, int size, String message) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(badElementIndex(index, size, message));
        }
    }

    private String badElementIndex(int index, int size, String message) {
        if (index < 0)
            return "negative index -> " + index;
        else if (size < 0)
            throw new IllegalArgumentException("negative size " + size);
        else // index >= size
            return "index >= size -> size=" + size + " index=" + index;
    }

    /**
     * @param ifTrue  condition you expect to be true i.e. obj != null
     * @param message error message if ifTrue is false
     */
    public void checkArg(boolean ifTrue, String message) {
        if (!ifTrue)
            throw new IllegalArgumentException(message);
    }

}
