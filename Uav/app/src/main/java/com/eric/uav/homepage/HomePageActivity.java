package com.eric.uav.homepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eric.uav.R;
import com.eric.uav.Settings;
import com.eric.uav.applications.ApplicationActivity;
import com.eric.uav.applications.look_album.LookAlbumActivity;
import com.eric.uav.applications.uav_video.UavVideoActivity;
import com.eric.uav.login.LoginActivity;
import com.eric.uav.map.MapActivity;
import com.eric.uav.profile.ProfileActivity;
import com.eric.uav.zxing.android.CaptureActivity;
import com.eric.uav.utils.Dialog;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
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


    private TextView moreFuncBtn;
    private LinearLayout logoutBtn;
    private LinearLayout scanScreen;

    private WebView newListPanel;

    private SharedPreferences sharedPreferences;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        sharedPreferences = getSharedPreferences("register", MODE_PRIVATE);

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
        // 加载头像
        Glide.with(this).load("http://" + Settings.ServerHost + ":" +
                Settings.ServerPort + sharedPreferences.getString("avatar", "/static/assets/img/profile.jpg"))
                .into((RadiusImageView) findViewById(R.id.top_avatar));


        // 底部栏相关设置
        mapActivity = findViewById(R.id.map_activity);
        mapActivity.setOnClickListener(this);
        profileActivity = findViewById(R.id.personnal_activity);
        profileActivity.setOnClickListener(this);
        applicationActivity = findViewById(R.id.application_activity);
        applicationActivity.setOnClickListener(this);

        // 初始化banner
        initBanner();
        // 初始化新闻列表
        initNewsList();

        moreFuncBtn = findViewById(R.id.more_func);
        moreFuncBtn.setOnClickListener(this);
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
            case R.id.logout_lin: {
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
            case R.id.more_func: {
                View moreFuncView = getLayoutInflater().inflate(R.layout.popupwindow_more_func, null);
                PopupWindow popupWindow = new PopupWindow(moreFuncView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setOutsideTouchable(true);    // 点击其他区域能够隐藏popupWindow
                popupWindow.setFocusable(true);    // 设置点击一下出现，再点击隐藏的效果
                popupWindow.showAsDropDown(moreFuncBtn);
                // 设置阴影
                WindowManager.LayoutParams lp = HomePageActivity.this.getWindow().getAttributes();
                lp.alpha = 0.8f;
                HomePageActivity.this.getWindow().setAttributes(lp);
                popupWindow.setOnDismissListener(() -> {
                    WindowManager.LayoutParams lpDel = HomePageActivity.this.getWindow().getAttributes();
                    lpDel.alpha = 1f;
                    HomePageActivity.this.getWindow().setAttributes(lpDel);
                });

                logoutBtn = moreFuncView.findViewById(R.id.logout_lin);
                logoutBtn.setOnClickListener(this);

                scanScreen = moreFuncView.findViewById(R.id.scan_btn);
                scanScreen.setOnClickListener(this);
            }
            break;
            case R.id.scan_btn: {
                // 开始二维码扫描
                startActivity(new Intent(this, CaptureActivity.class));
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

    /**
     * 加载新闻列表
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void initNewsList() {
        newListPanel = findViewById(R.id.news_list);
        // 防止WebView打开浏览器
        newListPanel.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        newListPanel.getSettings().setJavaScriptEnabled(true);    // 支持javascript
        newListPanel.loadUrl("http://" + Settings.ServerHost + ":" + Settings.ServerPort + "/userManage/news_list");
    }
}
