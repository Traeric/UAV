package com.eric.uav.applications.voice.wakeup.listener;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.eric.uav.R;
import com.eric.uav.applications.voice.VoiceActivity;
import com.eric.uav.applications.voice.audioplay.listener.FinishStatus;
import com.eric.uav.applications.voice.wakeup.WakeUpResult;

/**
 * Created by fujiayi on 2017/6/21.
 */

public class SimpleWakeupListener implements IWakeupListener {
    private Context context;
    public static boolean flag = true;

    private static final String TAG = "SimpleWakeupListener";

    public SimpleWakeupListener(Context context) {
        this.context = context;
    }

    /**
     * 唤醒成功
     *
     * @param word
     * @param result
     */
    @Override
    public void onSuccess(String word, WakeUpResult result) {
        if (flag) {
            flag = false;
            FinishStatus.finishAudioPlay = FinishStatus.START_AUDIO_DISTINGUISH;
            if (word.contains("同学")) {
                ((VoiceActivity) context).getSynthesizer().speak("我在");
            } else if (word.contains("你好")) {
                ((VoiceActivity) context).getSynthesizer().speak("你好啊，请对我说指令吧！");
            }
            // 切换图片
            Glide.with(context).load(R.drawable.audio).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(((VoiceActivity) context).getVoiceAssistantLogo());
        }
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
