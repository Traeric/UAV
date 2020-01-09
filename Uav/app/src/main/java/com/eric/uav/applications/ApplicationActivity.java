package com.eric.uav.applications;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eric.uav.R;
import com.eric.uav.applications.link_bluetooth.BlueToothActivity;
import com.eric.uav.applications.look_album.LookAlbumActivity;
import com.eric.uav.applications.send_at.SendATActivity;
import com.eric.uav.applications.uav_video.UavVideoActivity;
import com.eric.uav.applications.voice.VoiceActivity;
import com.eric.uav.homepage.HomePageActivity;
import com.eric.uav.login.LoginActivity;
import com.eric.uav.map.MapActivity;
import com.eric.uav.profile.ProfileActivity;
import com.eric.uav.utils.Dialog;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

public class ApplicationActivity extends AppCompatActivity implements View.OnClickListener {
    // 底部栏activity
    private RelativeLayout homepageActivityView;
    private RelativeLayout mapActivityView;
    private RelativeLayout profileActivityView;

    // 应用入口
    private ImageView checkUavVideoBtn;
    private ImageView snedAtBtn;
    private ImageView voiceBtn;
    private ImageView lookAlbum;
    private ImageView blueTooth;

    private TextView logoutBtn;

    private RadiusImageView bannerVideo;
    private RadiusImageView bannerAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        // 初始化样式
        ((TextView) findViewById(R.id.title)).setText("应用");
        findViewById(R.id.homepage_activity_item).setBackgroundResource(R.drawable.home_page);
        findViewById(R.id.map_activity_item).setBackgroundResource(R.drawable.map);
        findViewById(R.id.application_activity_item).setBackgroundResource(R.drawable.other_select);
        findViewById(R.id.personnal_activity_item).setBackgroundResource(R.drawable.mine);

        ((TextView) findViewById(R.id.homepage_activity_item_tips)).setTextColor(getResources().getColor(R.color.no_select_color));
        ((TextView) findViewById(R.id.map_activity_item_tips)).setTextColor(getResources().getColor(R.color.no_select_color));
        ((TextView) findViewById(R.id.application_activity_item_tips)).setTextColor(getResources().getColor(R.color.select_color));
        ((TextView) findViewById(R.id.personnal_activity_item_tips)).setTextColor(getResources().getColor(R.color.no_select_color));

        // 底部栏
        homepageActivityView = findViewById(R.id.homepage_activity);
        homepageActivityView.setOnClickListener(this);
        mapActivityView = findViewById(R.id.map_activity);
        mapActivityView.setOnClickListener(this);
        profileActivityView = findViewById(R.id.personnal_activity);
        profileActivityView.setOnClickListener(this);

        checkUavVideoBtn = findViewById(R.id.check_uav_video);
        checkUavVideoBtn.setOnClickListener(this);

        snedAtBtn = findViewById(R.id.send_at);
        snedAtBtn.setOnClickListener(this);

        voiceBtn = findViewById(R.id.yuyingshibie);
        voiceBtn.setOnClickListener(this);

        logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);

        lookAlbum = findViewById(R.id.look_Album);
        lookAlbum.setOnClickListener(this);

        bannerVideo = findViewById(R.id.banner_video);
        bannerVideo.setOnClickListener(this);

        bannerAlbum = findViewById(R.id.banner_album);
        bannerAlbum.setOnClickListener(this);

        blueTooth = findViewById(R.id.link_bluetooth);
        blueTooth.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homepage_activity: {
                Intent intent = new Intent(ApplicationActivity.this, HomePageActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
            break;
            case R.id.map_activity: {
                Intent intent = new Intent(ApplicationActivity.this, MapActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
            break;
            case R.id.personnal_activity: {
                Intent intent = new Intent(ApplicationActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
            break;
            case R.id.check_uav_video: {
                // 跳转到航拍画面的Activity
                Intent intent = new Intent(ApplicationActivity.this, UavVideoActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.send_at: {
                // 跳转到发送指令的界面
                Intent intent = new Intent(ApplicationActivity.this, SendATActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.logout_btn: {
                AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationActivity.this);
                builder.setTitle("确认退出？");
                builder.setIcon(R.drawable.profile_logout);
                builder.setMessage("是否要退出登录？");
                builder.setPositiveButton("退出", (dialog, which) -> {
                    // 供存储使用
                    SharedPreferences sharedPreferences = getSharedPreferences("register", MODE_PRIVATE);
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("logined", "false");
                    editor.apply();
                    // 跳转到登录页面
                    startActivity(new Intent(ApplicationActivity.this, LoginActivity.class));
                    overridePendingTransition(0, 0);
                });
                builder.setNegativeButton("取消", (dialog, which) -> {
                });
                builder.show();
            }
            break;
            case R.id.yuyingshibie: {
                Intent intent = new Intent(ApplicationActivity.this, VoiceActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.look_Album: {
                startActivity(new Intent(ApplicationActivity.this, LookAlbumActivity.class));
            }
            break;
            case R.id.banner_video: {
                // 跳转到航拍画面的Activity
                startActivity(new Intent(ApplicationActivity.this, UavVideoActivity.class));
            }
            break;
            case R.id.banner_album: {
                Intent intent = new Intent(ApplicationActivity.this, LookAlbumActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.link_bluetooth: {
                Intent intent = new Intent(ApplicationActivity.this, BlueToothActivity.class);
                startActivity(intent);
            }
            break;
            default:
                break;
        }
    }

    /**
     * 实现按下返回键提示用户再按一次返回桌面，而不是返回上一个页面
     *
     * @param keyCode
     * @param event
     * @return
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            // 用户按下了返回键
            if (System.currentTimeMillis() - exitTime > 2000) {
                Dialog.toastWithoutAppName(this, "再按一次退出Uav");
                exitTime = System.currentTimeMillis();
            } else {
                // 退出到桌面
                Intent backHome = new Intent(Intent.ACTION_MAIN);
                backHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                backHome.addCategory(Intent.CATEGORY_HOME);
                startActivity(backHome);
                return true;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
