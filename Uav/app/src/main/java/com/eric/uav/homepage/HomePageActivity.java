package com.eric.uav.homepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eric.uav.R;
import com.eric.uav.applications.ApplicationActivity;
import com.eric.uav.applications.look_album.LookAlbumActivity;
import com.eric.uav.applications.uav_video.UavVideoActivity;
import com.eric.uav.login.LoginActivity;
import com.eric.uav.map.MapActivity;
import com.eric.uav.profile.ProfileActivity;
import com.eric.uav.utils.Dialog;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener, OnBannerListener {
    // 底部栏相关
    private RelativeLayout mapActivity;
    private RelativeLayout profileActivity;
    private RelativeLayout applicationActivity;


    private TextView logoutBtn;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // 初始化样式
        ((TextView) findViewById(R.id.title)).setText("首页");
        findViewById(R.id.homepage_activity_item).setBackgroundResource(R.drawable.homepage_select);
        findViewById(R.id.map_activity_item).setBackgroundResource(R.drawable.map);
        findViewById(R.id.application_activity_item).setBackgroundResource(R.drawable.other);
        findViewById(R.id.personnal_activity_item).setBackgroundResource(R.drawable.mine);

        ((TextView) findViewById(R.id.homepage_activity_item_tips)).setTextColor(getResources().getColor(R.color.select_color));
        ((TextView) findViewById(R.id.map_activity_item_tips)).setTextColor(getResources().getColor(R.color.no_select_color));
        ((TextView) findViewById(R.id.application_activity_item_tips)).setTextColor(getResources().getColor(R.color.no_select_color));
        ((TextView) findViewById(R.id.personnal_activity_item_tips)).setTextColor(getResources().getColor(R.color.no_select_color));

        logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);

        // 底部栏相关设置
        mapActivity = findViewById(R.id.map_activity);
        mapActivity.setOnClickListener(this);
        profileActivity = findViewById(R.id.personnal_activity);
        profileActivity.setOnClickListener(this);
        applicationActivity = findViewById(R.id.application_activity);
        applicationActivity.setOnClickListener(this);

        initBanner();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.map_activity: {
                // 跳转到mapActivity
                Intent intent = new Intent(HomePageActivity.this, MapActivity.class);
                startActivity(intent);
                // 去掉进场动画
                overridePendingTransition(0, 0);
            }
            break;
            case R.id.personnal_activity: {
                // 跳转到profileActivity
                Intent intent = new Intent(HomePageActivity.this, ProfileActivity.class);
                startActivity(intent);
                // 去掉进场动画
                overridePendingTransition(0, 0);
            }
            break;
            case R.id.application_activity: {
                // 跳转到applicationActivity
                Intent intent = new Intent(HomePageActivity.this, ApplicationActivity.class);
                startActivity(intent);
                // 去掉进场动画
                overridePendingTransition(0, 0);
            }
            break;
            case R.id.logout_btn: {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomePageActivity.this);
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
                    startActivity(new Intent(HomePageActivity.this, LoginActivity.class));
                    overridePendingTransition(0, 0);
                });
                builder.setNegativeButton("取消", (dialog, which) -> {
                });
                builder.show();
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
                Dialog.toastWithoutAppName(HomePageActivity.this, "再按一次退出Uav");
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

    /**
     * 设置banner
     */
    public void initBanner() {
        //放图片地址的集合
        ArrayList<Integer> list_path = new ArrayList<>();
        //放标题的集合
        ArrayList<String> list_title = new ArrayList<>();

        list_path.add(R.drawable.app_banner);
        list_path.add(R.drawable.uav_video);
        list_path.add(R.drawable.look_album);
        list_title.add("应用中心");
        list_title.add("查看航拍");
        list_title.add("查看相册");
        Banner banner = findViewById(R.id.banner);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        // 设置轮播的动画效果
        banner.setBannerAnimation(Transformer.Default);
        // 设置图片加载器
        banner.setImageLoader(new BannerImageLoader());
        // 设置图片集合
        banner.setImages(list_path);
        banner.setBannerTitles(list_title);
        // 点击监听
        banner.setOnBannerListener(this);
        // banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    @Override
    public void OnBannerClick(int position) {
        switch (position) {
            case 0: {
                // 跳转到应用页面
                startActivity(new Intent(HomePageActivity.this, ApplicationActivity.class));
            }
            break;
            case 1: {
                // 跳转到航拍页面
                startActivity(new Intent(HomePageActivity.this, UavVideoActivity.class));
            }
            break;
            case 2: {
                // 跳转到相册页面
                startActivity(new Intent(HomePageActivity.this, LookAlbumActivity.class));
            }
            break;
            default:
                break;
        }
    }
}
