package com.eric.uav.map;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;
import com.eric.uav.R;
import com.eric.uav.homepage.HomePageActivity;
import com.eric.uav.profile.ProfileActivity;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;

public class MapActivity extends AppCompatActivity implements View.OnClickListener {
    private RoundButton getLocation;

    private MapView mapView;
    private MyLocationStyle myLocationStyle;
    private AMap aMap;

    // 底部栏相关
    private TextView homepageActivityView;
    private TextView profileActivityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // 初始化样式
        ((TextView) findViewById(R.id.title)).setText("地图");
        findViewById(R.id.homepage_activity).setBackground(getResources().getDrawable(R.drawable.home_page));
        findViewById(R.id.map_activity).setBackground(getResources().getDrawable(R.drawable.map_select));
        findViewById(R.id.application_activity).setBackground(getResources().getDrawable(R.drawable.other));
        findViewById(R.id.personnal_activity).setBackground(getResources().getDrawable(R.drawable.mine));

        homepageActivityView = findViewById(R.id.homepage_activity);
        homepageActivityView.setOnClickListener(this);
        profileActivityView = findViewById(R.id.personnal_activity);
        profileActivityView.setOnClickListener(this);

        // 加载地图样式
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        // 定位
        locationMap();

        // 获取定位信息
        getLocation = findViewById(R.id.current_location);
        getLocation.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homepage_activity: {
                // 跳转到首页
                Intent intent = new Intent(MapActivity.this, HomePageActivity.class);
                startActivity(intent);
                // 去掉入场动画
                overridePendingTransition(0, 0);
            }
            break;
            case R.id.current_location: {
                Toast.makeText(MapActivity.this,
                        "当前位置：\n经度：" + aMap.getMyLocation().getLongitude() + "\n纬度：" + aMap.getMyLocation().getLatitude(),
                        Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.personnal_activity: {
                // 跳转到首页
                Intent intent = new Intent(MapActivity.this, ProfileActivity.class);
                startActivity(intent);
                // 去掉入场动画
                overridePendingTransition(0, 0);
            }
            break;
            default:
                break;
        }
    }

    /**
     * 获取当前定位
     */
    public void locationMap() {
        // 初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle = new MyLocationStyle();
        // 连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);
        // 设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.interval(2000);
        // 设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);
        // aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMyLocationEnabled(true);
        // 开启室内地图，缩放到一定级别（>=17）时会显示
        aMap.showIndoorMap(true);
    }
}
