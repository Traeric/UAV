package com.eric.uav.uav_video;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.eric.uav.R;
import com.kongqw.rockerlibrary.view.RockerView;

import java.util.Timer;
import java.util.TimerTask;

public class UavVideoActivity extends AppCompatActivity {
    private VideoView videoView;
    // 摇杆
    private RockerView rockerView;

    // 屏幕旋转监听
    /**
     * 手机屏幕旋转的监听
     */
    private OrientationEventListener orientationEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uav_video);
        // 隐藏状态栏
        LinearLayout linearLayout = findViewById(R.id.uav_activity);
        linearLayout.setSystemUiVisibility(View.INVISIBLE);  // 设置为不可见

        videoView = findViewById(R.id.videoView);
        rockerView = findViewById(R.id.rockerView);
        // 加载视频文件
//        videoView.setVideoPath("https://www.runoob.com/try/demo_source/movie.mp4");
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.video;
        videoView.setVideoURI(Uri.parse(uri));
        // 让VideoView获取焦点
        videoView.requestFocus();
        videoView.setOnPreparedListener(mediaPlayer -> {
            // 设置循环
            mediaPlayer.setLooping(true);
            // 开始播放
            mediaPlayer.start();
        });

        // 屏幕旋转监听
        startOrientationChangeListener(UavVideoActivity.this);
        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable();
        } else {
            Toast.makeText(UavVideoActivity.this, "无法开启屏幕旋转监听...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        orientationEventListener.disable();
        super.onDestroy();
    }

    /**
     * 屏幕旋转
     *
     * @param context
     */
    public void startOrientationChangeListener(Context context) {
        orientationEventListener = new OrientationEventListener(context) {
            @Override
            public void onOrientationChanged(int rotation) {
                //判断四个方向
                if (rotation == -1) {
                    // 手机平放
//                    Toast.makeText(UavVideoActivity.this, "手机平放", Toast.LENGTH_SHORT).show();
                } else if (rotation < 10 || rotation > 350) {
                    // 正竖屏
//                    Toast.makeText(UavVideoActivity.this, "正竖屏", Toast.LENGTH_SHORT).show();
                } else if (rotation < 100 && rotation > 80) {
                    // 反横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
//                    Toast.makeText(UavVideoActivity.this, "反横屏", Toast.LENGTH_SHORT).show();
                } else if (rotation < 190 && rotation > 170) {
                    // 反竖屏
//                    Toast.makeText(UavVideoActivity.this, "反竖屏", Toast.LENGTH_SHORT).show();
                } else if (rotation < 280 && rotation > 260) {
                    // 正横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
//                    Toast.makeText(UavVideoActivity.this, "正横屏", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 点击屏幕
            // 显示摇杆
            rockerView.setVisibility(View.VISIBLE);
            // 获取手指点击屏幕的位置
            float rawX = ev.getRawX();
            float rawY = ev.getRawY();
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            // 离开屏幕
            // 隐藏摇杆
            rockerView.setVisibility(View.INVISIBLE);
        }
        return super.dispatchTouchEvent(ev);
    }


}
