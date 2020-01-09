package com.eric.uav.applications.link_bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eric.uav.R;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;

import java.util.List;

public class ConnectDeviceAdapter extends RecyclerView.Adapter<ConnectDeviceAdapter.LinearViewHolder> {
    private Context context;
    private List<BluetoothDevice> devices;

    public ConnectDeviceAdapter(Context context, List<BluetoothDevice> devices) {
        this.context = context;
        this.devices = devices;
    }

    @NonNull
    @Override
    public ConnectDeviceAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new LinearViewHolder(LayoutInflater.from(this.context).inflate(R.layout.recycle_connect_bluetooth_item, viewGroup, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ConnectDeviceAdapter.LinearViewHolder viewHolder, int i) {
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

        viewHolder.disconnectBtn.setOnClickListener(view -> {

        });
    }

    /**
     * 列表长度，也就是有多少项
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return devices.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {
        private TextView blueToothName;
        private ImageView itemIcon;
        private RoundButton disconnectBtn;


        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            blueToothName = itemView.findViewById(R.id.bluetooth_name);
            itemIcon = itemView.findViewById(R.id.icon_bluetooth);
            disconnectBtn = itemView.findViewById(R.id.disconnect);
        }
    }
}
