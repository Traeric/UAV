package com.eric.uav.applications.look_album.image_viewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.eric.uav.R;
import com.eric.uav.applications.look_album.DataTransform;

public class ImageViewerActivity extends AppCompatActivity {
    private ImageView imageView;
    private LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        imageView = findViewById(R.id.image_viewer_image);
        imageView.setImageBitmap(DataTransform.imageBitmap);

        linearLayout = findViewById(R.id.image_viewer_wrap);
        linearLayout.setOnClickListener(view -> {
            // 关闭当前的activity
            ImageViewerActivity.this.finish();
            overridePendingTransition(0, 0);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            // 用户按下了返回键
            this.finish();
            overridePendingTransition(0, 0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
