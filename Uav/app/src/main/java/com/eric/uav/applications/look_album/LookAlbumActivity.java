package com.eric.uav.applications.look_album;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.eric.uav.R;
import com.eric.uav.utils.Dialog;
import com.eric.uav.utils.VideoUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

public class LookAlbumActivity extends AppCompatActivity implements View.OnClickListener {

    private List<ConvertFile> files = PublicData.files;
    private Map<Long, List<ConvertFile>> fileMap = PublicData.fileMap; // 每天都对对应很多个文件，使用map将每天跟每天的文件对应起来
    private List<Long> keys = PublicData.keys;   // 所有时间节点的集合
    private List<ConvertFile> recordList = new ArrayList<>();   // 用于递归时删除的列表
    private Map<Long, List<ConvertFile>> exchangeMap = PublicData.fileMap;

    private boolean hasListedFlag = false;

    // 顶部栏数据
    private TextView allAlbum;
    private TextView imageAlbum;
    private TextView videoAlbum;

    private ImageView loadIcon;
    private LinearLayout loadParent;

    private RecyclerView parentRecycle;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_album);

        // 获取顶部切换栏的项
        allAlbum = findViewById(R.id.all_album);
        imageAlbum = findViewById(R.id.image_album);
        videoAlbum = findViewById(R.id.video_album);
        imageAlbum.setOnClickListener(this);
        videoAlbum.setOnClickListener(this);
        allAlbum.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"SimpleDateFormat", "UseSparseArrays"})
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !hasListedFlag) {
            new Thread(() -> {
                File file = new File("/sdcard/Uav");
                if (file.listFiles().length != files.size()) {
                    runOnUiThread(() -> {
                        // 加载提示图片
                        loadParent = findViewById(R.id.load_parent);
                        loadIcon = findViewById(R.id.prev_load);
                        loadParent.setVisibility(View.VISIBLE);
                        Glide.with(this).load(R.drawable.load).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .into(loadIcon);
                    });
                    // 清除数据
                    exchangeMap = new HashMap<>(fileMap);
                    System.out.println(exchangeMap);
                    files.clear();
                    keys.clear();
                    fileMap.clear();
                    // 获取Uav目录下的所有文件
                    for (File file1 : file.listFiles()) {
                        files.add(new ConvertFile(file1));
                    }

                    // 获取今天0点的时刻
                    long current = System.currentTimeMillis();
                    long startTime = current - (current + TimeZone.getDefault().getRawOffset()) % (1000 * 3600 * 24);
                    // 以及今天最后时刻
                    long endTime = startTime + 24 * 60 * 60 * 1000;
                    // 生成数据
                    recordList = new ArrayList<>(files);
                    this.sortFileByDate(startTime, endTime);
                }
                LookAlbumActivity.this.runOnUiThread(() -> {
                    // 删除加载图标
                    if (loadParent != null) {
                        loadParent.setVisibility(View.GONE);
                    }
                    // 添加到页面
                    parentRecycle = findViewById(R.id.parent_recycle);
                    parentRecycle.setLayoutManager(new LinearLayoutManager(this));
                    parentRecycle.addItemDecoration(new RecycleViewLinearItemDecoration(60));
                    parentRecycle.setAdapter(new ParentAdapter(keys, fileMap, this));
                    hasListedFlag = true;   // 已经渲染了
                });
            }).start();
        }
    }

    /**
     * 递归分类出每一天的文件
     *
     * @param start 一天的起始时间
     * @param end   一天的结束时间
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortFileByDate(long start, long end) {
        if (recordList.size() <= 0) {
            return;
        }
        List<ConvertFile> todayList = new LinkedList<>();
        // 筛选出传入时间之前的文件
        List<ConvertFile> tempList = new ArrayList<>(recordList);
        for (ConvertFile file : tempList) {
            if (file.getFile().lastModified() >= start && file.getFile().lastModified() <= end) {
                // 查看以前的map中是否含有该图片的资料
                List<ConvertFile> currentList = exchangeMap.get(start);
                if (currentList != null &&
                        currentList.stream().anyMatch(f -> f.getFile().getName().equals(file.getFile().getName()))) {
                    // 如果有，不用再转换
                    todayList.add(currentList.stream()
                            .filter(f -> f.getFile().getName().equals(file.getFile().getName())).findFirst().get());
                    // 从列表中移除掉这个文件
                    recordList.remove(file);
                    continue;
                }
                // 是当天创建的文件
                todayList.add(file);
                // 如果是图片，进行转换
                if (file.getFile().getAbsolutePath().endsWith(".png")) {
                    file.setBitmap(BitmapFactory.decodeFile(file.getFile().getAbsolutePath()));
                }
                // 如果是视频
                if (file.getFile().getAbsolutePath().endsWith(".mp4")) {
                    file.setBitmap(VideoUtils.getInstance().getVideoThumbnail(file.getFile().getAbsolutePath(),
                            720, 1280, MediaStore.Images.Thumbnails.MINI_KIND));
                }
                // 从列表中移除掉这个文件
                recordList.remove(file);
            }
        }
        // 今天有图片才存
        if (todayList.size() > 0) {
            // 添加到Map
            fileMap.put(start, todayList);
            keys.add(start);
        }
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

    /**
     * 接收Activity的返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == DataTransform.DELETE_IMAGE || resultCode == DataTransform.DELETE_VIDEO) {
            // 刷新activity
            hasListedFlag = false;
            Dialog.toastWithoutAppName(this, "删除成功");
        }
    }
}
