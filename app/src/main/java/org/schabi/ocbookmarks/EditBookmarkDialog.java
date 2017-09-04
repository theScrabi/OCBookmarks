package org.schabi.ocbookmarks;

import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import org.schabi.ocbookmarks.REST.Bookmark;

import java.util.ArrayList;

import static android.os.Build.VERSION.SDK_INT;

public class EditBookmarkDialog {
    ArrayList<String> tagList = new ArrayList<>();
    Bookmark bookmark;
    String title = "";
    String url = "";

    public interface OnBookmarkChangedListener {
        void bookmarkChanged(Bookmark bookmark);
    }
    private OnBookmarkChangedListener onBookmarkChangedListener;

    public void newBookmark(final String title, final String url) {
        this.title = title;
        this.url = url;
    }

    public AlertDialog getDialog(final Activity context, Bookmark b, OnBookmarkChangedListener listener) {
        onBookmarkChangedListener = listener;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.edit_bookmark_dialog, null);
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        final EditText urlInput = (EditText) view.findViewById(R.id.urlInput);
        final EditText titleInput = (EditText) view.findViewById(R.id.titleInput);
        final EditText descriptionInput = (EditText) view.findViewById(R.id.descriptionInput);
        String dialogTitle = null;

        if(b == null) {
            dialogTitle = "Add bookmark";
            bookmark = Bookmark.emptyInstance();
            bookmark.setTitle(title);
            bookmark.setUrl(url);
        } else {
            dialogTitle = "Edit bookmark";
            bookmark = b;
        }
        urlInput.setText(bookmark.getUrl());
        titleInput.setText(bookmark.getTitle());
        descriptionInput.setText(bookmark.getDescription());

        for(String tag : bookmark.getTags()) {
            tagList.add(tag);
        }

        toolbar.setTitle(dialogTitle);
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
        final TagsRecyclerViewAdapter adapter = new TagsRecyclerViewAdapter(context, true, tagList);
        adapter.setOnTagDeletedListener(new TagsRecyclerViewAdapter.OnTagDeletedListener() {
            @Override
            public void onTagDeleted(String tag) {
                tagList.remove(tag);
                adapter.notifyDataSetChanged();
            }
        });
        adapter.setOnTagEditedListener(new TagsRecyclerViewAdapter.OnTagEditedListener() {
            @Override
            public void onTagEdited(String oldTag, String newTag) {
                if(newTag.isEmpty()) {
                    tagList.remove(oldTag);
                    adapter.notifyDataSetChanged();
                }

                if (newTag != oldTag) {
                    int oldTagPos = tagList.indexOf(oldTag);
                    if (oldTagPos >= 0) {
                        tagList.set(tagList.indexOf(oldTag), newTag);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        fixTitlebarColor(toolbar, context);

        return dialog;
    }

    private void fixTitlebarColor(Toolbar toolbar, Context context) {
        int textColor = 0;
        if(SDK_INT <= 23) {
            textColor = Color.parseColor("#ffffffff");
        } else {
            textColor = context.getColor(R.color.editTitlebarTextColor);
        }
        toolbar.setTitleTextColor(textColor);
        TextView saveItem  = (TextView) toolbar.findViewById(R.id.save_menu);
        saveItem.setTextColor(textColor);

    }

}
