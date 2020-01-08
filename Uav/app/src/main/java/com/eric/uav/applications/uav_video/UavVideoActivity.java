package com.eric.uav.applications.uav_video;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.eric.uav.R;
import com.eric.uav.utils.CameraUtils;
import com.eric.uav.utils.Dialog;
import com.kongqw.rockerlibrary.view.RockerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

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
    private TextView recordScreen;
    private TextView cutScreen;

    private MediaProjectionManager mediaProjectionManager;
    private boolean stopRecordScreen = false;
    private MediaRecorder mediaRecorder = new MediaRecorder();


    private LinearLayout linearLayout;

    // 屏幕旋转监听
    /**
     * 手机屏幕旋转的监听
     */
    private OrientationEventListener orientationEventListener;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uav_video);

        // 隐藏状态栏
        linearLayout = findViewById(R.id.uav_activity);
        linearLayout.setSystemUiVisibility(View.INVISIBLE);  // 设置状态栏为不可见

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

        // 录屏
        recordScreen = findViewById(R.id.record_screen);
        recordScreen.setOnClickListener(this);
        cutScreen = findViewById(R.id.cut_screen);
        cutScreen.setOnClickListener(this);
        // 创建存储录屏文件的目录
        File file = new File("/sdcard/Uav/");
        if (!file.exists()) {
            file.mkdirs();
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

    /**
     * 屏幕点击监听
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            // 屏幕密度（0.75 / 1.0 / 1.5）
            float density = dm.density;
            // 获取屏幕宽度
            int width = dm.widthPixels;


            // 获取手指点击屏幕的位置
            float rawX = ev.getRawX();
            float rawY = ev.getRawY();
            // 判断是否点击了地图控件
            if (rawX > 0 && rawX < 200 * density && rawY > 0 && rawY < 200 * density) {
                return super.dispatchTouchEvent(ev);
            }
            // 判断是否点击了右侧控件
            if (rawX > (width - 160 * density) && rawY < 40 * density) {
                return super.dispatchTouchEvent(ev);
            }
            clicked = true;
            // 点击屏幕
            // 显示摇杆
            rockerView.setVisibility(View.VISIBLE);
            // 定义一个LayoutParams
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins((int) (rawX - 75 * density), (int) (rawY - 75 * density), 0, 0);
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

    private static final int REQUEST_CUT_CAPTURE = 1;
    private static final int REQUEST_RECORD_SCREEN = 2;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open_map: {
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics dm = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(dm);
                // 屏幕密度（0.75 / 1.0 / 1.5）
                float density = dm.density;

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.height = (int) (200 * density - mapView.getWidth());
                layoutParams.width = (int) (200 * density - mapView.getWidth());
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
            case R.id.record_screen: {
                if (stopRecordScreen) {
                    // 停止录屏
                    mediaRecorder.stop();
                    Dialog.toastWithoutAppName(UavVideoActivity.this, "录屏完成");
                    // 切换图标
                    recordScreen.setBackgroundResource(R.drawable.screen);
                    stopRecordScreen = false;
                    return;
                }
                // 动态权限申请
                if (ContextCompat.checkSelfPermission(UavVideoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_RECORD_SCREEN);
                }
                if (ContextCompat.checkSelfPermission(UavVideoActivity.this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_SCREEN);
                }

                // 实例化MediaProjectionManager
                mediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                // 利用MediaProjectionManager类实例的功能函数createScreenCaptureIntent()生成intent，为接下来的的抓取屏幕做准备
                Intent screenCaptureIntent = mediaProjectionManager.createScreenCaptureIntent();
                startActivityForResult(screenCaptureIntent, REQUEST_RECORD_SCREEN);
            }
            break;
            case R.id.cut_screen: {
                // 实例化MediaProjectionManager
                mediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                // 利用MediaProjectionManager类实例的功能函数createScreenCaptureIntent()生成intent，为接下来的的抓取屏幕做准备
                Intent screenCaptureIntent = mediaProjectionManager.createScreenCaptureIntent();
                startActivityForResult(screenCaptureIntent, REQUEST_CUT_CAPTURE);
            }
            break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        linearLayout.setSystemUiVisibility(View.INVISIBLE);  // 设置状态栏为不可见
        MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
        //WindowManager对象用于获取屏幕尺寸
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int windowHeight = displayMetrics.heightPixels;
        int windowWidth = displayMetrics.widthPixels;
        switch (requestCode) {
            // 此处是截图功能的返回
            case REQUEST_CUT_CAPTURE: {
                if (resultCode == RESULT_OK && mediaProjection != null) {
                    // 图片路径
                    String nameImage = "/sdcard/Uav/Uav无人机航拍画面截图" + System.currentTimeMillis() + ".png";
                    ImageReader imageReader = ImageReader.newInstance(windowWidth, windowHeight, PixelFormat.RGBA_8888, 2);
                    mediaProjection.createVirtualDisplay("屏幕捕获", windowWidth, windowHeight, 240,
                            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imageReader.getSurface(), null, null);
                    // 延时500毫秒在抓取
                    new Handler().postDelayed(() -> {
                        // 获取Image对象
                        Image image = imageReader.acquireLatestImage();
                        int width = image.getWidth();
                        int height = image.getHeight();
                        Image.Plane[] planes = image.getPlanes();
                        ByteBuffer buffer = planes[0].getBuffer();
                        int pixelStride = planes[0].getPixelStride();
                        int rowStride = planes[0].getRowStride();
                        int rowPadding = rowStride - pixelStride * width;
                        // 创建Bitmap对象
                        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                        bitmap.copyPixelsFromBuffer(buffer);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
                        image.close();
                        if (bitmap != null) {
                            File file = new File(nameImage);
                            if (!file.exists()) {
                                // 不存在就创建
                                try {
                                    file.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                // 保存文件
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                                fileOutputStream.flush();
                                fileOutputStream.close();
                                Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                Uri contentUri = Uri.fromFile(file);
                                media.setData(contentUri);
                                this.sendBroadcast(media);
                                Dialog.toastWithoutAppName(this, "截图成功");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 500);
                }
            }
            break;
            // 此处是录制屏幕的返回
            case REQUEST_RECORD_SCREEN: {
                if (resultCode == RESULT_OK && mediaProjection != null) {
                    // 视频保存路径
                    String videoPath = "/sdcard/Uav/Uav无人机航拍画面录屏" + System.currentTimeMillis() + ".mp4";
                    File file = new File(videoPath);
                    // mediaRecorder相关设置
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setOutputFile(file.getAbsolutePath());
                    mediaRecorder.setVideoSize(2048, 1024);
                    mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mediaRecorder.setVideoEncodingBitRate(5 * 2048 * 1024);
                    mediaRecorder.setVideoFrameRate(30);
                    try {
                        mediaRecorder.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 创建virtualDisplay
                    mediaProjection.createVirtualDisplay("screen mirror", windowWidth, windowHeight, 240,
                            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);

                    // 保存视频
                    new Handler().postDelayed(() -> {
                        // 开始保存
                        mediaRecorder.start();
                        stopRecordScreen = true;
                        // 切换图标
                        recordScreen.setBackgroundResource(R.drawable.stop);
                    }, 500);
                }
            }
            break;
            default:
                break;
        }
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
