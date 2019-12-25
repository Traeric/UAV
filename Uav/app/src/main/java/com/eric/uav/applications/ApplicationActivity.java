package com.eric.uav.applications;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eric.uav.R;
import com.eric.uav.homepage.HomePageActivity;
import com.eric.uav.map.MapActivity;
import com.eric.uav.profile.ProfileActivity;
import com.eric.uav.send_at.SendATActivity;
import com.eric.uav.uav_video.UavVideoActivity;

public class ApplicationActivity extends AppCompatActivity implements View.OnClickListener {
    // 底部栏activity
    private TextView homepageActivityView;
    private TextView mapActivityView;
    private TextView profileActivityView;

    private ImageView checkUavVideoBtn;
    private ImageView snedAtBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        // 初始化样式
        ((TextView) findViewById(R.id.title)).setText("应用");
        findViewById(R.id.homepage_activity).setBackground(getResources().getDrawable(R.drawable.home_page));
        findViewById(R.id.map_activity).setBackground(getResources().getDrawable(R.drawable.map));
        findViewById(R.id.application_activity).setBackground(getResources().getDrawable(R.drawable.other_select));
        findViewById(R.id.personnal_activity).setBackground(getResources().getDrawable(R.drawable.mine));

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
            default:
                break;
        }
    }
}
