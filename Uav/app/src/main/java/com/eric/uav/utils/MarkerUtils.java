package com.eric.uav.utils;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.eric.uav.R;

import java.util.List;

public class MarkerUtils {
    /**
     * 画无人机的定位点
     * @param latLng
     * @param activity
     * @param aMap
     */
    public static Marker drawUavMarker(LatLng latLng, Activity activity, AMap aMap) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("无人机位置").snippet(String.format("无人机: %s, %s", latLng.latitude, latLng.longitude));
        // 设置marker的显示图标
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
                BitmapFactory.decodeResource(activity.getResources(), R.drawable.uav_marker)));
        return aMap.addMarker(markerOptions);
    }

    /**
     * 画普通点位点
     * @param latLng
     * @param aMap
     */
    public static void drawClickMarker(LatLng latLng, AMap aMap) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("点击位置").snippet(String.format("%s, %s", latLng.latitude, latLng.longitude));
        aMap.addMarker(markerOptions);
    }
}



