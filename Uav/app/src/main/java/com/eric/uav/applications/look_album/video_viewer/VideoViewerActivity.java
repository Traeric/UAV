package com.eric.uav.applications.look_album.video_viewer;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.VideoView;

import com.eric.uav.R;
import com.eric.uav.applications.look_album.DataTransform;
import com.eric.uav.utils.Dialog;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import java.io.File;

public class VideoViewerActivity extends AppCompatActivity implements View.OnClickListener {
    private VideoView videoView;
    private ImageView backViewer;
    private Button morePopupWindow;

    private View popupView;
    private PopupWindow popupWindow;

    private TextView cancelBtn;
    private RadiusImageView deleteFile;

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

        morePopupWindow = findViewById(R.id.more_popup_window);
        morePopupWindow.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.more_popup_window: {
                popupView = getLayoutInflater().inflate(R.layout.video_share_popup_window, null);
                popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
                // 设置背景图片
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                // 设置点击一下出现，再点击隐藏的效果
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                // 平移动画相对于手机屏幕的底部开始，X轴不变，Y轴从1变0
                TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0,
                        Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
                animation.setInterpolator(new AccelerateInterpolator());
                animation.setDuration(200);
                // 设置popupWindow的显示位置，此处是在手机屏幕底部且水平居中的位置
                popupWindow.showAtLocation(findViewById(R.id.video_box), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                popupView.startAnimation(animation);
                // 设置阴影
                WindowManager.LayoutParams lp = VideoViewerActivity.this.getWindow().getAttributes();
                lp.alpha = 0.3f;
                VideoViewerActivity.this.getWindow().setAttributes(lp);
                popupWindow.setOnDismissListener(() -> {
                    WindowManager.LayoutParams lpDel = VideoViewerActivity.this.getWindow().getAttributes();
                    lpDel.alpha = 1f;
                    VideoViewerActivity.this.getWindow().setAttributes(lpDel);
                });

                popupWindow.showAsDropDown(morePopupWindow);

                cancelBtn = popupView.findViewById(R.id.popup_window_cancel);
                cancelBtn.setOnClickListener(this);

                deleteFile = popupView.findViewById(R.id.delete_file);
                deleteFile.setOnClickListener(this);
            }
            break;
            case R.id.popup_window_cancel: {
                popupWindow.dismiss();
            }
            break;
            case R.id.delete_file: {
                // 获取文件
                File file = DataTransform.file;
                if (file.delete()) {
                    // 删除成功
                    setResult(DataTransform.DELETE_VIDEO);  // 设置返回码
                    this.finish();
                    overridePendingTransition(0, 0);
                } else {
                    // 删除失败
                    Dialog.toastWithoutAppName(VideoViewerActivity.this, "删除失败");
                }
            }
            break;
            default:
                break;
        }
    }
}
