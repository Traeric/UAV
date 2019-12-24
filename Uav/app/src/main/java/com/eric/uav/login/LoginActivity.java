package com.eric.uav.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.uav.R;
import com.eric.uav.Settings;
import com.eric.uav.dialog.LoadDialog;
import com.eric.uav.homepage.HomePageActivity;
import com.eric.uav.register.RegisterActivity;
import com.eric.uav.utils.Dialog;
import com.eric.uav.utils.HttpUtils;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private TextView registerBtn;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉顶部标题
        // Objects.requireNonNull(getSupportActionBar()).hide();
        // 去掉最上面时间、电量等
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
        //        , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // 供存储使用
        SharedPreferences sharedPreferences = getSharedPreferences("register", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 检查是否登录
        String loginStatus = sharedPreferences.getString("logined", "false");
        if ("true".equals(loginStatus)) {
            // 已经登录了，直接跳转首页
            Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
            startActivity(intent);
        }

        registerBtn = findViewById(R.id.register);
        // 设置点击事件
        registerBtn.setOnClickListener(view -> {
            // 跳转到注册界面
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        // 登录按钮
        loginBtn = findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(view -> {
            // 获取用户名跟密码
            EditText userNameEdit = LoginActivity.this.findViewById(R.id.email);
            EditText passwordEdit = LoginActivity.this.findViewById(R.id.login_password);
            String userName = userNameEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            if ("".equals(userName)) {
                Toast.makeText(LoginActivity.this, "邮箱不能为空", Toast.LENGTH_LONG).show();
                LoginActivity.this.findViewById(R.id.account).setBackgroundResource(R.drawable.input_err_radius);
                return;
            }
            if ("".equals(password)) {
                Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
                LoginActivity.this.findViewById(R.id.password).setBackgroundResource(R.drawable.input_err_radius);
                return;
            }

            // 进行登录
            Map<String, String> param = new HashMap<>();
            param.put("email", userName);
            param.put("password", password);

            LoadDialog builder = new LoadDialog(LoginActivity.this)
                    .settTitle("提示")
                    .settContent("正在登录...");
            // 执行正在加载的样式
            builder.show();

            HttpUtils httpUtils = new HttpUtils(LoginActivity.this) {
                @Override
                public void callback(String result) {
                    // 成功返回
                    // 该步必须放到runOnUiThread API接口中执行，在当前线程中是不能执行ui线程里的组件的
                    runOnUiThread(() -> {
                        builder.hide();
                        if ("0".equals(result)) {
                            Dialog.tipsDialog(LoginActivity.this, "该邮箱还未注册！");
                        } else if ("1".equals(result)) {
                            // 登录成功
                            // 将邮箱存到SharedPreferences中，方便后面使用
                            editor.putString("email", userName);
                            editor.putString("logined", "true");
                            editor.apply();
                            // 跳转到首页
                            Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                            startActivity(intent);
                        } else if ("2".equals(result)) {
                            Dialog.tipsDialog(LoginActivity.this, "密码错误，请重试！");
                        }
                    });
                }
            };
            httpUtils.sendPost(Settings.routerMap.get("login"), param);
        });
    }
}
