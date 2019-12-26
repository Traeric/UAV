package com.eric.uav.voice.wakeup.listener;

import android.content.Context;
import android.widget.Toast;

import com.eric.uav.voice.VoiceActivity;
import com.eric.uav.voice.wakeup.WakeUpResult;

/**
 * Created by fujiayi on 2017/6/21.
 */

public class SimpleWakeupListener implements IWakeupListener {
    private Context context;

    private static final String TAG = "SimpleWakeupListener";

    public SimpleWakeupListener(Context context) {
        this.context = context;
    }

    /**
     * 唤醒成功
     * @param word
     * @param result
     */
    @Override
    public void onSuccess(String word, WakeUpResult result) {
        ((VoiceActivity) context).getSynthesizer().speak("诶，你好呀！我是小则同学");
//        MyLogger.info(TAG, "唤醒成功，唤醒词：" + word);
    }

    /**
     * 执行了MyWakeup的stop()方法才会执行
     */
    @Override
    public void onStop() {
//        MyLogger.info(TAG, "唤醒词识别结束：");
    }

    @Override
    public void onError(int errorCode, String errorMessge, WakeUpResult result) {
//        MyLogger.info(TAG, "唤醒错误：" + errorCode + ";错误消息：" + errorMessge + "; 原始返回" + result.getOrigalJson());
    }

    @Override
    public void onASrAudio(byte[] data, int offset, int length) {
//        MyLogger.error(TAG, "audio data： " + data.length);
    }

}
