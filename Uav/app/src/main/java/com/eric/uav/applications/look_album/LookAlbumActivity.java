package com.eric.uav.applications.look_album;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.eric.uav.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

public class LookAlbumActivity extends AppCompatActivity {

    private List<File> files;
    private Map<Long, List<File>> fileMap = new ConcurrentHashMap<>(); // 每天都对对应很多个文件，使用map将每天跟每天的文件对应起来
    private List<Long> keys = new LinkedList<>();   // 所有时间节点的集合

    private boolean hasListedFlag = false;

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
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !hasListedFlag) {
            // 添加到页面
            LinearLayout linearLayout1 = findViewById(R.id.image_body);
            for (long item : keys) {
                if (Objects.requireNonNull(fileMap.get(item)).size() <= 0) {
                    continue;
                }
                // 生成一天布局
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                // 添加到页面中
                linearLayout1.addView(linearLayout);
                // 生成时间tips
                TextView textView = new TextView(this);
                textView.setText(new SimpleDateFormat("yyyy年MM月dd日").format(item));
                textView.setPadding(10, 50, 0, 20);
                linearLayout.addView(textView);
                // 生成图片
                LinearLayout linearLayout2 = new LinearLayout(this);
                linearLayout2.setOrientation(LinearLayout.VERTICAL);
                // 添加图片
                addItem(Objects.requireNonNull(fileMap.get(item)), linearLayout2);
                linearLayout.addView(linearLayout2);
            }
            // 设置scroolview到最顶层
            ScrollView scrollView = findViewById(R.id.scroll_view);
            scrollView.smoothScrollTo(0, 20);
            // 禁用滑动事件
            linearLayout1.setNestedScrollingEnabled(false);
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

    /**
     * 添加每一项内容
     *
     * @param list
     * @param linearLayout
     */
    public void addItem(List<File> list, LinearLayout linearLayout) {
        int i = 0;
        LinearLayout linearLayoutItem = new LinearLayout(this);
        linearLayoutItem.setWeightSum(4f);
        for (File file : list) {
            if (i % 4 == 0) {
                linearLayout.addView(linearLayoutItem);
                linearLayoutItem = new LinearLayout(this);
                linearLayoutItem.setWeightSum(4f);
            }
            i++;
            if (file.getAbsoluteFile().toString().endsWith(".mp4")) {
                // 如果显示的是视频
                RelativeLayout relativeLayout = new RelativeLayout(this);
                VideoView videoView = new VideoView(this);
                videoView.setVideoPath(file.getAbsolutePath());
                videoView.setBackgroundResource(R.drawable.video_scale);
                // 设置videoView样式
                RelativeLayout.LayoutParams relativeLayoutLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                relativeLayoutLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                relativeLayoutLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                relativeLayoutLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                relativeLayoutLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                videoView.setLayoutParams(relativeLayoutLayoutParams);
                relativeLayout.addView(videoView);

                // 设置relativeLayout样式
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(1, 1, 1, 1);
                layoutParams.weight = 1;
                layoutParams.height = 200;
                layoutParams.width = 200;
                relativeLayout.setLayoutParams(layoutParams);
                linearLayoutItem.addView(relativeLayout);
            } else {
                // 如果显示的是图片
                ImageView imageView = new ImageView(this);
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                // 设置样式
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(1, 1, 1, 1);
                layoutParams.weight = 1;
                layoutParams.height = 200;
                layoutParams.width = 200;
                imageView.setLayoutParams(layoutParams);
                linearLayoutItem.addView(imageView);
            }
        }
        // 最后一列还没有填充满
        while (true) {
            if (i % 4 == 0) {
                linearLayout.addView(linearLayoutItem);
                break;
            }
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            layoutParams.height = 200;
            layoutParams.width = 200;
            imageView.setLayoutParams(layoutParams);
            i++;
            linearLayoutItem.addView(imageView);
        }
    }
}
