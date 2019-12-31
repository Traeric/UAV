package com.eric.uav.applications.voice.recog.event_handler;

import android.content.Context;

import com.eric.uav.applications.voice.VoiceActivity;
import com.eric.uav.applications.voice.audioplay.listener.FinishStatus;

public class SimpleEventHandler extends EventHandler {
    public SimpleEventHandler(Context context) {
        super(context);
    }

    @Override
    protected void before(String word) {
        if (word.contains("关闭") && word.contains("应用")) {
            FinishStatus.finishAudioPlay = FinishStatus.CLOSE_APP;
            ((VoiceActivity) context).getSynthesizer().speak("好的");
        } else if (word.contains("开启") && word.contains("连续对话")) {
            FinishStatus.finishAudioPlay = FinishStatus.CONTINUE_CONVERSATION;
            ((VoiceActivity) context).getSynthesizer().speak("好的");
        } else if (word.contains("关闭") && word.contains("连续对话")) {
            FinishStatus.finishAudioPlay = FinishStatus.CLOSE_CONTINUE_CONVERSATION;
            ((VoiceActivity) context).getSynthesizer().speak("好的");
        } else {
            FinishStatus.finishAudioPlay = FinishStatus.NOTHING_TO_DO;
            ((VoiceActivity) context).getSynthesizer().speak("你说的是" + word);
        }
    }
}
