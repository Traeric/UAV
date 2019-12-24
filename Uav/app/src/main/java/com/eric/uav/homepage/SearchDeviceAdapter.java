package com.eric.uav.homepage;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.uav.R;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class SearchDeviceAdapter extends RecyclerView.Adapter<SearchDeviceAdapter.LinearViewHolder> {
    private Context context;
    private List<BluetoothDevice> devices;

    public SearchDeviceAdapter(Context context, List<BluetoothDevice> devices) {
        this.context = context;
        this.devices = devices;
    }

    @NonNull
    @Override
    public SearchDeviceAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new LinearViewHolder(LayoutInflater.from(this.context).inflate(R.layout.recycle_search_bluetooth_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchDeviceAdapter.LinearViewHolder viewHolder, int i) {
        // 设置每一个蓝牙的信息
        BluetoothDevice device = devices.get(i);
        // 设置名称
        viewHolder.blueToothName.setText(device.getName() == null ? device.getAddress() : device.getName());
        // 设置图标
        switch (device.getBluetoothClass().getMajorDeviceClass()) {
            case BluetoothClass.Device.Major.AUDIO_VIDEO: {
                // 表示该蓝牙是耳机
                viewHolder.itemIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.headset));
            }
            break;
            case BluetoothClass.Device.Major.PHONE: {
                // 表示是手机
                viewHolder.itemIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.phone));
            }
            break;
            default: {
                // 其他设备
                viewHolder.itemIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.bluetooth));
            }
            break;
        }

        // 设置按钮点击事件
        viewHolder.connectBtn.setOnClickListener(view -> {
            // 进行配对
            try {
                Method method = BluetoothDevice.class.getMethod("createBond");
                method.invoke(device);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {
        private TextView blueToothName;
        private ImageView itemIcon;
        private RoundButton connectBtn;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            blueToothName = itemView.findViewById(R.id.bluetooth_name);
            itemIcon = itemView.findViewById(R.id.icon_bluetooth);
            connectBtn = itemView.findViewById(R.id.connect);
        }
    }
}
