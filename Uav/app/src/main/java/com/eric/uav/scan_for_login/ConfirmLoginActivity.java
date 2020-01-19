package com.eric.uav.scan_for_login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.eric.uav.R;
import com.eric.uav.Settings;
import com.eric.uav.homepage.HomePageActivity;
import com.eric.uav.utils.HttpUtils;
import com.eric.uav.zxing.android.CaptureActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfirmLoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tips;
    private Button cancelBtn;
    private Button confirmLogin;

    private SharedPreferences sharedPreferences;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_login);
        // 供存储使用
        sharedPreferences = getSharedPreferences("register", MODE_PRIVATE);
        // 设置提示字
        tips = findViewById(R.id.tips_text);
        tips.setText("即将使用 " + sharedPreferences.getString("email", "none") + " 登录");

        cancelBtn = findViewById(R.id.cancel_login);
        cancelBtn.setOnClickListener(this);

        confirmLogin = findViewById(R.id.confirm_login);
        confirmLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_login: {
                Map<String, String> param = new HashMap<>();
                param.put("uuid", CaptureActivity.RESULT_INFO);

                HttpUtils httpUtils = new HttpUtils(ConfirmLoginActivity.this) {
                    @Override
                    public void callback(String result) {
                        runOnUiThread(() -> {
                            // 成功返回
                            // 跳转到首页
                            startActivity(new Intent(ConfirmLoginActivity.this, HomePageActivity.class));
                            overridePendingTransition(0, 0);
                        });
                    }
                };
                httpUtils.sendPost(Settings.routerMap.get("cancelLogin"), param);
            }
            break;
            case R.id.confirm_login: {
                Map<String, String> param = new HashMap<>();
                param.put("user_id", Objects.requireNonNull(sharedPreferences.getString("id", "")));
                param.put("uuid", CaptureActivity.RESULT_INFO);

                HttpUtils httpUtils = new HttpUtils(ConfirmLoginActivity.this) {
                    @Override
                    public void callback(String result) {
                        runOnUiThread(() -> {
                            // 成功返回
                            // 跳转到首页
                            startActivity(new Intent(ConfirmLoginActivity.this, HomePageActivity.class));
                            overridePendingTransition(0, 0);
                        });
                    }
                };
                httpUtils.sendPost(Settings.routerMap.get("confirmLogin"), param);
            }
            break;
            default:
                break;
        }
    }
}
