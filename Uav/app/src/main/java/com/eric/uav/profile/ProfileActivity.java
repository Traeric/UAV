package com.eric.uav.profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.eric.uav.R;
import com.eric.uav.homepage.HomePageActivity;
import com.eric.uav.map.MapActivity;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    // 底部栏相关状态
    private TextView homepageActivity;
    private TextView mapActivity;

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
            default:
                break;
        }
    }
}
