package com.eric.uav.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.uav.R;
import com.eric.uav.applications.ApplicationActivity;
import com.eric.uav.homepage.HomePageActivity;
import com.eric.uav.login.LoginActivity;
import com.eric.uav.map.MapActivity;
import com.eric.uav.utils.Dialog;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    // 底部栏相关状态
    private TextView homepageActivity;
    private TextView mapActivity;
    private TextView applicationActivityView;
    private RelativeLayout logout;
    private TextView logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 初始化样式
        ((TextView) findViewById(R.id.title)).setText("个人中心");
        findViewById(R.id.homepage_activity).setBackground(getResources().getDrawable(R.drawable.home_page));
        findViewById(R.id.map_activity).setBackground(getResources().getDrawable(R.drawable.map));
        findViewById(R.id.application_activity).setBackground(getResources().getDrawable(R.drawable.other));
        findViewById(R.id.personnal_activity).setBackground(getResources().getDrawable(R.drawable.mine_select));

        // 底部栏按钮
        homepageActivity = findViewById(R.id.homepage_activity);
        homepageActivity.setOnClickListener(this);
        mapActivity = findViewById(R.id.map_activity);
        mapActivity.setOnClickListener(this);
        applicationActivityView = findViewById(R.id.application_activity);
        applicationActivityView.setOnClickListener(this);

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);
        logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);
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
            case R.id.logout: {
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
                builder.setNegativeButton("取消", (dialog, which) -> {});
                builder.show();
            }
            break;
            case R.id.logout_btn: {
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
                builder.setNegativeButton("取消", (dialog, which) -> {});
                builder.show();
            }
            break;
            default:
                break;
        }
    }


    /**
     * 实现按下返回键提示用户再按一次返回桌面，而不是返回上一个页面
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
