package com.xxxxxxH.netinfo.entity;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Copyright (C) 2021,2021/5/26, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public class DataEntity implements Parcelable {

    private String roomName = "";
    private String roomLoc = "";
    private String netName = "";
    private String boardName = "";
    private String portName = "";
    private String fiberName = "";
    private String scramblingId = "";
    private String childName = "";
    private String startTime = "";
    private String endTime = "";
    private String scramblingRate = "";
    private String scramblingCode = "";
    private String scramblingLoc = "";
    private HashMap<String, String> customRoom;
    private HashMap<String, String> customScrambling;
    private ArrayList<String> imgList;

    public DataEntity() {

    }

    public DataEntity(String roomName, String roomLoc, String netName, String boardName, String portName,
            String fiberName,
            String scramblingId, String childName, String startTime, String endTime, String scramblingRate,
            String scramblingCode, String scramblingLoc,
            HashMap<String, String> customRoom, HashMap<String, String> customScrambling, ArrayList<String> imgList) {
        this.roomName = roomName;
        this.roomLoc = roomLoc;
        this.netName = netName;
        this.boardName = boardName;
        this.portName = portName;
        this.fiberName = fiberName;
        this.scramblingId = scramblingId;
        this.childName = childName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.scramblingRate = scramblingRate;
        this.scramblingCode = scramblingCode;
        this.scramblingLoc = scramblingLoc;
        this.customRoom = customRoom;
        this.customScrambling = customScrambling;
        this.imgList = imgList;
    }

    protected DataEntity(Parcel in) {
        roomName = in.readString();
        roomLoc = in.readString();
        netName = in.readString();
        boardName = in.readString();
        portName = in.readString();
        fiberName = in.readString();
        scramblingId = in.readString();
        childName = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        scramblingRate = in.readString();
        scramblingCode = in.readString();
        scramblingLoc = in.readString();
        customRoom = in.readHashMap(HashMap.class.getClassLoader());
        customScrambling = in.readHashMap(HashMap.class.getClassLoader());
        imgList = in.readArrayList(ArrayList.class.getClassLoader());
    }

    public static final Creator<DataEntity> CREATOR = new Creator<DataEntity>() {
        @Override
        public DataEntity createFromParcel(Parcel in) {
            return new DataEntity(in);
        }

        @Override
        public DataEntity[] newArray(int size) {
            return new DataEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(roomName);
        dest.writeString(roomLoc);
        dest.writeString(netName);
        dest.writeString(boardName);
        dest.writeString(portName);
        dest.writeString(fiberName);
        dest.writeString(scramblingId);
        dest.writeString(childName);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(scramblingRate);
        dest.writeString(scramblingCode);
        dest.writeString(scramblingLoc);
        dest.writeMap(customRoom);
        dest.writeMap(customScrambling);
        dest.writeList(imgList);
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomLoc() {
        return roomLoc;
    }

    public String getNetName() {
        return netName;
    }

    public String getBoardName() {
        return boardName;
    }

    public String getPortName() {
        return portName;
    }

    public String getFiberName() {
        return fiberName;
    }

    public String getScramblingId() {
        return scramblingId;
    }

    public String getChildName() {
        return childName;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getScramblingRate() {
        return scramblingRate;
    }

    public String getScramblingCode() {
        return scramblingCode;
    }

    public String getScramblingLoc() {
        return scramblingLoc;
    }

    public HashMap<String, String> getCustomRoom() {
        return customRoom;
    }

    public void setCustomRoom(HashMap<String, String> customRoom) {
        this.customRoom = customRoom;
    }

    public HashMap<String, String> getCustomScrambling() {
        return customScrambling;
    }

    public void setCustomScrambling(HashMap<String, String> customScrambling) {
        this.customScrambling = customScrambling;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setRoomLoc(String roomLoc) {
        this.roomLoc = roomLoc;
    }

    public void setNetName(String netName) {
        this.netName = netName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public void setFiberName(String fiberName) {
        this.fiberName = fiberName;
    }

    public void setScramblingId(String scramblingId) {
        this.scramblingId = scramblingId;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setScramblingRate(String scramblingRate) {
        this.scramblingRate = scramblingRate;
    }

    public void setScramblingCode(String scramblingCode) {
        this.scramblingCode = scramblingCode;
    }

    public void setScramblingLoc(String scramblingLoc) {
        this.scramblingLoc = scramblingLoc;
    }

    public ArrayList<String> getImgList() {
        return imgList;
    }

    public void setImgList(ArrayList<String> imgList) {
        this.imgList = imgList;
    }
}
