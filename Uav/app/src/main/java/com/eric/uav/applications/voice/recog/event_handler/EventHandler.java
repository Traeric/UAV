package com.eric.uav.applications.voice.recog.event_handler;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import com.baidu.speech.asr.SpeechConstant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.eric.uav.R;
import com.eric.uav.applications.voice.VoiceActivity;
import com.eric.uav.applications.voice.audioplay.listener.FinishStatus;
import com.eric.uav.applications.voice.wakeup.listener.SimpleWakeupListener;
import com.eric.uav.utils.Dialog;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理用户的语音事件
 */
public abstract class EventHandler {
    protected Context context;

    public EventHandler(Context context) {
        this.context = context;
    }

    // 为了反射
    public EventHandler() {
    }

    /**
     * 处理语音
     *
     * @param word 用户说的话
     */
    public void assignEvent(String word) {
        before(word);
        // 切换图片
        if (!FinishStatus.continueConversationMode) {
            Glide.with(context).load(R.drawable.voice_symble).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(((VoiceActivity) context).getVoiceAssistantLogo());
            // 开启唤醒
            SimpleWakeupListener.flag = true;
        }
    }

    protected abstract void before(String word);

    /**
     * 开启语音识别
     *
     * @param context
     */
    public void startVoiceDistinguish(Context context) {
        // 唤醒结束，在这里开始语音识别
        System.out.println("===========");
        // 开启语音识别
        Map<String, Object> map = new HashMap<>();
        map.put(SpeechConstant.ACCEPT_AUDIO_DATA, true); // 目前必须开启此回掉才能保存音频
        map.put(SpeechConstant.OUT_FILE,
                Environment.getExternalStorageDirectory().toString() + "/UAVASR" + "/outfile.pcm");
        ((VoiceActivity) context).getRecognizer().start(map);
    }

    /**
     * 结束当前应用
     *
     * @param context
     */
    public void closeApp(Context context) {
        // 结束当前的activity
        ((VoiceActivity) context).finish();
        ((VoiceActivity) context).overridePendingTransition(0, 0);
    }

    /**
     * 开启连续对话模式
     *
     * @param context
     */
    public void startContinueChatMode(Context context) {
        SimpleWakeupListener.flag = false;
        FinishStatus.continueConversationMode = true;
        ((VoiceActivity) context).getSwitchButton().setChecked(true);
        // 切换图片
        Glide.with(context).load(R.drawable.audio).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(((VoiceActivity) context).getVoiceAssistantLogo());
    }

    /**
     * 关闭连续对话模式
     * @param context
     */
    public void closeContinueChatMode(Context context) {
        SimpleWakeupListener.flag = true;
        FinishStatus.continueConversationMode = false;
        ((VoiceActivity) context).getSwitchButton().setChecked(false);
        // 切换图片
        Glide.with(context).load(R.drawable.voice_symble).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(((VoiceActivity) context).getVoiceAssistantLogo());
    }

    public void turnLeft(Context context) {
        Dialog.toastWithoutAppName((Activity) context, "向左的方法执行了");
    }

    public void turnRight(Context context) {
        Dialog.toastWithoutAppName((Activity) context, "向右的方法执行了");
    }

    /**
     * 没有该功能
     * @param context
     */
    public void nothingToDo(Context context) {

    }
}
