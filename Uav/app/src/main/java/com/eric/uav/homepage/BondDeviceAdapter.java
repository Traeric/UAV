package com.eric.uav.homepage;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.uav.R;
import com.eric.uav.utils.BlueToothUtils;
import com.xuexiang.xui.widget.button.roundbutton.RoundButton;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BondDeviceAdapter extends RecyclerView.Adapter<BondDeviceAdapter.LinearViewHolder> {
    private Context context;
    private List<BluetoothDevice> devices;

    public BondDeviceAdapter(Context context, List<BluetoothDevice> devices) {
        this.context = context;
        this.devices = devices;
    }

    @NonNull
    @Override
    public BondDeviceAdapter.LinearViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new LinearViewHolder(LayoutInflater.from(this.context).inflate(R.layout.recycle_linear_item, viewGroup, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull BondDeviceAdapter.LinearViewHolder viewHolder, int i) {
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

        // 设置连接按钮
        viewHolder.connectBtn.setOnClickListener(view -> {
            // 连接
            BlueToothUtils blueToothUtils = new BlueToothUtils((AppCompatActivity) context);
            blueToothUtils.connectBlueTooth(device, UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            // 显示提示
            TextView connectingTipsView = ((HomePageActivity) context).getConnectingTipsView();
            connectingTipsView.setText("正在连接中...");
            connectingTipsView.setTextColor(context.getResources().getColor(R.color.tips));
        });

        // 设置断开按钮
        viewHolder.unBondBtn.setOnClickListener(view -> {
            try {
                Method method = device.getClass().getMethod("removeBond", (Class[]) null);
                method.setAccessible(true);
                method.invoke(device, (Object[]) null);

                // 删除该设备
                List<BluetoothDevice> bondBlueToothList = ((HomePageActivity) context).getBondBlueToothList();
                bondBlueToothList.remove(device);   // 移除设备
                ((HomePageActivity) context).setBondBlueToothList(bondBlueToothList);
                RecyclerView bondBlueToothView = ((HomePageActivity) context).getBondBlueToothView();
                bondBlueToothView.setAdapter(new BondDeviceAdapter(context, bondBlueToothList));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                Toast.makeText(context, "解绑失败", Toast.LENGTH_SHORT).show();
            }
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
        private RoundButton connectBtn;
        private RoundButton unBondBtn;


        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            blueToothName = itemView.findViewById(R.id.bluetooth_name);
            itemIcon = itemView.findViewById(R.id.icon_bluetooth);
            connectBtn = itemView.findViewById(R.id.connect);
            unBondBtn = itemView.findViewById(R.id.delete);
        }
    }
}
