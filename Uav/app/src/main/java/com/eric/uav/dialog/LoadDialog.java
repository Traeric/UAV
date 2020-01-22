package com.eric.uav.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.eric.uav.R;

public class LoadDialog extends Dialog {
    private ImageView loadIcon;
    private TextView title, content;
    private String tTitle, tContent;

    public LoadDialog(@NonNull Context context) {
        super(context);
    }

    public LoadDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_dialog);     // 设置布局

        // 设置宽度
        WindowManager m = getWindow().getWindowManager();
        Display display = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        display.getSize(size);
        p.width = (int) (size.x * 0.8);
        getWindow().setAttributes(p);   // 设置dialog的宽度为当前手机屏幕的宽度

        title = findViewById(R.id.title);
        content = findViewById(R.id.content);

        // 设置内容
        if (tTitle != null) {
            title.setText(tTitle);
        }

        if (tContent != null) {
            content.setText(tContent);
        }

        loadIcon = findViewById(R.id.load_icon);
        Glide.with(getContext()).load(R.drawable.load).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(loadIcon);
    }

    public LoadDialog settTitle(String tTitle) {
        this.tTitle = tTitle;
        return this;
    }

    public LoadDialog settContent(String tContent) {
        this.tContent = tContent;
        return this;
    }
}
