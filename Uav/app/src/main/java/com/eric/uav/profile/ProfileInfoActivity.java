package com.eric.uav.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.eric.uav.R;
import com.eric.uav.Settings;

public class ProfileInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView back;
    private WebView webView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        // 供存储使用
        sharedPreferences = getSharedPreferences("register", MODE_PRIVATE);

        back = findViewById(R.id.finish_this_activity);
        back.setOnClickListener(this);

        webView = findViewById(R.id.web_view);
        // 防止WebView打开浏览器
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);    // 支持javascript
        webView.loadUrl("http://" + Settings.ServerHost + ":" + Settings.ServerPort + "/userManage/user_info_app?id=" +
                sharedPreferences.getString("id", "0"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.finish_this_activity: {
                finish();
            }
            break;
            default:
                break;
        }
    }
}
