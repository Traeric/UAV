package com.eric.uav.applications.voice.audioplay.listener;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.eric.uav.applications.voice.VoiceActivity;
import com.eric.uav.applications.voice.audioplay.MainHandlerConstant;
import com.eric.uav.applications.voice.recog.event_handler.EventHandler;
import com.eric.uav.applications.voice.recog.event_handler.SimpleEventHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * SpeechSynthesizerListener 简单地实现，仅仅记录日志
 * Created by fujiayi on 2017/5/19.
 */

public class MessageListener implements SpeechSynthesizerListener, MainHandlerConstant {
    private static final String TAG = "MessageListener";

    private Context context;

    public MessageListener(Context context) {
        this.context = context;
    }

    /**
     * 播放开始，每句播放开始都会回调
     *
     * @param utteranceId
     */
    @Override
    public void onSynthesizeStart(String utteranceId) {
        sendMessage("准备开始合成,序列号:" + utteranceId);
    }

    /**
     * 语音流 16K采样率 16bits编码 单声道 。
     *
     * @param utteranceId
     * @param bytes       二进制语音 ，注意可能有空data的情况，可以忽略
     * @param progress    如合成“百度语音问题”这6个字， progress肯定是从0开始，到6结束。 但progress无法和合成到第几个字对应。
     *                    engineType 下版本提供。1:音频数据由离线引擎合成； 0：音频数据由在线引擎（百度服务器）合成。
     */

    public void onSynthesizeDataArrived(String utteranceId, byte[] bytes, int progress) {
        Log.i(TAG, "合成进度回调, progress：" + progress + ";序列号:" + utteranceId);
        // + ";" + (engineType == 1? "离线合成":"在线合成"));
    }

    @Override
    // engineType 下版本提供。1:音频数据由离线引擎合成； 0：音频数据由在线引擎（百度服务器）合成。
    public void onSynthesizeDataArrived(String utteranceId, byte[] bytes, int progress, int engineType) {
        onSynthesizeDataArrived(utteranceId, bytes, progress);
    }

    /**
     * 合成正常结束，每句合成正常结束都会回调，如果过程中出错，则回调onError，不再回调此接口
     *
     * @param utteranceId
     */
    @Override
    public void onSynthesizeFinish(String utteranceId) {
        sendMessage("合成结束回调, 序列号:" + utteranceId);
    }

    @Override
    public void onSpeechStart(String utteranceId) {
        sendMessage("播放开始回调, 序列号:" + utteranceId);
    }

    /**
     * 播放进度回调接口，分多次回调
     *
     * @param utteranceId
     * @param progress    如合成“百度语音问题”这6个字， progress肯定是从0开始，到6结束。 但progress无法保证和合成到第几个字对应。
     */
    @Override
    public void onSpeechProgressChanged(String utteranceId, int progress) {
        //  Log.i(TAG, "播放进度回调, progress：" + progress + ";序列号:" + utteranceId );
    }

    /**
     * 播放正常结束，每句播放正常结束都会回调，如果过程中出错，则回调onError,不再回调此接口
     *
     * @param utteranceId
     */
    @Override
    public void onSpeechFinish(String utteranceId) {
        sendMessage("播放结束回调, 序列号:" + utteranceId);
        ((VoiceActivity) context).runOnUiThread(() -> {
            // 执行对应的方法
            Class<SimpleEventHandler> clazz = SimpleEventHandler.class;
            try {
                Object obj = clazz.newInstance();
                System.out.println(FinishStatus.finishAudioPlay);
                Method method = clazz.getMethod(FinishStatus.finishAudioPlay, Context.class);
                method.invoke(obj, context);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }


            if (FinishStatus.continueConversationMode) {
                // 连续对话模式
                Map<String, Object> map = new HashMap<>();
                map.put(SpeechConstant.ACCEPT_AUDIO_DATA, true); // 目前必须开启此回掉才能保存音频
                map.put(SpeechConstant.OUT_FILE,
                        Environment.getExternalStorageDirectory().toString() + "/UAVASR" + "/outfile.pcm");
                ((VoiceActivity) context).getRecognizer().start(map);
            }
        });
    }

    /**
     * 当合成或者播放过程中出错时回调此接口
     *
     * @param utteranceId
     * @param speechError 包含错误码和错误信息
     */
    @Override
    public void onError(String utteranceId, SpeechError speechError) {
        sendErrorMessage("错误发生：" + speechError.description + "，错误编码："
                + speechError.code + "，序列号:" + utteranceId);
    }

    private void sendErrorMessage(String message) {
        sendMessage(message, true);
    }


    private void sendMessage(String message) {
        sendMessage(message, false);
    }

    protected void sendMessage(String message, boolean isError) {
        if (isError) {
            Log.e(TAG, message);
        } else {
            Log.i(TAG, message);
        }

    }
}
