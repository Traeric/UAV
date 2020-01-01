package com.eric.uav.applications.look_album;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.eric.uav.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

public class LookAlbumActivity extends AppCompatActivity implements View.OnClickListener {

    private List<File> files;
    private Map<Long, List<File>> fileMap = new ConcurrentHashMap<>(); // 每天都对对应很多个文件，使用map将每天跟每天的文件对应起来
    private List<Long> keys = new LinkedList<>();   // 所有时间节点的集合

    private boolean hasListedFlag = false;

    // 顶部栏数据
    private TextView allAlbum;
    private TextView imageAlbum;
    private TextView videoAlbum;

    private RecyclerView parentRecycle;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_album);

        // 获取Uav目录下的所有文件
        File file = new File("/sdcard/Uav");
        files = Arrays.asList(file.listFiles());
        files = new ArrayList<>(files);

        // 获取今天0点的时刻
        long current = System.currentTimeMillis();
        long startTime = current - (current + TimeZone.getDefault().getRawOffset()) % (1000 * 3600 * 24);
        // 以及今天最后时刻
        long endTime = startTime + 24 * 60 * 60 * 1000;
        // 生成数据
        this.sortFileByDate(startTime, endTime);

        // 获取顶部切换栏的项
        allAlbum = findViewById(R.id.all_album);
        imageAlbum = findViewById(R.id.image_album);
        videoAlbum = findViewById(R.id.video_album);
        imageAlbum.setOnClickListener(this);
        videoAlbum.setOnClickListener(this);
        allAlbum.setOnClickListener(this);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !hasListedFlag) {
            // 添加到页面
            parentRecycle = findViewById(R.id.parent_recycle);
            parentRecycle.setLayoutManager(new LinearLayoutManager(this));
            parentRecycle.addItemDecoration(new RecycleViewLinearItemDecoration(60));
            parentRecycle.setAdapter(new ParentAdapter(keys, fileMap, this));
            // 防止滑动卡顿
            parentRecycle.setFocusable(false);
            parentRecycle.setHasFixedSize(true);
            parentRecycle.setNestedScrollingEnabled(false);
            // 设置scroolview到最顶层
            ScrollView scrollView = findViewById(R.id.scroll_view);
            scrollView.smoothScrollTo(0, 20);
            hasListedFlag = true;   // 已经渲染了
        }
    }

    /**
     * 递归分类出每一天的文件
     *
     * @param start 一天的起始时间
     * @param end   一天的结束时间
     */
    public void sortFileByDate(long start, long end) {
        if (files.size() <= 0) {
            return;
        }
        List<File> todayList = new LinkedList<>();
        // 筛选出传入时间之间的文件
        List<File> tempList = new ArrayList<>(files);
        for (File file : tempList) {
            if (file.lastModified() >= start && file.lastModified() <= end) {
                // 是当天创建的文件
                todayList.add(file);
                // 从列表中移除掉这个文件
                files.remove(file);
            }
        }
        // 添加到Map
        fileMap.put(start, todayList);
        keys.add(start);
        // 递归调用
        this.sortFileByDate(start - (24 * 60 * 60 * 1000), start);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_album: {
                imageAlbum.setTextColor(getResources().getColor(R.color.album_tips));
                allAlbum.setTextColor(getResources().getColor(R.color.default_text_color));
                videoAlbum.setTextColor(getResources().getColor(R.color.default_text_color));
                // 只展示图片
                parentRecycle.setAdapter(new ParentAdapter(keys, fileMap, this, "image"));
            }
            break;
            case R.id.video_album: {
                imageAlbum.setTextColor(getResources().getColor(R.color.default_text_color));
                allAlbum.setTextColor(getResources().getColor(R.color.default_text_color));
                videoAlbum.setTextColor(getResources().getColor(R.color.album_tips));
                // 只展示视频
                parentRecycle.setAdapter(new ParentAdapter(keys, fileMap, this, "video"));
            }
            break;
            case R.id.all_album: {
                imageAlbum.setTextColor(getResources().getColor(R.color.default_text_color));
                allAlbum.setTextColor(getResources().getColor(R.color.album_tips));
                videoAlbum.setTextColor(getResources().getColor(R.color.default_text_color));
                // 都展示
                parentRecycle.setAdapter(new ParentAdapter(keys, fileMap, this));
            }
            break;
            default:
                break;
        }
    }
}
