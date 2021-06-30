package com.xxxxxxH.netinfo.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Copyright (C) 2021,2021/6/30, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public class RouterUtils {

    private static RouterUtils instance = null;

    private RouterUtils(){

    }

    public static RouterUtils getInstance(){
        if (instance == null){
            instance = new RouterUtils();
        }
        return instance;
    }

    public void router(Context context,Class target,String key, String value){
        Bundle bundle = new Bundle();
        bundle.putString(key,value);
        Intent intent = new Intent(context,target);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

}
