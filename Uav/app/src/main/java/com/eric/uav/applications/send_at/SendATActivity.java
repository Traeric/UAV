package com.eric.uav.applications.send_at;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.eric.uav.R;
import com.eric.uav.utils.Dialog;

import java.util.LinkedList;
import java.util.List;

public class SendATActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView closeActivity;
    private EditText atCmd;
    private ImageView sendBtn;

    private RecyclerView cmdDisplay;
    private List<String> cmdList = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_at);

        closeActivity = findViewById(R.id.close_activity);
        closeActivity.setOnClickListener(this);

        atCmd = findViewById(R.id.at_cmd);
        sendBtn = findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(this);

        cmdDisplay = findViewById(R.id.display_panel);
        // 设置输入框随键盘上移
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);    // 设置此项，当软键盘弹出时，布局会自动顶上去
        cmdDisplay.setLayoutManager(linearLayoutManager);
//        cmdDisplay.setLayoutManager(new LinearLayoutManager(this));
        cmdDisplay.setAdapter(new CommandDisplayAdapter(this, cmdList));
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
                cmdList.add(cmd);
                cmdDisplay.setAdapter(new CommandDisplayAdapter(this, cmdList));
                // 清空输入框
                atCmd.setText("");
            }
            break;
            default:
                break;
        }
    }
}
