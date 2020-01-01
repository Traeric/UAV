package com.eric.uav.applications.look_album;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eric.uav.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ListViewHolder> {
    private List<Long> keys;
    private Map<Long, List<ConvertFile>> listMap;
    private Context context;
    private String mode;

    public ParentAdapter(List<Long> keys, Map<Long, List<ConvertFile>> listMap, Context context) {
        this.keys = keys;
        this.listMap = listMap;
        this.context = context;
    }

    public ParentAdapter(List<Long> keys, Map<Long, List<ConvertFile>> listMap, Context context, String mode) {
        this.keys = keys;
        this.listMap = listMap;
        this.context = context;
        this.mode = mode;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ListViewHolder(LayoutInflater.from(this.context).inflate(R.layout.parent_recycle_layout, viewGroup, false));
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder listViewHolder, int i) {
        Long key = keys.get(i);
        if (Objects.requireNonNull(listMap.get(key)).size() <= 0) {
            return;
        }
        listViewHolder.dayText.setText(new SimpleDateFormat("yyyy年MM月dd日").format(key));
        // 设置recycle
        listViewHolder.dayRecycle.setLayoutManager(new GridLayoutManager(this.context, 4));
        // 每行距离10px
        listViewHolder.dayRecycle.addItemDecoration(new RecyclerViewSpacesItemDecoration(10));

        // 筛选数据
        List<ConvertFile> tempList = new ArrayList<>();
        if ("image".equals(mode)) {
            // 只展示图片
            for (ConvertFile file : Objects.requireNonNull(listMap.get(key))) {
                if (file.getFile().getAbsolutePath().endsWith(".png")) {
                    tempList.add(file);
                }
            }
        } else if ("video".equals(mode)) {
            // 只展示视频
            for (ConvertFile file : Objects.requireNonNull(listMap.get(key))) {
                if (file.getFile().getAbsolutePath().endsWith(".mp4")) {
                    tempList.add(file);
                }
            }
        } else {
            // 都展示
            tempList = listMap.get(key);
        }

        listViewHolder.dayRecycle.setAdapter(new AlbumAdapter(tempList, this.context));
        // 防止卡顿
        listViewHolder.dayRecycle.setFocusable(false);
        listViewHolder.dayRecycle.setHasFixedSize(true);
        listViewHolder.dayRecycle.setNestedScrollingEnabled(false);
    }

    @Override
    public int getItemCount() {
        int count = 1;
        for (Long l : keys) {
            if (Objects.requireNonNull(listMap.get(l)).size() > 0) {
                count++;
            }
        }
        return count;
    }

    class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView dayText;
        private RecyclerView dayRecycle;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.day_text);
            dayRecycle = itemView.findViewById(R.id.day_recycle);
        }
    }
}
