package com.eric.uav.utils;

import android.app.Activity;
import android.support.v7.app.AlertDialog;

import com.eric.uav.R;

public class Dialog {
    public static void tipsDialog(Activity activity, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setIcon(R.drawable.tips);
        builder.setPositiveButton("确定", (dialogInterface, i) -> {});
        builder.show();
    }


}



