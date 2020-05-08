package com.eric.uav.register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.uav.R;
import com.eric.uav.Settings;
import com.eric.uav.dialog.LoadDialog;
import com.eric.uav.login.LoginActivity;
import com.eric.uav.utils.Dialog;
import com.eric.uav.utils.HttpUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {
    private TextView back;
    private Fragment currentFragment;
    private Button nextStep;
    private int step = EMAIL;

    // 注册步骤
    private static final int EMAIL = 0;
    private static final int CAPTCHA = 1;
    private static final int PASSWORD = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 供存储使用
        SharedPreferences sharedPreferences = getSharedPreferences("register", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        back = findViewById(R.id.back);
        back.setOnClickListener((view) -> RegisterActivity.this.finish());

        // 加载email fragment
        currentFragment = new EmailFragment();
        // 添加到registerActivity中
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, currentFragment).commitAllowingStateLoss();

        // 点击下一步
        nextStep = findViewById(R.id.next_step);
        nextStep.setOnClickListener(view -> {
            if (step == EMAIL) {
                // 验证邮箱，发送验证码
                // 获取邮箱
                EditText emailEdit = Objects.requireNonNull(currentFragment.getView()).findViewById(R.id.register_email);
                String email = emailEdit.getText().toString();
                if ("".equals(email)) {
                    // 邮箱为空
                    Dialog.tipsDialog(this, "邮箱不能为空！");
                    return;
                }
                /*
                 * 进行验证
                 */
                Map<String, String> param = new HashMap<>();
                param.put("email", email);

                LoadDialog builder = new LoadDialog(RegisterActivity.this)
                        .settTitle("提示")
                        .settContent("正在生成验证码...");
                builder.setCancelable(false);
                // 执行正在加载的样式
                builder.show();

                HttpUtils httpUtils = new HttpUtils(RegisterActivity.this) {
                    @Override
                    public void callback(String result) {
                        // 成功返回
                        builder.dismiss();
                        // 该步必须放到runOnUiThread API接口中执行，在当前线程中是不能执行ui线程里的组件的
                        runOnUiThread(() -> {
                            if ("0".equals(result)) {
                                builder.dismiss();
                                Dialog.tipsDialog(RegisterActivity.this, "该邮箱已注册");
                                return;
                            }
                            // 将验证码存到SharedPreferences中，方便后面验证
                            editor.putString("captcha", result);
                            editor.putString("email", email);
                            editor.apply();
                            // 设置到验证码的那一步
                            step = CAPTCHA;
                            // 加载填写验证码的Fragment
                            currentFragment = new CaptchaFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment)
                                    .commitAllowingStateLoss();
                        });
                    }
                };
                httpUtils.sendPost(Settings.routerMap.get("sendEmail"), param);

            } else if (step == CAPTCHA) {
                // 获取验证码
                EditText captchaEdit = Objects.requireNonNull(currentFragment.getView()).findViewById(R.id.captcha);
                String captcha = captchaEdit.getText().toString();
                if ("".equals(captcha)) {
                    // 验证码为空
                    Dialog.tipsDialog(this, "验证码不能为空！");
                    return;
                }
                if (captcha.length() != 4) {
                    // 验证码长度不对
                    Dialog.tipsDialog(this, "验证码长度必须是4位！");
                    return;
                }
                // 验证码没问题，进行比对
                LoadDialog builder = new LoadDialog(RegisterActivity.this)
                        .settTitle("提示")
                        .settContent("正在验证中...");
                builder.setCancelable(false);
                // 执行正在加载的样式
                builder.show();
                // 从SharedPreferences获取先前存储的验证码
                String captcha1 = sharedPreferences.getString("captcha", "0000");// 没取到时默认使用0000
                if (captcha.equals(captcha1)) {
                    // 验证码填写正确
                    builder.hide();
                    // 设置到验证码的哪一步
                    step = PASSWORD;
                    // 显示输入密码的Fragment
                    currentFragment = new SetPasswordFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment)
                            .commitAllowingStateLoss();
                    // 将按钮的字更改
                    nextStep.setText("开始注册");
                } else {
                    // 验证码填写错误
                    builder.hide();
                    Dialog.tipsDialog(this, "验证码填写错误！");
                }

            } else {
                // 验证密码
                EditText passwordEdit = Objects.requireNonNull(currentFragment.getView()).findViewById(R.id.register_password);
                EditText repasswordEdit = currentFragment.getView().findViewById(R.id.register_repassword);
                String password = passwordEdit.getText().toString();
                String repassword = repasswordEdit.getText().toString();

                if ("".equals(password)) {
                    Dialog.tipsDialog(this, "密码不能为空！");
                    return;
                }
                if (password.length() < 6) {
                    Dialog.tipsDialog(this, "密码不能小于6位数！");
                    return;
                }
                if (!password.equals(repassword)) {
                    Dialog.tipsDialog(this, "两次密码输入不一致！");
                    return;
                }
                // 开始注册
                String email = sharedPreferences.getString("email", "....");

                Map<String, String> param = new HashMap<>();
                param.put("email", email);
                param.put("password", password);

                LoadDialog builder = new LoadDialog(RegisterActivity.this)
                        .settTitle("提示")
                        .settContent("正在注册中，请稍等...");
                builder.setCancelable(false);
                // 执行正在加载的样式
                builder.show();

                HttpUtils httpUtils = new HttpUtils(RegisterActivity.this) {
                    @Override
                    public void callback(String result) {
                        // 成功返回
                        // 该步必须放到runOnUiThread API接口中执行，在当前线程中是不能执行ui线程里的组件的
                        runOnUiThread(() -> {
                            builder.hide();
                            Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_LONG).show();
                        });
                        // 注册成功，返回登录页面
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                };
                httpUtils.sendPost(Settings.routerMap.get("register"), param);
            }
        });
    }
}
