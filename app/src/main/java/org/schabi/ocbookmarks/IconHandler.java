package org.schabi.ocbookmarks;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.schabi.ocbookmarks.REST.Bookmark;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by the-scrabi on 16.06.17.
 */

public class IconHandler {

    private Context context;
    public  IconHandler(Context context) {
        this.context = context;
    }


    public void loadIcon(final ImageView imageView, final Bookmark bookmark) {

        if(siteHasNoIcon(bookmark)) {
            imageView.setImageBitmap(null);
            return;
        }

        Bitmap icon = loadIcon(bookmark);
        if(icon != null) {

            imageView.setImageBitmap(icon);
            return;
        }

        AsyncTask<Void, Void, Bitmap> loadTask = new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                HttpURLConnection connection = null;
                BufferedReader in = null;
                try {

                    // get icon url
                    URL url = new URL(bookmark.getUrl());
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    in = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));

                    StringBuilder res = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        res.append(inputLine);
                    }
                    in.close();

                    Document doc = Jsoup.parse(res.toString(), bookmark.getUrl());
                    Element link = null;
                    // try to get highres firs
                    link = doc.select("link[rel*=\"apple-touch-icon\"]").first();
                    if (link == null) {
                        link = doc.select("link[rel*=\"icon\"]").first();
                    }

                    if (link != null) {

                        // get icon
                        String iconUrl = link.attr("abs:href");

                        // fix icon url for certain sites
                        // ---------------------------------
                        iconUrl = iconUrl.replace("google.com", "www.google.com");
                        // ---------------------------------

                        URL iUrl = new URL(iconUrl);
                        connection = (HttpURLConnection) iUrl.openConnection();
                        connection.setRequestMethod("GET");
                        Bitmap icon = BitmapFactory.decodeStream(connection.getInputStream());
                        return icon;
                    } else {
                        Log.d("IconHandler", "Nothing found for: " + bookmark.getUrl());
                    }

                    return null;
                } catch (Exception e) {
                    if(BuildConfig.DEBUG) e.printStackTrace();
                    return null;
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (Exception e) {
                        if(BuildConfig.DEBUG) e.printStackTrace();
                    }
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                if(result == null) {
                    setSiteHasNoIcon(bookmark);
                } else {
                    storeIcon(bookmark, result);
                }
                imageView.setImageBitmap(result);
            }
        }.execute();

    }

    private Bitmap loadIcon(Bookmark bookmark) {
        int id = bookmark.getId();
        File homeDir = context.getFilesDir();
        File iconFile = new File(homeDir.toString() + "/" + id + ".png");
        if(iconFile.exists()) {
            return BitmapFactory.decodeFile(iconFile.toString());
        } else {
            return null;
        }
    }

    private boolean siteHasNoIcon(Bookmark bookmark) {
        int id = bookmark.getId();
        File homeDir = context.getFilesDir();
        File iconFile = new File(homeDir.toString() + "/" + id + ".noicon");
        return iconFile.exists();
    }

    private void setSiteHasNoIcon(Bookmark bookmark) {
        int id = bookmark.getId();
        File homeDir = context.getFilesDir();
        File iconFile = new File(homeDir.toString() + "/" + id + ".noicon");
        try {
            iconFile.createNewFile();
        } catch (Exception e) {
            if(BuildConfig.DEBUG) e.printStackTrace();
        }
    }

    private void storeIcon(Bookmark bookmark, Bitmap icon) {
        int id = bookmark.getId();
        File homeDir = context.getFilesDir();
        File iconFile = new File(homeDir.toString() + "/" + id + ".png");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(iconFile.toString());
            icon.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            if(BuildConfig.DEBUG) e.printStackTrace();
        }
    }

    public void deleteAll() {
        File homeDir = context.getFilesDir();
        for(File file : homeDir.listFiles()) {
            if(file.toString().endsWith(".png") || file.toString().endsWith(".noicon")) {
                file.delete();
            }
        }
    }
}
