package org.schabi.ocbookmarks;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.schabi.ocbookmarks.REST.Bookmark;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by the-scrabi on 15.05.17.
 */

public class BookmarkFragment extends Fragment {

    private BookmarksRecyclerViewAdapter mAdapter;
    private SwipeRefreshLayout refreshLayout;

    public interface OnRequestReloadListener {
        void requestReload();
    }
    private OnRequestReloadListener onRequestReloadListener = null;
    public void setOnRequestReloadListener(OnRequestReloadListener listener) {
        onRequestReloadListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fagment_bookmarks, container, false);

        refreshLayout =
                (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh_bookmarks);
        RecyclerView recyclerView =
                (RecyclerView) rootView.findViewById(R.id.bookmark_recycler_view);
        mAdapter = new BookmarksRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(onRequestReloadListener != null) {
                    onRequestReloadListener.requestReload();
                }
            }
        });

        return rootView;
    }

    public void showByTag(String tag) {
        mAdapter.notifyDataSetChanged();
    }

    public void releaseTag() {
        mAdapter.notifyDataSetChanged();
    }

    public void updateData(Bookmark[] bookmarks) {
        refreshLayout.setRefreshing(false);
    }

    public void setRefreshing(boolean refresh) {
        refreshLayout.setRefreshing(refresh);
    }

    class BookmarksRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        ArrayList<String> arrayList = new ArrayList<>();
        Context context;
        LayoutInflater inflater = LayoutInflater.from(getContext());

        public BookmarksRecyclerViewAdapter(Context context) {
            this.context = context;
            arrayList.add("gurken");
            arrayList.add("git");
            arrayList.add("home");
            arrayList.add("my");
            arrayList.add("schabi");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    return new BookmarkHolder(inflater.inflate(R.layout.bookmark_list_item, parent, false));
                case 1:
                    return new FooderViewHolder(inflater.inflate(R.layout.bookmark_list_item_fooder, parent, false));
                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        }

        @Override
        public int getItemViewType(int position) {
            if(position < arrayList.size()) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getItemCount() {
            return arrayList.size() + 1;
        }

        public class BookmarkHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener, View.OnLongClickListener {

            final PopupMenu popup;

            public BookmarkHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                view.setOnLongClickListener(this);

                popup = new PopupMenu(getActivity(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.edit_bookmark_item_menu, popup.getMenu());


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
                            case R.id.share:
                                return true;
                            case R.id.edit_menu:
                                EditBookmarkDialog.getDialog(getActivity()).show();

                                return true;
                            case R.id.delete_menu:
                                return true;
                        }

                        return false;
                    }
                });
            }

            @Override
            public void onClick(View view) {

            }

            @Override
            public boolean onLongClick(View view) {
                popup.show();
                return true;
            }
        }

        class FooderViewHolder extends RecyclerView.ViewHolder {
            FooderViewHolder(View view) {
                super(view);
            }
        }
    }
}
