package com.eric.uav.applications.voice.audioplay.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 状态类
public class FinishStatus {
    // 完成语音播放后要做的事情
    public static String finishAudioPlay = "nothingToDo";

    // 连续对话模式
    public static boolean continueConversationMode = false;

    // 语音执行的关键字以及对应要执行的方法
    public static Map<List<String>, Map<String, String>> keyWordMap = new ConcurrentHashMap<>();

    static {
        List<String> list = new ArrayList<>();
        list.add("关闭");
        list.add("应用");
        Map<String, String> value = new HashMap<>();
        value.put("function", "closeApp");
        value.put("voiceTips", "好的");
        keyWordMap.put(list, value);

        list = new ArrayList<>();
        list.add("开启");
        list.add("连续对话");
        value = new HashMap<>();
        value.put("function", "startContinueChatMode");
        value.put("voiceTips", "好的");
        keyWordMap.put(list, value);

        list = new ArrayList<>();
        list.add("关闭");
        list.add("连续对话");
        value = new HashMap<>();
        value.put("function", "closeContinueChatMode");
        value.put("voiceTips", "好的");
        keyWordMap.put(list, value);
    }
}
