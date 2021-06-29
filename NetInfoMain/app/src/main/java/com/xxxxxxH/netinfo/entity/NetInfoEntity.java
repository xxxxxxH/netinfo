package com.xxxxxxH.netinfo.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class NetInfoEntity {
    private String roomName = "";//机房名称
    private String roomLoc = "";//网元坐标
    private String netName = "";//网元名称
    private String point = "";//据点信息
    private HashMap<String, String> customRoom;//自定义字段
    private ArrayList<String> imgRoomList;//现场图片
    private HashMap<String, ArrayList<BoardDetailsEntity>> netDetails;//网元详细信息

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomLoc() {
        return roomLoc;
    }

    public void setRoomLoc(String roomLoc) {
        this.roomLoc = roomLoc;
    }

    public String getNetName() {
        return netName;
    }

    public void setNetName(String netName) {
        this.netName = netName;
    }

    public HashMap<String, String> getCustomRoom() {
        return customRoom;
    }

    public void setCustomRoom(HashMap<String, String> customRoom) {
        this.customRoom = customRoom;
    }

    public ArrayList<String> getImgRoomList() {
        return imgRoomList;
    }

    public void setImgRoomList(ArrayList<String> imgRoomList) {
        this.imgRoomList = imgRoomList;
    }

    public HashMap<String, ArrayList<BoardDetailsEntity>> getNetDetails() {
        return netDetails;
    }

    public void setNetDetails(HashMap<String, ArrayList<BoardDetailsEntity>> netDetails) {
        this.netDetails = netDetails;
    }
}
