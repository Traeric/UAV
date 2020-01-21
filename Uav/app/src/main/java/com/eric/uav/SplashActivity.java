package com.eric.uav;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.eric.uav.homepage.HomePageActivity;
import com.eric.uav.login.LoginActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // 停留一秒进入主界面
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // 检查是否登录
                // 供存储使用
                SharedPreferences sharedPreferences = getSharedPreferences("register", MODE_PRIVATE);
                // 检查是否登录
                String loginStatus = sharedPreferences.getString("logined", "false");
                Intent intent = null;
                if ("true".equals(loginStatus)) {
                    // 已经登录了，直接跳转首页
                    intent = new Intent(SplashActivity.this, HomePageActivity.class);
                    // 存储作为公共变量
                    UvaApplication.id = sharedPreferences.getString("id", "0");
                } else {
                    // 未登录，跳转到登录界面
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        }, 1000);
    }
}
