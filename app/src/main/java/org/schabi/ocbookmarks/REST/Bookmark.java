package org.schabi.ocbookmarks.REST;

import java.util.Date;

/**
 * Created by the-scrabi on 14.05.17.
 */
public class Bookmark {
    private int id = -1;
    private String url = "";
    private String title = "";
    private String userId = "";
    private String description = "";
    private Date added = null;
    private Date lastModified = null;
    private int clickcount = -1;
    private boolean isPublic = false;
    private String[] tags = new String[0];

    public static Bookmark emptyInstance() {
        return new Bookmark();
    }

    private Bookmark() {

    }

    // ++++++++++++++++++++
    // +  factory setter  +
    // ++++++++++++++++++++

    public Bookmark setId(int id) {
        this.id = id;
        return this;
    }

    public Bookmark setUrl(String url) {
        this.url = url;
        return this;
    }

    public Bookmark setTitle(String title) {
        this.title = title;
        return this;
    }

    public Bookmark setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Bookmark setDescription(String description) {
        this.description = description;
        return this;
    }

    public Bookmark setAdded(Date added) {
        this.added = added;
        return this;
    }

    public Bookmark setLastModified(Date lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public Bookmark setClickcount(int clickcount) {
        this.clickcount = clickcount;
        return this;
    }

    public Bookmark setTags(String[] tags) {
        this.tags = tags;
        return this;
    }

    public Bookmark setPublic(boolean aPublic) {
        isPublic = aPublic;
        return this;
    }

    // +++++++++++++++++++++++++
    // +   getter functions    +
    // +++++++++++++++++++++++++


    public int getId() {
        return id;
    }
    public String getUrl() {
        return url;
    }
    public String getTitle() {
        return title;
    }
    public String getUserId() {
        return userId;
    }
    public String getDescription() {
        return description;
    }
    public Date getAdded() {
        return added;
    }
    public Date getLastModified() {
        return lastModified;
    }
    public int getClickcount() {
        return clickcount;
    }
    public String[] getTags() {
        return tags;
    }
    public boolean isPublic() {
        return isPublic;
    }

    @Override
    public String toString() {
        String tagsString = "[";
        for(String tag : tags) {
            tagsString += tag + ",";
        }
        tagsString += "]";
        return "id:" + Integer.toString(id) + "\n" +
                "url:" + url + "\n" +
                "title:" + title + "\n" +
                "userId:" + userId + "\n" +
                "description:" + description + "\n" +
                "added:" + added.toString() + "\n" +
                "lastModified:" + lastModified.toString() + "\n" +
                "clickount:" + clickcount + "\n" +
                "tags:" + tagsString + "\n" +
                "isPublic:" + Boolean.toString(isPublic);
    }
}
