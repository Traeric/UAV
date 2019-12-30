package com.eric.uav.homepage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.uav.R;
import com.eric.uav.applications.ApplicationActivity;
import com.eric.uav.login.LoginActivity;
import com.eric.uav.map.MapActivity;
import com.eric.uav.profile.ProfileActivity;
import com.eric.uav.utils.BlueToothUtils;
import com.eric.uav.utils.Dialog;

import java.util.LinkedList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button;
    private RecyclerView bondBlueToothView;
    private RecyclerView searchBlueToothView;
    private RecyclerView connecttedBlueToothView;
    private TextView searchingTipsView;
    private TextView connectingTipsView;

    // 底部栏相关
    private TextView mapActivity;
    private TextView profileActivity;
    private TextView applicationActivity;


    private TextView logoutBtn;

    // 搜索到的蓝牙设备会放到这个列表里面
    private List<BluetoothDevice> searchedBlueToothList = new LinkedList<>();
    // 已经配对的蓝牙设备
    private List<BluetoothDevice> bondBlueToothList = new LinkedList<>();
    // 已经连接的蓝牙设备
    private List<BluetoothDevice> connecttedBlueToothList = new LinkedList<>();


    private BroadcastReceiver searchDevices = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 发现设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 获取发现的设备对象
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 防止重复添加
                if (searchedBlueToothList.indexOf(device) == -1 && bondBlueToothList.indexOf(device) == -1) {
                    searchedBlueToothList.add(device);
                }
                // 刷新recycleView
                searchBlueToothView.setAdapter(new SearchDeviceAdapter(HomePageActivity.this, searchedBlueToothList));
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // 扫描完成
                // 修改提示字母
                searchingTipsView.setText("扫描完成...");
                searchingTipsView.setTextColor(getResources().getColor(R.color.success));
                // 取消搜索
//                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                // 配对相关处理
                // 获取正在配对的设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    searchingTipsView.setText("配对完成...");
                    // 刷新两个列表
                    bondBlueToothList.add(device);
                    searchedBlueToothList.remove(device);
                    bondBlueToothView.setAdapter(new BondDeviceAdapter(HomePageActivity.this, bondBlueToothList));
                    searchBlueToothView.setAdapter(new SearchDeviceAdapter(HomePageActivity.this, searchedBlueToothList));
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    searchingTipsView.setText("正在配对中...");
                } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    bondBlueToothList.remove(device);   // 移除设备
                    bondBlueToothView.setAdapter(new BondDeviceAdapter(HomePageActivity.this, bondBlueToothList));
                }
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // 初始化样式
        ((TextView) findViewById(R.id.title)).setText("首页");
        findViewById(R.id.homepage_activity).setBackground(getResources().getDrawable(R.drawable.homepage_select));
        findViewById(R.id.map_activity).setBackground(getResources().getDrawable(R.drawable.map));
        findViewById(R.id.application_activity).setBackground(getResources().getDrawable(R.drawable.other));
        findViewById(R.id.personnal_activity).setBackground(getResources().getDrawable(R.drawable.mine));

        button = findViewById(R.id.test);
        button.setOnClickListener(this);
        logoutBtn = findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);

        // 底部栏相关设置
        mapActivity = findViewById(R.id.map_activity);
        mapActivity.setOnClickListener(this);
        profileActivity = findViewById(R.id.personnal_activity);
        profileActivity.setOnClickListener(this);
        applicationActivity = findViewById(R.id.application_activity);
        applicationActivity.setOnClickListener(this);

        searchingTipsView = findViewById(R.id.search_status);
        connectingTipsView = findViewById(R.id.connecting_tips);

        bondBlueToothView = findViewById(R.id.bondDevice);
        bondBlueToothView.setLayoutManager(new LinearLayoutManager(HomePageActivity.this));
