package org.schabi.ocbookmarks;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by the-scrabi on 15.05.17.
 */

public class TagsFragment extends Fragment {

    private ArrayList<String> tagList = new ArrayList<>();
    TagsRecyclerViewAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    public interface OnTagTapedListener {
        void onTagTaped(String tag);
    }
    private OnTagTapedListener onTagTapedListener = null;
    public void setOnTagTapedListener(OnTagTapedListener listener) {
        onTagTapedListener = listener;
    }

    private TagsRecyclerViewAdapter.OnTagDeletedListener onTagDeletedListener = null;
    public void setOnTagDeletedListener(TagsRecyclerViewAdapter.OnTagDeletedListener listener) {
        onTagDeletedListener = listener;
    }

    private TagsRecyclerViewAdapter.OnTagEditedListener onTagEditedListener = null;
    public void setOnTagEditedListener(TagsRecyclerViewAdapter.OnTagEditedListener listener) {
        onTagEditedListener = listener;
    }

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
        View rootView = inflater.inflate(R.layout.fragment_tags, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.tag_recycler_view);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh_tags);

        adapter = new TagsRecyclerViewAdapter(getActivity(), false, tagList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        adapter.setOnTagTapedListener(new TagsRecyclerViewAdapter.OnTagTapedListener() {
            @Override
            public void onTagTaped(String tag) {
                onTagTapedListener.onTagTaped(tag);
            }
        });
        adapter.setOnTagDeletedListener(new TagsRecyclerViewAdapter.OnTagDeletedListener() {
            @Override
            public void onTagDeleted(String tag) {
                if(onTagDeletedListener != null) {
                    onTagDeletedListener.onTagDeleted(tag);
                }
            }
        });
        adapter.setOnTagEditedListener(new TagsRecyclerViewAdapter.OnTagEditedListener() {
            @Override
            public void onTagEdited(String oldTag, String newTag) {
                if(onTagEditedListener != null) {

                    onTagEditedListener.onTagEdited(oldTag, newTag);
                }
            }
        });

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

    public void updateData(String[] tags) {
        if(refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }

        tagList.clear();
        for(String tag : tags) {
            tagList.add(tag);
        }
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setRefreshing(boolean refresh) {
        refreshLayout.setRefreshing(refresh);
    }


}
