package com.eric.uav.applications.look_album.video_viewer;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.eric.uav.R;
import com.eric.uav.applications.look_album.DataTransform;

public class VideoViewerActivity extends AppCompatActivity {
    private VideoView videoView;
    private ImageView backViewer;
//    private ImageView fullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_viewer);

        videoView = findViewById(R.id.video_viewer_video);
        // 设置视频播放地址
        videoView.setVideoPath(DataTransform.videoSrc);
        MediaController mediaController = new MediaController(this);
        // 让VideoView与MediaControl关联
        videoView.setMediaController(mediaController);
        videoView.setOnPreparedListener(mp -> {
            mp.start();
            mp.setLooping(true);
        });


        backViewer = findViewById(R.id.video_viewer_back);
        backViewer.setOnClickListener(view -> {
            VideoViewerActivity.this.finish();
            overridePendingTransition(0, 0);
        });

//        fullScreen = findViewById(R.id.full_screen);
//        fullScreen.setOnClickListener(view -> setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            // 用户按下了返回键
            this.finish();
            overridePendingTransition(0, 0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
