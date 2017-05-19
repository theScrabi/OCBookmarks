package org.schabi.ocbookmarks;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by the-scrabi on 15.05.17.
 */

public class BookmarkFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fagment_bookmarks, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.bookmark_recycler_view);
        recyclerView.setAdapter(new BookmarksRecyclerViewAdapter(getActivity()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    class BookmarksRecyclerViewAdapter extends RecyclerView.Adapter<BookmarksRecyclerViewAdapter.ViewHolder> {
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
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(inflater.inflate(R.layout.bookmark_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(BookmarksRecyclerViewAdapter.ViewHolder holder, int position) {
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ViewHolder(View view) {
                super(view);
            }

            @Override
            public void onClick(View view) {
                //handle operations
            }
        }
    }
}
