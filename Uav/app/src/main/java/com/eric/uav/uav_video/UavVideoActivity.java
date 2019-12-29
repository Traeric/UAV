package com.eric.uav.uav_video;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.eric.uav.R;
import com.eric.uav.utils.CameraUtils;
import com.kongqw.rockerlibrary.view.RockerView;

public class UavVideoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextureView videoView;
    // 摇杆
    private RockerView rockerView;

    // 监听是否按住了屏幕
    private static boolean clicked = false;

    // 地图
    private MapView mapView;
    private AMap aMap;

    // 右上角控制键
    private TextView openMap;
    private TextView quiteVideo;

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

        openMap = findViewById(R.id.open_map);
        openMap.setOnClickListener(this);
        quiteVideo = findViewById(R.id.quit_video);
        quiteVideo.setOnClickListener(this);

        rockerView = findViewById(R.id.rockerView);
        // 相机相关操作
        videoView = findViewById(R.id.textureView);
        requestPermission();   // 获取相机权限
        CameraUtils.getInstance(this, 0).openCamera(videoView);

        // 屏幕旋转监听
        startOrientationChangeListener(UavVideoActivity.this);
        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable();
        } else {
            Toast.makeText(UavVideoActivity.this, "无法开启屏幕旋转监听...", Toast.LENGTH_SHORT).show();
        }

        // 监听摇杆的方向
        listenShakeDirection();

        // 初始化地图
        mapView = findViewById(R.id.video_map);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
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

    /**
     * 屏幕点击监听
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获取手指点击屏幕的位置
            float rawX = ev.getRawX();
            float rawY = ev.getRawY();
            // 判断是否点击了地图控件
            if (rawX > 0 && rawX < 450 && rawY > 0 && rawY < 450) {
                return super.dispatchTouchEvent(ev);
            }
            // 判断是否点击了右侧控件
            if (rawX > 1900 && rawY < 140) {
                return super.dispatchTouchEvent(ev);
            }
            clicked = true;
            // 点击屏幕
            // 显示摇杆
            rockerView.setVisibility(View.VISIBLE);
            // 定义一个LayoutParams
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins((int) rawX - 275, (int) rawY - 210, 0, 0);
            rockerView.setLayoutParams(layoutParams);
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            clicked = false;
            // 离开屏幕
            // 隐藏摇杆
            rockerView.setVisibility(View.INVISIBLE);
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 监听摇杆角度
     */
    public void listenShakeDirection() {
        // 设置回调模式
        rockerView.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
        rockerView.setOnAngleChangeListener(new RockerView.OnAngleChangeListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void angle(double v) {
                if (clicked) {
                    Toast.makeText(UavVideoActivity.this, "摇杆方向：" + v, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFinish() {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open_map: {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.height = 450 - mapView.getWidth();
                layoutParams.width = 450 - mapView.getWidth();
                mapView.setLayoutParams(layoutParams);

                ValueAnimator vaMap;
                // 初始化
                vaMap = ValueAnimator.ofInt(mapView.getWidth(), 450 - mapView.getWidth());
                vaMap.addUpdateListener(valueAnimator -> {
                    // 获取当前的height值
                    int length = (int) valueAnimator.getAnimatedValue();
                    // 动态更新控件的宽高
                    mapView.getLayoutParams().height = length;
                    mapView.getLayoutParams().width = length;
                    mapView.requestLayout();
                });
                // 设置动画事件
                vaMap.setDuration(1000);
                // 开始动画
                vaMap.start();
            }
            break;
            case R.id.quit_video: {
                this.finish();
                overridePendingTransition(0, 0);
            }
            break;
            default:
                break;
        }
    }

    public TextureView getVideoView() {
        return videoView;
    }

    /**
     * 请求相机权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Camera permission are required for this demo", Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        }
    }
}
