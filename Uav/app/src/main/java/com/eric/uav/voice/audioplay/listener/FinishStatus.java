package com.eric.uav.voice.audioplay.listener;

// 状态类
public class FinishStatus {
    // 完成语音播放后要做的事情
    public static int finishAudioPlay = 0;

    // 语音播放完成后
    public static int START_AUDIO_DISTINGUISH = 0;   // 开启语音识别
    public static int NOTHING_TO_DO = 1;   // 什么也不做
    public static int CLOSE_APP = 2;   // 关闭应用
    public static int CONTINUE_CONVERSATION = 3;  // 开启连续对话模式
    public static int CLOSE_CONTINUE_CONVERSATION = 4;  // 关闭连续对话模式

    // 连续对话模式
    public static boolean continueConversationMode = false;
}
