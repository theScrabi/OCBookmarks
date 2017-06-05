package org.schabi.ocbookmarks;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.schabi.ocbookmarks.REST.Bookmark;

public class AddBookmarkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_bookmark_activity);
        setTitle("");

        EditBookmarkDialog bookmarkDialog = new EditBookmarkDialog();
        AlertDialog dialog = bookmarkDialog.getDialog(this, null, new EditBookmarkDialog.OnBookmarkChangedListener() {
            @Override
            public void bookmarkChanged(Bookmark bookmark) {

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
