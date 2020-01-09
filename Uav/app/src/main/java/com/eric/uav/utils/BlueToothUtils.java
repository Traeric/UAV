package com.eric.uav.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.eric.uav.applications.link_bluetooth.BlueToothActivity;
import com.eric.uav.homepage.HomePageActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BlueToothUtils {
    private BluetoothAdapter bluetoothAdapter;
    private AppCompatActivity currentActivity;
    public static BluetoothSocket bluetoothSocket = null;

    // 已经配对的蓝牙列表
    private List<BluetoothDevice> bondBlueTooth;

    public BlueToothUtils(AppCompatActivity activity) {
        this.currentActivity = activity;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bondBlueTooth = new ArrayList<>();
    }

    /**
     * 判断设备是否支持蓝牙
     *
     * @return boolean
     */
    public boolean isSupportBluetooth() {
        return bluetoothAdapter != null;
    }

    /**
     * 判断蓝牙是否开启
     *
     * @return boolean
     */
    public boolean blueToothIsEnable() {
        return bluetoothAdapter.isEnabled();
    }

    /**
     * 打开蓝牙
     */
    public void enableBlueTooth() {
        bluetoothAdapter.enable();
        Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        // 设置自己手机蓝牙多少秒可见，最多300秒
        bluetoothIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        // 弹窗，让用户确认开启
        currentActivity.startActivity(bluetoothIntent);
    }

    /**
     * 开始搜索其他蓝牙设备
     */
    private void searchBlueTooth() {
        // 先判断是否处于搜索状态，是的话就先关闭再搜索
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    /**
     * 蓝牙广播过滤器
     * 蓝牙状态改变
     * 找到设备
     * 搜索完成
     * 开始扫描
     * 状态改变
     *
     * @return IntentFilter
     */
    public IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);   // 蓝牙状态改变的广播
        filter.addAction(BluetoothDevice.ACTION_FOUND);       // 找到设备的广播
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);   // 搜索完成的广播
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);    // 开始扫描的广播
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);    // 状态改变
        return filter;
    }

    /**
     * 获取已经绑定了的设备
     *
     * @return map
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<BluetoothDevice> bondDevices() {
        // 清空绑定设备
        bondBlueTooth.clear();
        // 获取已经绑定了的设备
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            bondedDevices.forEach((item) -> {
                // 防止重复添加
                if (bondBlueTooth.indexOf(item) == -1) {
                    bondBlueTooth.add(item);
                }
            });
        }
        return bondBlueTooth;
    }

    /**
     * 连接蓝牙
     */
    public void connectBlueTooth(BluetoothDevice device, UUID uuid) {
        if (bluetoothSocket != null) {
            Toast.makeText(currentActivity, "正在连接，请不要重复点击", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {
            try {
                // 必须是服务端的UUID一致，不能是随便randomUUID
                bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
                // 关闭蓝牙扫描
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                // 连接蓝牙
                if (!bluetoothSocket.isConnected()) {
                    bluetoothSocket.connect();
                }
            } catch (IOException e) {
                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
            // 执行回调
            ((BlueToothActivity) currentActivity).callback();
        }).start();
    }
}
