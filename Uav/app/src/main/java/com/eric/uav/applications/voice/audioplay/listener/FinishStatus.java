package com.eric.uav.applications.voice.audioplay.listener;

import com.eric.uav.Settings;
import com.eric.uav.UvaApplication;
import com.eric.uav.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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
        // 添加本地关键字
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

        // 从远程主机获取用户其他的关键字
        Map<String, String> param = new HashMap<>();
        param.put("id", UvaApplication.id);

        HttpUtils httpUtils = new HttpUtils() {
            @Override
            public void callback(String result) {
                // 成功返回
                // 解析json
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        // 获取关键词
                        String[] keyWordArr = jsonObject.getString("key_word").split(",");
                        List<String> list = new ArrayList<>(Arrays.asList(keyWordArr));
                        Map<String, String> value = new HashMap<>();
                        value.put("function", jsonObject.getString("func_name"));
                        value.put("voiceTips", jsonObject.getString("feedback"));
                        value.put("code", jsonObject.getString("code"));
                        keyWordMap.put(list, value);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        httpUtils.sendPost(Settings.routerMap.get("getKeyWord"), param);
    }
}
