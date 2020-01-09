package com.eric.uav.applications.link_bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.uav.R;
import com.eric.uav.utils.BlueToothUtils;
import com.eric.uav.utils.Dialog;

import java.util.LinkedList;
import java.util.List;

public class BlueToothActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView button;
    private RecyclerView bondBlueToothView;
    private RecyclerView searchBlueToothView;
    private RecyclerView connecttedBlueToothView;
    private TextView searchingTipsView;
    private TextView connectingTipsView;

    private ImageView closeBtn;

    // 搜索到的蓝牙设备会放到这个列表里面
    private List<BluetoothDevice> searchedBlueToothList = new LinkedList<>();
    // 已经配对的蓝牙设备
    private List<BluetoothDevice> bondBlueToothList = new LinkedList<>();
    // 已经连接的蓝牙设备
    private List<BluetoothDevice> connecttedBlueToothList = new LinkedList<>();

    private boolean searchFinished = true;


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
                searchBlueToothView.setAdapter(new SearchDeviceAdapter(BlueToothActivity.this, searchedBlueToothList));
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // 扫描完成
                // 修改提示字母
                searchingTipsView.setText("扫描完成...");
                searchingTipsView.setTextColor(getResources().getColor(R.color.success));
                searchFinished = true;
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
                    bondBlueToothView.setAdapter(new BondDeviceAdapter(BlueToothActivity.this, bondBlueToothList));
                    searchBlueToothView.setAdapter(new SearchDeviceAdapter(BlueToothActivity.this, searchedBlueToothList));
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    searchingTipsView.setText("正在配对中...");
                } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    bondBlueToothList.remove(device);   // 移除设备
                    bondBlueToothView.setAdapter(new BondDeviceAdapter(BlueToothActivity.this, bondBlueToothList));
                }
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);

        button = findViewById(R.id.search_device);
        button.setOnClickListener(this);

        searchingTipsView = findViewById(R.id.search_status);
        connectingTipsView = findViewById(R.id.connecting_tips);

        bondBlueToothView = findViewById(R.id.bondDevice);
        bondBlueToothView.setLayoutManager(new LinearLayoutManager(BlueToothActivity.this));
        // 防止卡顿
        bondBlueToothView.setFocusable(false);
        bondBlueToothView.setHasFixedSize(true);
        bondBlueToothView.setNestedScrollingEnabled(false);
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
                bondBlueToothView.setAdapter(new BondDeviceAdapter(BlueToothActivity.this, bondBlueToothList));
                break;
            }
        }

        // 加载搜索设备的RecycleView
        searchBlueToothView = findViewById(R.id.searchDevice);
        searchBlueToothView.setLayoutManager(new LinearLayoutManager(BlueToothActivity.this));
        // 设置Adapter
        searchBlueToothView.setAdapter(new SearchDeviceAdapter(BlueToothActivity.this, searchedBlueToothList));
        // 防止卡顿
        searchBlueToothView.setFocusable(false);
        searchBlueToothView.setHasFixedSize(true);
        searchBlueToothView.setNestedScrollingEnabled(false);

        // 加载已连接的蓝牙设备的RecycleView
        connecttedBlueToothView = findViewById(R.id.connectDevice);
        connecttedBlueToothView.setLayoutManager(new LinearLayoutManager(BlueToothActivity.this));
        // 设置Adapter
        connecttedBlueToothView.setAdapter(new ConnectDeviceAdapter(BlueToothActivity.this, connecttedBlueToothList));
        // 防止卡顿
        connecttedBlueToothView.setFocusable(false);
        connecttedBlueToothView.setHasFixedSize(true);
        connecttedBlueToothView.setNestedScrollingEnabled(false);

        /*
        设置蓝牙连接监听
         */
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(BlueToothActivity.this, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceDisconnected(int profile) {
                // TODO Auto-generated method stub
                Toast.makeText(BlueToothActivity.this, "断开", Toast.LENGTH_SHORT).show();
                connecttedBlueToothView.setAdapter(new ConnectDeviceAdapter(BlueToothActivity.this,
                        ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getConnectedDevices(profile)));
            }

            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                // 获取连接了的设备
                List<BluetoothDevice> deviceList = proxy.getConnectedDevices();
                connecttedBlueToothView.setAdapter(new ConnectDeviceAdapter(BlueToothActivity.this, deviceList));
            }
        }, BluetoothProfile.HEADSET);

        closeBtn = findViewById(R.id.close_bluetooth);
        closeBtn.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_device: {
                if (!searchFinished) {
                    Dialog.toastWithoutAppName(this, "扫描还未完成");
                    return;
                }
                searchFinished = false;
                // 清空列表
                searchedBlueToothList.clear();
                searchBlueToothView.setAdapter(new SearchDeviceAdapter(BlueToothActivity.this, searchedBlueToothList));
                BlueToothUtils blueToothUtils = new BlueToothUtils(BlueToothActivity.this);
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
            case R.id.close_bluetooth: {
                // 关闭当前应用
                finish();
                overridePendingTransition(0, 0);
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
}
