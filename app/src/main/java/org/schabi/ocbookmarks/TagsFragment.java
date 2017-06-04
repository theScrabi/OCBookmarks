package org.schabi.ocbookmarks;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.schabi.ocbookmarks.REST.Bookmark;

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
        TagsRecyclerViewAdapter adapter = new TagsRecyclerViewAdapter(getActivity(), false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        adapter.setOnTagTapedListener(new OnTagTapedListener() {
            @Override
            public void onTagTaped(String tag) {
                onTagTapedListener.onTagTaped(tag);
            }
        });

        return rootView;
    }

    public void updateData(String[] tags) {

    }

}
