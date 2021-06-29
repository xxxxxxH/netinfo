package com.xxxxxxH.netinfo.entity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Copyright (C) 2021,2021/6/29, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public class NetTourEntity {
    private String id;
    private String stake;
    private String point;
    private String loc;
    private ArrayList<String> imgList;
    private HashMap<String, String> custom;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStake() {
        return stake;
    }

    public void setStake(String stake) {
        this.stake = stake;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public ArrayList<String> getImgList() {
        return imgList;
    }

    public void setImgList(ArrayList<String> imgList) {
        this.imgList = imgList;
    }

    public HashMap<String, String> getCustom() {
        return custom;
    }

    public void setCustom(HashMap<String, String> custom) {
        this.custom = custom;
    }
}
