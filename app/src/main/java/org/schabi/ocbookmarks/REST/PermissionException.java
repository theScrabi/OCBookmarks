package org.schabi.ocbookmarks.REST;

import java.io.IOException;

/**
 * Created by the-scrabi on 14.05.17.
 */
public class PermissionException extends RequestException {
    PermissionException(Exception e) {
        super(e);
    }
}
