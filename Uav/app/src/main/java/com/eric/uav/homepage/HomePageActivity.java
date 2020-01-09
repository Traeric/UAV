package com.eric.uav.homepage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eric.uav.R;
import com.eric.uav.applications.ApplicationActivity;
import com.eric.uav.applications.look_album.LookAlbumActivity;
import com.eric.uav.applications.look_album.RecycleViewLinearItemDecoration;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener, OnBannerListener {
    // 底部栏相关
    private RelativeLayout mapActivity;
    private RelativeLayout profileActivity;
    private RelativeLayout applicationActivity;


    private TextView logoutBtn;

    private RecyclerView recyclerView;
    private List<Map<String, Object>> newsList = new ArrayList<Map<String, Object>>();


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

        // 初始化banner
        initBanner();
        // 初始化新闻列表
        initNewsList();
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

    /**
     * 加载新闻列表
     */
    public void initNewsList() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", "京瓷拟用无人机打造无信号地区移动通信中继站");
        map.put("images", new String[] {
                "https://pics4.baidu.com/feed/f3d3572c11dfa9ecc48abde994b6f005908fc111.jpeg?token=091a7457b1d5cac8de4e599df4192dde&s=990993578E5216D24A8494E50300C061",
                "https://pics3.baidu.com/feed/e4dde71190ef76c6756d48d9b104fafcae516799.jpeg?token=2b490732a9e5973d2b008fb8cef3234e&s=F9878F50769E5FC26C1884D40300C0A3",
                "https://pics2.baidu.com/feed/7acb0a46f21fbe0910cc7bc25d720b358644ad6a.jpeg?token=4486f85c7be96c36e55f54ff79e84f4a&s=611457990ED14CC24641BCC50300A0B2"});
        map.put("author", "半导体投资联盟");
        map.put("date", "2020年1月9日");
        newsList.add(map);

        Map<String, Object> map1 = new HashMap<>();
        map1.put("title", "直播预告｜明天是春运第一天，警犬无人机将一起亮相，安检员乘务员会接力服务");
        map1.put("images", new String[] {
                "https://ss2.baidu.com/6ONYsjip0QIZ8tyhnq/it/u=3500851598,3121780975&fm=173&app=49&f=JPEG?w=640&h=480&s=AD60DB035A4F1ADA489904B50300D010",
                "https://pics4.baidu.com/feed/a9d3fd1f4134970a0726415c8112d7cea6865d4d.jpeg?token=e43f4ca3174bd72b2e33895e68dfbfcd&s=F21470843E00035B04B278810300708A",
                "https://dg-fd.zol-img.com.cn/t_s2000x2000/g2/M00/04/05/ChMlWVyi_PWILAvnAAAwAa7MJbsAAJLKgOxwKUAADAZ566.jpg"});
        map1.put("author", "钱江晚报");
        map1.put("date", "2020年1月9日");
        newsList.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("title", "CES2019现场：折叠放进手机壳的SELFLY无人机");
        map2.put("images", new String[] {
                "https://article-fd.zol-img.com.cn/t_s640x2000/g5/M00/01/0A/ChMkJ1w4J8-If2dqAA-dHJTetIwAAuUrQLBQNYAD500638.jpg",
                "https://article-fd.zol-img.com.cn/t_s640x2000/g5/M00/01/0A/ChMkJlw4J8mIOihpABQXJC_HRHoAAuUrQKooDEAFBc8015.jpg",
                "https://article-fd.zol-img.com.cn/t_s640x2000/g5/M00/01/0A/ChMkJlw4J8iIU1CUAA2AeDFAqtYAAuUrQKbH6EADYCQ233.jpg",
                "https://article-fd.zol-img.com.cn/t_s640x2000/g5/M00/01/0A/ChMkJ1w4J8-IRHJTAA36kVoTB68AAuUrQLsJp8ADfqp368.jpg"});
        map2.put("author", "朱玲");
        map2.put("date", "2019年1月11日");
        newsList.add(map2);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("title", "CES2019现场：折叠放进手机壳的SELFLY无人机");
        map3.put("images", new String[] {
                "https://article-fd.zol-img.com.cn/t_s640x2000/g5/M00/01/0A/ChMkJ1w4J8-If2dqAA-dHJTetIwAAuUrQLBQNYAD500638.jpg",
                "https://article-fd.zol-img.com.cn/t_s640x2000/g5/M00/01/0A/ChMkJlw4J8mIOihpABQXJC_HRHoAAuUrQKooDEAFBc8015.jpg",
                "https://article-fd.zol-img.com.cn/t_s640x2000/g5/M00/01/0A/ChMkJlw4J8iIU1CUAA2AeDFAqtYAAuUrQKbH6EADYCQ233.jpg",
                "https://article-fd.zol-img.com.cn/t_s640x2000/g5/M00/01/0A/ChMkJ1w4J8-IRHJTAA36kVoTB68AAuUrQLsJp8ADfqp368.jpg"});
        map3.put("author", "朱玲");
        map3.put("date", "2019年1月11日");
        newsList.add(map3);

        Map<String, Object> map4 = new HashMap<>();
        map4.put("title", "CES2019现场：折叠放进手机壳的SELFLY无人机");
        map4.put("images", new String[] {
                "https://article-fd.zol-img.com.cn/t_s640x2000/g5/M00/01/0A/ChMkJ1w4J8-If2dqAA-dHJTetIwAAuUrQLBQNYAD500638.jpg",
                "https://article-fd.zol-img.com.cn/t_s640x2000/g5/M00/01/0A/ChMkJlw4J8mIOihpABQXJC_HRHoAAuUrQKooDEAFBc8015.jpg",
                "https://article-fd.zol-img.com.cn/t_s640x2000/g5/M00/01/0A/ChMkJlw4J8iIU1CUAA2AeDFAqtYAAuUrQKbH6EADYCQ233.jpg",
                "https://article-fd.zol-img.com.cn/t_s640x2000/g5/M00/01/0A/ChMkJ1w4J8-IRHJTAA36kVoTB68AAuUrQLsJp8ADfqp368.jpg"});
        map4.put("author", "朱玲");
        map4.put("date", "2019年1月11日");
        newsList.add(map4);

        recyclerView = findViewById(R.id.news_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyDecoration());
        recyclerView.setAdapter(new NewsAdapter(this, newsList));
    }

    class MyDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.dividerHeight));
        }
    }
}
