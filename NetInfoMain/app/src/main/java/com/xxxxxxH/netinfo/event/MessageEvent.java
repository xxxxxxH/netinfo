package com.xxxxxxH.netinfo.event;

/**
 * Copyright (C) 2021,2021/6/30, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public class MessageEvent {

    public final String[] message;

    public MessageEvent(String... message) {
        this.message = message;
    }

    public String[] getMessage() {
        return message;
    }
}
