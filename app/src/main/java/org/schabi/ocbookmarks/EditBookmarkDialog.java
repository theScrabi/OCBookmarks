package org.schabi.ocbookmarks;


import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.app.Activity;

public class EditBookmarkDialog {
    public static AlertDialog getDialog(Activity context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.edit_bookmark_dialog, null);
        final Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        final

        String title = "Edit bookmark";
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.inflateMenu(R.menu.edit_bookmark_menu);

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setCancelable(true)
                .setView(v)
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

                    //todo: save here

                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });

        setupRecyclerView(v, context);

        return dialog;
    }

    private static void setupRecyclerView(View v, Activity context) {
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.tag_recycler_view);
        TagsRecyclerViewAdapter adapter = new TagsRecyclerViewAdapter(context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
    }
}
