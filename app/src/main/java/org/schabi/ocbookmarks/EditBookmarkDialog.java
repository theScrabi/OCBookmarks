package org.schabi.ocbookmarks;


import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.app.Activity;
import android.widget.EditText;
import android.widget.Toast;

import org.schabi.ocbookmarks.REST.Bookmark;

import java.util.ArrayList;

public class EditBookmarkDialog {
    ArrayList<String> tagList = new ArrayList<>();
    Bookmark bookmark;

    public interface OnBookmarkChangedListener {
        void bookmarkChanged(Bookmark bookmark);
    }
    private OnBookmarkChangedListener onBookmarkChangedListener;

    public AlertDialog getDialog(final Activity context, Bookmark b, OnBookmarkChangedListener listener) {
        onBookmarkChangedListener = listener;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.edit_bookmark_dialog, null);
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        final EditText urlInput = (EditText) view.findViewById(R.id.urlInput);
        final EditText titleInput = (EditText) view.findViewById(R.id.titleInput);
        final EditText descriptionInput = (EditText) view.findViewById(R.id.descriptionInput);
        String title = null;

        if(b == null) {
            title = "Add bookmark";
            bookmark = Bookmark.emptyInstance();
        } else {
            title = "Edit bookmark";
            bookmark = b;
        }
        urlInput.setText(bookmark.getUrl());
        titleInput.setText(bookmark.getTitle());
        descriptionInput.setText(bookmark.getDescription());

        for(String tag : bookmark.getTags()) {
            tagList.add(tag);
        }

        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.inflateMenu(R.menu.edit_bookmark_menu);

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setCancelable(true)
                .setView(view)
                .create();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.save_menu) {
                    bookmark.setUrl(urlInput.getText().toString());
                    bookmark.setTitle(titleInput.getText().toString());
                    bookmark.setDescription(descriptionInput.getText().toString());

                    if(bookmark.getUrl().isEmpty()) {
                        Toast.makeText(context, R.string.no_url_entered, Toast.LENGTH_SHORT).show();
                    } else {
                        String[] tags = new String[tagList.size()];
                        for (int i = 0; i < tags.length; i++) {
                            tags[i] = tagList.get(i);
                        }
                        bookmark.setTags(tags);
                        if (onBookmarkChangedListener != null) {
                            onBookmarkChangedListener.bookmarkChanged(bookmark);
                        }
                        dialog.dismiss();
                    }
                    return true;
                }
                return false;
            }
        });

        // setup recycler view
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.tag_recycler_view);
        TagsRecyclerViewAdapter adapter = new TagsRecyclerViewAdapter(context, true, tagList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        return dialog;
    }

}
