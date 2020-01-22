package com.eric.uav.applications.voice.recog.event_handler;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;

import com.eric.uav.applications.voice.VoiceActivity;
import com.eric.uav.applications.voice.audioplay.listener.FinishStatus;

import java.util.List;
import java.util.Map;

public class SimpleEventHandler extends EventHandler {

    public SimpleEventHandler(Context context) {
        super(context);
    }

    public SimpleEventHandler() {}

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void before(String word) {
        boolean target = false;
        for (Map.Entry<List<String>, Map<String, String>> entry : FinishStatus.keyWordMap.entrySet()) {
            boolean flag = true;
            for (String keyWord : entry.getKey()) {
                if (!word.contains(keyWord)) {
                    // 不包含该关键字
                    flag = false;
                    break;
                }
            }
            if (flag) {
                // 如果全部包含了
                FinishStatus.finishAudioPlay = entry.getValue().get("function");
                // 播放语音
                ((VoiceActivity) context).getSynthesizer().speak(entry.getValue().get("voiceTips"));
                // 匹配成功
                target = true;
                break;
            }
        }
        // 没有匹配到功能
        if (!target) {
            FinishStatus.finishAudioPlay = "nothingToDo";
            ((VoiceActivity) context).getSynthesizer().speak("小则还没有学习该功能，请到控制台帮助小则学习吧。");
        }
    }
}
