package org.schabi.ocbookmarks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by the-scrabi on 15.05.17.
 */

public class TagsFragment extends Fragment {


    public interface OnTagTapedListener {
        void onTagTaped(String tag);
    }

    private OnTagTapedListener onTagTapedListener = null;
    public void setOnTagTapedListener(OnTagTapedListener listener) {
        onTagTapedListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tags, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.tag_recycler_view);
        recyclerView.setAdapter(new TagsRecyclerViewAdapter(getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        return rootView;
    }

    class TagsRecyclerViewAdapter extends RecyclerView.Adapter<TagsRecyclerViewAdapter.ViewHolder> {
        ArrayList<String> arrayList = new ArrayList<>();
        Context context;
        LayoutInflater inflater = LayoutInflater.from(getContext());

        public TagsRecyclerViewAdapter(Context context) {
            this.context = context;
            arrayList.add("gurken");
            arrayList.add("git");
            arrayList.add("home");
            arrayList.add("my");
            arrayList.add("schabi");
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(inflater.inflate(R.layout.tag_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setTagName(arrayList.get(position));
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener, View.OnLongClickListener{
            private final TextView textView;
            private final PopupMenu popup;
            private final CardView cardView;
            private String tagName;

            public ViewHolder(View view) {
                super(view);

                textView = (TextView) view.findViewById(R.id.tag_text);
                cardView = (CardView) view.findViewById(R.id.card_view);

                cardView.setOnClickListener(this);
                cardView.setOnLongClickListener(this);

                popup = new PopupMenu(getActivity(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.edit_tag_item_menu, popup.getMenu());

                // try setting force show icons via reflections (android is a peace of shit)
                Object menuHelper;
                Class[] argTypes;
                try {
                    Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                    fMenuHelper.setAccessible(true);
                    menuHelper = fMenuHelper.get(popup);
                    argTypes = new Class[]{boolean.class};
                    menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.edit_menu:
                                showEditDialog();
                                return true;
                            case R.id.delete_menu:
                                return true;
                        }

                        return false;
                    }
                });
            }

            public void setTagName(String tag) {
                tagName = tag;
                textView.setText(tagName);
            }

            @Override
            public void onClick(View view) {
                if(onTagTapedListener != null) {
                    onTagTapedListener.onTagTaped(tagName);
                }
            }

            @Override
            public boolean onLongClick(View view) {
                popup.show();
                return true;
            }

            private void showEditDialog() {
                final EditText editText = new EditText(getActivity());
                editText.setText(tagName);
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.edit_tag)
                        .setView(editText)
                        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setTagName(editText.getText().toString());
                                //todo: update owncloud edittext
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }
    }
}
