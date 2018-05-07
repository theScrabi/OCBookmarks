package org.schabi.ocbookmarks;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.schabi.ocbookmarks.REST.Bookmark;
import org.schabi.ocbookmarks.REST.OCBookmarksRestConnector;

public class AddBookmarkActivity extends AppCompatActivity {

    LoginData loginData = new LoginData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_bookmark_activity);
        setTitle("");

        Intent intent = getIntent();
        String title = intent.getStringExtra(Intent.EXTRA_SUBJECT);
        String url = intent.getStringExtra(Intent.EXTRA_TEXT);

        EditBookmarkDialog bookmarkDialog = new EditBookmarkDialog();
        bookmarkDialog.newBookmark(title, url);
        AlertDialog dialog = bookmarkDialog.getDialog(this, null, new EditBookmarkDialog.OnBookmarkChangedListener() {
            @Override
            public void bookmarkChanged(final Bookmark bookmark) {
                SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
                loginData.url = preferences.getString(getString(R.string.login_url), "");
                loginData.user = preferences.getString(getString(R.string.login_user), "");
                loginData.password = preferences.getString(getString(R.string.login_pwd), "");
                if(loginData.url.isEmpty()) {
                    //this means the user is not yet loged in
                    Toast.makeText(AddBookmarkActivity.this,
                            R.string.not_yet_logged_in,
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddBookmarkActivity.this, MainActivity.class);
                    startActivity(intent);
                }

                AsyncTask<Void, Void, String> updateTask = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        OCBookmarksRestConnector connector = new OCBookmarksRestConnector(
                                loginData.url,
                                loginData.user,
                                loginData.password);
                        try {
                            connector.addBookmark(bookmark);
                        } catch (Exception e) {
                            if(BuildConfig.DEBUG) e.printStackTrace();
                            return getString(R.string.could_not_add_bookmark);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        if(result != null) {
                            Toast.makeText(AddBookmarkActivity.this,
                                    result,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AddBookmarkActivity.this,
                                    R.string.bookmark_saved,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
        });
        dialog.show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                AddBookmarkActivity.this.finish();
            }
        });
    }
}
