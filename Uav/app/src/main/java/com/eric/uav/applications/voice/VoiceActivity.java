package com.eric.uav.applications.voice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.eric.uav.R;
import com.eric.uav.applications.voice.audioplay.control.InitConfig;
import com.eric.uav.applications.voice.audioplay.control.MySyntherizer;
import com.eric.uav.applications.voice.audioplay.listener.FinishStatus;
import com.eric.uav.applications.voice.audioplay.listener.MessageListener;
import com.eric.uav.applications.voice.audioplay.utils.Auth;
import com.eric.uav.applications.voice.audioplay.utils.IOfflineResourceConst;
import com.eric.uav.applications.voice.audioplay.utils.OfflineResource;
import com.eric.uav.applications.voice.recog.MyRecognizer;
import com.eric.uav.applications.voice.recog.listener.IRecogListener;
import com.eric.uav.applications.voice.recog.listener.MessageStatusRecogListener;
import com.eric.uav.applications.voice.wakeup.MyWakeup;
import com.eric.uav.applications.voice.wakeup.listener.IWakeupListener;
import com.eric.uav.applications.voice.wakeup.listener.SimpleWakeupListener;
import com.suke.widget.SwitchButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VoiceActivity extends AppCompatActivity {
    private MyWakeup myWakeup;
    private MySyntherizer synthesizer;
    private MyRecognizer recognizer;

    private SwitchButton switchButton;

    private ImageView quitApp;

    protected String appId;

    protected String appKey;

    protected String secretKey;

    protected String sn; // 纯离线合成SDK授权码；离在线合成SDK免费，没有此参数
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； TtsMode.OFFLINE 纯离线合成，需要纯离线SDK
    protected TtsMode ttsMode = TtsMode.ONLINE;

    private ImageView voiceAssistantLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);

        quitApp = findViewById(R.id.quit_xiaoze);
        quitApp.setOnClickListener(view -> {
            VoiceActivity.this.finish();
            overridePendingTransition(0, 0);
        });

        voiceAssistantLogo = findViewById(R.id.voice_assistant_img);

        switchButton = findViewById(R.id.switch_button);
        switchButton.setOnCheckedChangeListener((view, isChacked) -> FinishStatus.continueConversationMode = isChacked);

        // 获取权限
        getAudioPermissions();
        getAudioCombinePermissions();

        // 监听唤醒识别的结果
        IWakeupListener listener = new SimpleWakeupListener(this);
        myWakeup = new MyWakeup(this, listener);
        // 启动唤醒
        Map<String, Object> params = new HashMap<>();
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        params.put(SpeechConstant.APP_ID, "18118235");
        myWakeup.start(params);

        // 语音识别部分
        // 初始化一个listener
        IRecogListener iRecogListener = new MessageStatusRecogListener(this);
        // 初始化myRecognizer
        recognizer = new MyRecognizer(this, iRecogListener);


        // 语音合成部分
        // 获取auth对象
        try {
            Auth.getInstance(this);
        } catch (Auth.AuthCheckException e) {
            return;
        }
        // 获取appId appKey secretKey
        appId = Auth.getInstance(this).getAppId();
        appKey = Auth.getInstance(this).getAppKey();
        secretKey = Auth.getInstance(this).getSecretKey();
        sn = Auth.getInstance(this).getSn(); // 纯离线合成必须有此参数；离在线合成SDK免费，没有此参数
        initialTts(); // 初始化TTS引擎
    }

    @Override
    protected void onDestroy() {
        // 退出事件管理器
        myWakeup.release();
        synthesizer.release();
        recognizer.release();
        SimpleWakeupListener.flag = true;
        FinishStatus.continueConversationMode = false;
        super.onDestroy();
    }

    /**
     * 获取语音识别的权限
     */
    private void getAudioPermissions() {
        String[] permissions = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                //进入到这里代表没有权限.
                toApplyList.add(perm);
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }
    }


    /**
     * 获取语音合成的权限
     */
    private void getAudioCombinePermissions() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
        };

        ArrayList<String> toApplyList = new ArrayList<>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.
            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }
    }


    /**
     * 初始化引擎，需要的参数均在InitConfig类里
     * <p>
     * DEMO中提供了3个SpeechSynthesizerListener的实现
     * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
     * UiMessageListener 在MessageListener的基础上，对handler发送消息，实现UI的文字更新
     * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
     */
    protected void initialTts() {
        LoggerProxy.printable(true); // 日志打印在logcat中
        // 设置初始化参数
        // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
        InitConfig config = getInitConfig();
        SpeechSynthesizerListener listener = new MessageListener(VoiceActivity.this);
        synthesizer = new MySyntherizer(this, config, listener);
    }


    protected InitConfig getInitConfig() {
        Map<String, String> params = getParams();
        // 添加你自己的参数
        InitConfig initConfig;
        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
        if (sn == null) {
            initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params);
        } else {
            initConfig = new InitConfig(appId, appKey, secretKey, sn, ttsMode, params);
        }
        return initConfig;
    }

    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return 合成参数Map
     */
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>, 其它发音人见文档
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-15 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "15");
        // 设置合成的语速，0-15 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");

        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // params.put(SpeechSynthesizer.PARAM_MIX_MODE_TIMEOUT, SpeechSynthesizer.PARAM_MIX_TIMEOUT_TWO_SECOND);
        // 离在线模式，强制在线优先。在线请求后超时2秒后，转为离线合成。

        // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
        OfflineResource offlineResource = createOfflineResource(OfflineResource.VOICE_DUYY_MODEL);
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                offlineResource.getModelFilename());
        return params;
    }

    protected OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(this, voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
        }
        return offlineResource;
    }

    public MySyntherizer getSynthesizer() {
        return synthesizer;
    }

    public ImageView getVoiceAssistantLogo() {
        return voiceAssistantLogo;
    }

    public MyRecognizer getRecognizer() {
        return recognizer;
    }

    public SwitchButton getSwitchButton() {
        return switchButton;
    }
}
