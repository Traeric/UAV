package com.eric.uav.utils;

import android.app.Activity;

import com.eric.uav.Settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public abstract class HttpUtils {
    private Activity currentActivity;

    private static final int TIME_OUT = 8 * 1000;                          //超时时间
    private static final String CHARSET = "utf-8";                         //编码格式
    private static final String PREFIX = "--";                            //前缀
    private static final String BOUNDARY = UUID.randomUUID().toString();  //边界标识 随机生成
    private static final String CONTENT_TYPE = "multipart/form-data";     //内容类型
    private static final String LINE_END = "\r\n";                        //换行

    public HttpUtils(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public HttpUtils() {
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
        new Thread(() -> {
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

    /**
     * 上传文件
     * @param path
     * @param fileParams
     * @param param
     */
    public void transformFile(String path, final Map<String, File> fileParams, Map<String, String> param) {
        // 处理url
        path = "http://" + Settings.ServerHost + ":" + Settings.ServerPort + path;

        // 开一个线程单独执行
        String finalPath = path;
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(finalPath);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setReadTimeout(TIME_OUT);
                conn.setConnectTimeout(TIME_OUT);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);//Post 请求不能使用缓存
                //设置请求头参数
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setRequestProperty("Content-Type", CONTENT_TYPE+";boundary=" + BOUNDARY);
                /**
                 * 请求体
                 */
                //上传参数
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                //getStrParams()为一个
                dos.writeBytes( getStrParams(param).toString() );
                dos.flush();

                //文件上传
                StringBuilder fileSb = new StringBuilder();
                for (Map.Entry<String, File> fileEntry: fileParams.entrySet()){
                    fileSb.append(PREFIX)
                            .append(BOUNDARY)
                            .append(LINE_END)
                            /**
                             * 这里重点注意： name里面的值为服务端需要的key 只有这个key 才可以得到对应的文件
                             * filename是文件的名字，包含后缀名的 比如:abc.png
                             */
                            .append("Content-Disposition: form-data; name=\"file\"; filename=\""
                                    + fileEntry.getKey() + "\"" + LINE_END)
                            // .append("Content-Type: image/jpg" + LINE_END) //此处的ContentType不同于 请求头 中Content-Type
                            .append("Content-Transfer-Encoding: 8bit" + LINE_END)
                            .append(LINE_END);// 参数头设置完以后需要两个换行，然后才是参数内容
                    dos.writeBytes(fileSb.toString());
                    dos.flush();
                    InputStream is = new FileInputStream(fileEntry.getValue());
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = is.read(buffer)) != -1){
                        dos.write(buffer,0,len);
                    }
                    is.close();
                    dos.writeBytes(LINE_END);
                }
                //请求结束标志
                dos.writeBytes(PREFIX + BOUNDARY + PREFIX + LINE_END);
                dos.flush();
                dos.close();
                //读取服务器返回信息
                if (conn.getResponseCode() == 200) {
                    HttpUtils.this.callback(inputStream2Stream(conn.getInputStream()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (conn!=null){
                    conn.disconnect();
                }
            }
        }).start();
    }

    /**
     * 对post参数进行编码处理
     */
    private static StringBuilder getStrParams(Map<String, String> strParams) {
        StringBuilder strSb = new StringBuilder();
        for (Map.Entry<String, String> entry : strParams.entrySet()) {
            strSb.append(PREFIX)
                    .append(BOUNDARY)
                    .append(LINE_END)
                    .append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END)
                    .append("Content-Type: text/plain; charset=" + CHARSET + LINE_END)
                    .append("Content-Transfer-Encoding: 8bit" + LINE_END)
                    .append(LINE_END)// 参数头设置完以后需要两个换行，然后才是参数内容
                    .append(entry.getValue())
                    .append(LINE_END);
        }
        return strSb;
    }
}


