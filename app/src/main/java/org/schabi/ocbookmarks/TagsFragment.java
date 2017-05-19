package org.schabi.ocbookmarks;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by the-scrabi on 15.05.17.
 */

public class TagsFragment extends Fragment {
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
            holder.textView.setText(arrayList.get(position));
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView textView;

            public ViewHolder(View view) {
                super(view);
                textView = (TextView) view.findViewById(R.id.tag_text);
            }

            @Override
            public void onClick(View view) {
                //handle operations
            }
        }
    }
}
