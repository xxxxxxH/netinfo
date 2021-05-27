package com.xxxxxxH.netinfo.sendmain;

import javax.mail.PasswordAuthentication;

/**
 * Copyright (C) 2021,2021/5/21, a Tencent company. All rights reserved.
 *
 * User : v_xhangxie
 *
 * Desc :
 */
public class MyAuthenticator extends javax.mail.Authenticator{
    private String strUser;
    private String strPwd;
    public MyAuthenticator(String user, String password) {
        this.strUser = user;
        this.strPwd = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(strUser, strPwd);
    }
}
