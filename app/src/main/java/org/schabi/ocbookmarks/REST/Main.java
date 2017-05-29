package org.schabi.ocbookmarks.REST;

/**
 * Created by the-scrabi on 14.05.17.
 */


public class Main {
    public static void main(String[] argv) {
        try {
            OCBookmarksRestConnector connector =
                    new OCBookmarksRestConnector("http://msi-7816:8080", "admin", "gurkensallat");


            Bookmark b = Bookmark.emptyInstance();
            b.setUrl("https://cloud.schabi.org:15567/owncloud");
            connector.addBookmark(b);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
