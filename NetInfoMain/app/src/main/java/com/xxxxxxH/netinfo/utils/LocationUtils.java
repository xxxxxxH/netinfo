package com.xxxxxxH.netinfo.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;

/**
 * Copyright (C) 2021,2021/5/27, a Tencent company. All rights reserved.
 *
 * User : v_xhangxie
 *
 * Desc :
 */
public class LocationUtils {

    private static LocationManager manager;
    private final static String[] MULTI_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};


    public static void initData(Context context,Activity activity){
        if (manager == null){
            manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        boolean isGpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGpsEnabled) {
            //前往设置GPS页面
            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        } else {
            initLocationManager(context,activity);
        }

    }

    public static void initLocationManager(Context context, Activity activity) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, MULTI_PERMISSIONS, 100);
            return;
        }
        if (manager == null) {
            //获取定位服务
            manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            manager.addNmeaListener(mNmealocationListener, null);
        } else {
            manager.addNmeaListener(new GpsStatus.NmeaListener() {
                @Override
                public void onNmeaReceived(long timestamp, String nmea) {
                    // 低版本使用接口
                }
            });
        }
        // 设置监听器
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, (LocationListener) null);
        // 通过GPS获取位置
        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {

        }
    }

    @SuppressLint("NewApi")
    private static OnNmeaMessageListener mNmealocationListener = new OnNmeaMessageListener() {

        @Override
        public void onNmeaMessage(String message, long timestamp) {
            //GNGGA,071249.988,3209.5304,N,11841.5127,E,0,0,,72.0,M,4.9,M,,*53
            if (message.contains("GNGGA")) {
                String info[] = message.split(",");
                //GPGGA中altitude是MSL altitude(平均海平面)
                //UTC + (＋0800) = 本地（北京）时间
                int a = Integer.parseInt(info[1].substring(0, 2));
                a += 8;
                a %= 24;
                String time = "";
                String time1 = "";
                if (a < 10) {
                    time = "0" + a + info[1].substring(2, info[1].length() - 1);
                } else {
                    time = a + info[1].substring(2, info[1].length() - 1);
                }
                time1 = time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4, 6);
//                mLocNmeaTextView.setText("\nUTC时间：" + info[1] + "\n北京时间: " + time1
//                        + "\n纬度：" + info[3] + info[2] + "\n经度：" + info[5] + info[4]
//                        + "\nGPS状态：" + info[6] + "\n正在使用的卫星数量：" + info[7]
//                        + "\nHDOP水平精度因子：" + info[8] + "\n海拔高度：" + info[9] + info[10]
//                        + "\n水准面高度：" + info[11] + info[12]
//                        + "\n差分时间：" + info[13] + "\n差分站ID*异或校验值" + info[14]);
            }
        }
    };


}
