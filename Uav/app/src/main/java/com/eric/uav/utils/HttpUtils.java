package com.eric.uav.utils;

import android.app.Activity;

import com.eric.uav.Settings;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class HttpUtils {
    private Activity currentActivity;

    public HttpUtils(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    /**
     * Http Post请求
     *
     * @param path
     * @param param
     * @return
     */
    public void sendPost(String path, Map<String, String> param) {
        // 处理url
        path = "http://" + Settings.ServerHost + ":" + Settings.ServerPort + path;

        // 开一个线程单独执行
        String finalPath = path;
        new Thread(() -> {
            try {
                URL url = new URL(finalPath);
                // 获取conection对象
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // 设置请求方式
                connection.setRequestMethod("POST");
                connection.setDoOutput(true); // 允许写出
                connection.setDoInput(true);  // 允许读入
                connection.setUseCaches(false); // 不使用缓存
                // 连接
                connection.connect();

                // 设置参数
                StringBuilder body = new StringBuilder();
                for (Map.Entry<String, String> it : param.entrySet()) {
                    body.append("&").append(it.getKey()).append("=").append(it.getValue());
                }
                // 去掉最前面的&
                String res = body.toString().substring(1);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
                writer.write(res);
                writer.close();

                // 得到响应码
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 得到响应流
                    InputStream inputStream = connection.getInputStream();
                    // 请求成功，执行回调
                    HttpUtils.this.callback(inputStream2Stream(inputStream));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static String sendGet() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = "http://192.168.42.29:8000/userManage/test";
                    URL url = new URL(path);
                    //得到connection对象。
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //设置请求方式
                    connection.setRequestMethod("GET");
                    //连接
                    connection.connect();
                    //得到响应码
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        //得到响应流
                        InputStream inputStream = connection.getInputStream();
                        System.err.println(inputStream2Stream(inputStream));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return null;
    }

    public static String inputStream2Stream(InputStream inputStream) {
        StringBuilder result = new StringBuilder();
        byte[] data = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(data)) != -1) {
                result.append(new String(data, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public abstract void callback(String result);
}


