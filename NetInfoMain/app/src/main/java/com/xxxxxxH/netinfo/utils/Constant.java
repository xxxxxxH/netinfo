package com.xxxxxxH.netinfo.utils;

import android.content.Context;

import com.xxxxxxH.netinfo.widget.CustomItem;

import java.util.ArrayList;
import java.util.HashMap;

public class Constant {
    public static final String Longitude = "Longitude";
    public static final String Latitude = "Latitude";
    public static final String KEY_ROOM_NAME = "KEY_ROOM_NAME";
    public static final String KEY_SCRAM_ID = "KEY_SCRAM_ID";
    public static final String KEY_TOUR_ID = "KEY_TOUR_ID";
    public static final String KEY_EMAIL = "KEY_EMAIL";
    public static ArrayList<String> imgRoomList = null;
    public static ArrayList<String> imgScramblingList = null;
    public static ArrayList<CustomItem> itemList = null;
    public static ArrayList<CustomItem> itemList2 = null;
    public static ArrayList<CustomItem> itemList3 = null;
    public static final String FLAG_IMG = "img";
    public static final String FLAG_NAME = "name";
    public static final String FLAG_NET_INFO = "FLAG_NET_INFO";
    public static HashMap<String, String> customItem = null;
    public static HashMap<String, String> customItem2 = null;
    public static HashMap<String, String> customItem3 = null;
    public static Context Context = null;
    public static boolean ADD = false;
    public static String TO = "1758053745@qq.com";
    public static String FROM = "425270071@qq.com";
    public static String pwd = "tlvxychqlhgvbhbd";
//    public static String TO = "xiahua2@huawei.com";
//    public static String FROM = "xiahuan1017@163.com";
//    public static String pwd = "ZJTEZZVTXQGFTQAL";
    //debug  37:B9:52:B4:73:46:A8:BC:A0:A5:FF:37:E6:2B:97:C1:CD:EB:A6:11
    //release 81:56:4B:E7:23:74:F9:6C:DC:C5:34:8F:70:08:55:5A:B1:1C:D2:60
    //路由区分使用
    public static final String ROUTER_KEY = "ROUTER_KEY";
    public static final String TYPE_NET = "NET";
    public static final String TYPE_SCRAM = "SCRAM";
    public static final String TYPE_TOUR = "TOUR";
}
