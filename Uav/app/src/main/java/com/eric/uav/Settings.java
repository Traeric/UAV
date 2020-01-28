package com.eric.uav;

import java.util.HashMap;
import java.util.Map;

public final class Settings {
    public static String ServerHost = "119.3.146.100";
//    public static String ServerHost = "192.168.0.106";
    public static String ServerPort = "8000";

    // 路由映射表
    public static Map<String, String> routerMap = new HashMap<>();

    static {
        routerMap.put("sendEmail", "/userManage/send_email/");
        routerMap.put("register", "/userManage/register/");
        routerMap.put("login", "/userManage/login/");
        routerMap.put("refreshLoginStatus", "/userManage/refresh_login_status/");
        routerMap.put("cancelLogin", "/userManage/cancel_login/");
        routerMap.put("confirmLogin", "/userManage/confirm_login/");
        routerMap.put("getKeyWord", "/userManage/app_get_user_key_word/");
        routerMap.put("uploadFile", "/userManage/upload_file/");
    }
}
