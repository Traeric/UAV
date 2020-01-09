package com.eric.uav.homepage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eric.uav.R;
import com.eric.uav.applications.look_album.RecyclerViewSpacesItemDecoration;

import java.util.List;
import java.util.Map;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private Context context;
    private List list;

    public NewsAdapter(Context context, List list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new NewsViewHolder(LayoutInflater.from(this.context).inflate(R.layout.recycle_news_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder newsViewHolder, int i) {
        Map<String, Object> map = (Map<String, Object>) list.get(i);
        newsViewHolder.title.setText((String) map.get("title"));
        newsViewHolder.author.setText((String) map.get("author"));
        newsViewHolder.time.setText((String) map.get("date"));

        newsViewHolder.recyclerView.setLayoutManager(new GridLayoutManager(this.context, 3));
        // 每行距离10px
        newsViewHolder.recyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(10));
        newsViewHolder.recyclerView.setAdapter(new ImageAdapter(this.context, (String[]) map.get("images")));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView author;
        private TextView time;
        private RecyclerView recyclerView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            author = itemView.findViewById(R.id.author);
            time = itemView.findViewById(R.id.time);
            recyclerView = itemView.findViewById(R.id.image_recycle);
        }
    }
}
