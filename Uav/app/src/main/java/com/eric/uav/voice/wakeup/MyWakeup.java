package com.eric.uav.voice.wakeup;

import android.content.Context;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.eric.uav.voice.wakeup.listener.IWakeupListener;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by fujiayi on 2017/6/20.
 */

public class MyWakeup {
    private static boolean isInited = false;

    private EventManager wp;
    private EventListener eventListener;

    private Context context;

    private static final String TAG = "MyWakeup";

    public MyWakeup(Context context, EventListener eventListener) {
        if (isInited) {
            throw new RuntimeException("还未调用release()，请勿新建一个新类");
        }
        isInited = true;
        this.eventListener = eventListener;
        wp = EventManagerFactory.create(context, "wp");
        wp.registerListener(eventListener);

        this.context = context;
    }

    public MyWakeup(Context context, IWakeupListener eventListener) {
        this(context, new WakeupEventAdapter(eventListener));
    }

    /**
     * 启动唤醒
     * @param params
     */
    public void start(Map<String, Object> params) {
        String json = new JSONObject(params).toString();
        wp.send(SpeechConstant.WAKEUP_START, json, null, 0, 0);
    }

    /**
     * 停止唤醒
     */
    public void stop() {
//        MyLogger.info(TAG, "唤醒结束");
        wp.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void setEventListener(IWakeupListener eventListener) {
        this.eventListener =  new WakeupEventAdapter(eventListener);
    }

    /**
     * 释放唤醒的类
     */
    public void release() {
        stop();
        wp.unregisterListener(eventListener);
        wp = null;
        isInited = false;
    }
}
