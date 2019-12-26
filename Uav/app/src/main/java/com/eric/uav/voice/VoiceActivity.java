package com.eric.uav.voice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.speech.asr.SpeechConstant;
import com.eric.uav.R;
import com.eric.uav.voice.wakeup.MyWakeup;
import com.eric.uav.voice.wakeup.listener.IWakeupListener;
import com.eric.uav.voice.wakeup.listener.SimpleWakeupListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VoiceActivity extends AppCompatActivity {
    private MyWakeup myWakeup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        // 获取权限
        getPermissions();

        // 监听唤醒识别的结果
        IWakeupListener listener = new SimpleWakeupListener(this);
        myWakeup = new MyWakeup(this, listener);
        // 启动唤醒
        Map<String, Object> params = new HashMap<>();
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        myWakeup.start(params);
    }

    @Override
    protected void onDestroy() {
        // 退出事件管理器
        myWakeup.release();
        super.onDestroy();
    }

    /**
     * 获取权限
     */
    private void getPermissions() {
        String[] permissions = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                //进入到这里代表没有权限.
                toApplyList.add(perm);
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }
}
