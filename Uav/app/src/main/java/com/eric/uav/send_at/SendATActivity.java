package com.eric.uav.send_at;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eric.uav.R;
import com.eric.uav.utils.Dialog;

public class SendATActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView closeActivity;
    private EditText atCmd;
    private ImageView sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_at);

        closeActivity = findViewById(R.id.close_activity);
        closeActivity.setOnClickListener(this);

        atCmd = findViewById(R.id.at_cmd);
        sendBtn = findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_activity: {
                this.finish();
                overridePendingTransition(0, 0);
            }
            break;
            case R.id.send_btn: {
                // 获取输入框中的信息
                String cmd = atCmd.getText().toString();
                if ("".equals(cmd.trim())) {
                    Dialog.tipsDialog(this, "指令不能为空！");
                    return;
                }
                // 将指令发送到界面中显示
                
                Toast.makeText(this, cmd, Toast.LENGTH_SHORT).show();
                // 清空输入框
                atCmd.setText("");
            }
            break;
            default:
                break;
        }
    }
}
