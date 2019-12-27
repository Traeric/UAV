package com.eric.uav.voice.recog.event_handler;

import android.content.Context;

import com.eric.uav.voice.VoiceActivity;
import com.eric.uav.voice.audioplay.listener.FinishStatus;

public class SimpleEventHandler extends EventHandler {
    public SimpleEventHandler(Context context) {
        super(context);
    }

    @Override
    protected void before(String word) {
        if (word.contains("关闭应用")) {
            FinishStatus.finishAudioPlay = 2;
            ((VoiceActivity) context).getSynthesizer().speak("好的");
        } else {
            FinishStatus.finishAudioPlay = 1;
            ((VoiceActivity) context).getSynthesizer().speak("你说的是" + word);
        }
    }
}
