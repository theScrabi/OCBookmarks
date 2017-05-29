package org.schabi.ocbookmarks.REST;

import java.io.IOException;

/**
 * Created by the-scrabi on 14.05.17.
 */
public class RequestException extends IOException {
    RequestException(String message, Exception e) {
        super(message, e);
    }

    RequestException(Exception e) {
        super(e);
    }

    RequestException(String message) {
        super(message);
    }
}
