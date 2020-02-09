package com.eric.uav.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.eric.uav.R;
import com.eric.uav.Settings;
import com.eric.uav.applications.ApplicationActivity;
import com.eric.uav.homepage.HomePageActivity;
import com.eric.uav.login.LoginActivity;
import com.eric.uav.map.MapActivity;
import com.eric.uav.profile.info.ProfileInfoActivity;
import com.eric.uav.settings.SettingsActivity;
import com.eric.uav.utils.Dialog;
import com.eric.uav.zxing.android.CaptureActivity;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    // 底部栏相关状态
    private RelativeLayout homepageActivity;
    private RelativeLayout mapActivity;
    private RelativeLayout applicationActivityView;
    private RelativeLayout logout;


    private TextView detailInfo;
    private RelativeLayout lookDetailLayout;
    private RelativeLayout setting;

    private RelativeLayout profileAdmin;
    private RelativeLayout profileScan;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = getSharedPreferences("register", MODE_PRIVATE);

        findViewById(R.id.homepage_activity_item).setBackgroundResource(R.drawable.home_page);
        findViewById(R.id.map_activity_item).setBackgroundResource(R.drawable.map);
        findViewById(R.id.application_activity_item).setBackgroundResource(R.drawable.other);
        findViewById(R.id.personnal_activity_item).setBackgroundResource(R.drawable.mine_select);

        ((TextView) findViewById(R.id.homepage_activity_item_tips)).setTextColor(getResources().getColor(R.color.no_select_color));
        ((TextView) findViewById(R.id.map_activity_item_tips)).setTextColor(getResources().getColor(R.color.no_select_color));
        ((TextView) findViewById(R.id.application_activity_item_tips)).setTextColor(getResources().getColor(R.color.no_select_color));
        ((TextView) findViewById(R.id.personnal_activity_item_tips)).setTextColor(getResources().getColor(R.color.select_color));

        // 底部栏按钮
        homepageActivity = findViewById(R.id.homepage_activity);
        homepageActivity.setOnClickListener(this);
        mapActivity = findViewById(R.id.map_activity);
        mapActivity.setOnClickListener(this);
        applicationActivityView = findViewById(R.id.application_activity);
        applicationActivityView.setOnClickListener(this);

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);

        View.OnClickListener onClickListener = view -> startActivity(new Intent(ProfileActivity.this, ProfileInfoActivity.class));
        detailInfo = findViewById(R.id.look_detail_profile_info);
        detailInfo.setOnClickListener(onClickListener);

        lookDetailLayout = findViewById(R.id.look_profile);
        lookDetailLayout.setOnClickListener(onClickListener);

        setting = findViewById(R.id.profile_setting);
        setting.setOnClickListener(this);

        profileAdmin = findViewById(R.id.profile_admin);
        profileAdmin.setOnClickListener(this);

        profileScan = findViewById(R.id.profile_scan);
        profileScan.setOnClickListener(this);

        setProfile();

        setTopIconClickEvent();
    }


    public void setTopIconClickEvent() {
        findViewById(R.id.scan_icon).setOnClickListener(this);
        findViewById(R.id.admin_icon).setOnClickListener(this);
        findViewById(R.id.logout_icon).setOnClickListener(this);
    }


    public void setProfile() {
        ((TextView) findViewById(R.id.user_info_nick)).setText(sharedPreferences.getString("nick", "游客"));
        ((TextView) findViewById(R.id.user_info_email)).setText(sharedPreferences.getString("email", "nill"));
        // 加载头像
        Glide.with(this).load("http://" + Settings.ServerHost + ":" +
                Settings.ServerPort + sharedPreferences.getString("avatar", "/static/assets/img/profile.jpg"))
                .into((RadiusImageView) findViewById(R.id.user_info_avatar));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homepage_activity: {
                Intent intent = new Intent(ProfileActivity.this, HomePageActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
            break;
            case R.id.map_activity: {
                Intent intent = new Intent(ProfileActivity.this, MapActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
            break;
            case R.id.application_activity: {
                Intent intent = new Intent(ProfileActivity.this, ApplicationActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
            break;
            case R.id.profile_setting: {
                startActivity(new Intent(this, SettingsActivity.class));
            }
            break;
            case R.id.logout:
            case R.id.logout_icon: {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    overridePendingTransition(0, 0);
                });
                builder.setNegativeButton("取消", (dialog, which) -> {
                });
                builder.show();
            }
            break;

            case R.id.scan_icon:
            case R.id.profile_scan: {
                // 开始二维码扫描
                startActivity(new Intent(this, CaptureActivity.class));
            }
            break;
            case R.id.admin_icon:
            case R.id.profile_admin: {
                Intent intent = new Intent();
                intent.setData(Uri.parse("http://" + Settings.ServerHost + ":" + Settings.ServerPort + "/userManage/index/"));
                intent.setAction(Intent.ACTION_VIEW);
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
