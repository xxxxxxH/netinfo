package com.xxxxxxH.netinfo.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtils {

    public static String formatDouble(double data) {
        return new DecimalFormat("#.0000").format(data);
    }

    public static String formatDate(Date date){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

}