//        bondBlueToothView.addItemDecoration(new BlueToothDecoration());   // 添加分割线

        // 开启搜索蓝牙权限
        this.getPermission();
        // 加载已经连接了的蓝牙
        // 检测是否开启了蓝牙
        BlueToothUtils blueToothUtils = new BlueToothUtils(this);
        if (!blueToothUtils.blueToothIsEnable()) {
            // 没有开启蓝牙，现在开启
            blueToothUtils.enableBlueTooth();
        }
        while (true) {
            // 开启了蓝牙才能操作
            // 获取已经配对了的设备
            if (blueToothUtils.blueToothIsEnable()) {
                bondBlueToothList = blueToothUtils.bondDevices();
                bondBlueToothView.setAdapter(new BondDeviceAdapter(HomePageActivity.this, bondBlueToothList));
                break;
            }
        }

        // 加载搜索设备的RecycleView
        searchBlueToothView = findViewById(R.id.searchDevice);
        searchBlueToothView.setLayoutManager(new LinearLayoutManager(HomePageActivity.this));
        // 设置Adapter
        searchBlueToothView.setAdapter(new SearchDeviceAdapter(HomePageActivity.this, searchedBlueToothList));


        // 加载已连接的蓝牙设备的RecycleView
        connecttedBlueToothView = findViewById(R.id.connectDevice);
        connecttedBlueToothView.setLayoutManager(new LinearLayoutManager(HomePageActivity.this));
        // 设置Adapter
        connecttedBlueToothView.setAdapter(new ConnectDeviceAdapter(HomePageActivity.this, connecttedBlueToothList));


        /*
        设置蓝牙连接监听
         */
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(HomePageActivity.this, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceDisconnected(int profile) {
                // TODO Auto-generated method stub
                Toast.makeText(HomePageActivity.this, "断开", Toast.LENGTH_SHORT).show();
                connecttedBlueToothView.setAdapter(new ConnectDeviceAdapter(HomePageActivity.this,
                        ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getConnectedDevices(profile)));
            }

            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                // 获取连接了的设备
                List<BluetoothDevice> deviceList = proxy.getConnectedDevices();
                connecttedBlueToothView.setAdapter(new ConnectDeviceAdapter(HomePageActivity.this, deviceList));
            }
        }, BluetoothProfile.HEADSET);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.test: {
                // 清空列表
                searchedBlueToothList.clear();
                BlueToothUtils blueToothUtils = new BlueToothUtils(HomePageActivity.this);
                // 注册
                registerReceiver(searchDevices, blueToothUtils.makeFilter());
                // 搜索
                BluetoothAdapter.getDefaultAdapter().startDiscovery();
                /*
                 * 修改ui样式
                 */
                // 修改提示字母
                searchingTipsView.setText("正在搜索中...");
                searchingTipsView.setTextColor(getResources().getColor(R.color.delete));
            }
            break;
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
                builder.setNegativeButton("取消", (dialog, which) -> {});
                builder.show();
            }
            break;
            default:
                break;
        }
    }


    /**
     * 解决：无法发现蓝牙设备的问题
     * <p>
     * 对于发现新设备这个功能, 还需另外两个权限(Android M 以上版本需要显式获取授权,附授权代码):
     */
    private final int ACCESS_LOCATION = 1;

    @SuppressLint("WrongConstant")
    private void getPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            int permissionCheck;
            permissionCheck = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck += this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                //未获得权限
                this.requestPermissions( // 请求授权
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        ACCESS_LOCATION);// 自定义常量,任意整型
            }
        }
    }

    public RecyclerView getBondBlueToothView() {
        return bondBlueToothView;
    }

    public List<BluetoothDevice> getBondBlueToothList() {
        return bondBlueToothList;
    }

    public void setBondBlueToothList(List<BluetoothDevice> bondBlueToothList) {
        this.bondBlueToothList = bondBlueToothList;
    }

    public TextView getConnectingTipsView() {
        return connectingTipsView;
    }

    //  连接成功的回调
    public void callback() {
        runOnUiThread(() -> {
            // 获取socket
            if (BlueToothUtils.bluetoothSocket != null && BlueToothUtils.bluetoothSocket.isConnected()) {
                connectingTipsView.setText("连接成功");
                connectingTipsView.setTextColor(getResources().getColor(R.color.success));
            } else {
                connectingTipsView.setText("连接失败");
                connectingTipsView.setTextColor(getResources().getColor(R.color.delete));
                BlueToothUtils.bluetoothSocket = null;
            }
        });
    }


    /**
     * 实现按下返回键提示用户再按一次返回桌面，而不是返回上一个页面
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
}
