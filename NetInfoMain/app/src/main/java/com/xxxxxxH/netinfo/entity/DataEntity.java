package com.xxxxxxH.netinfo.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Copyright (C) 2021,2021/5/26, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public class DataEntity implements Parcelable {

    private final String roomName;
    private final String roomLoc;
    private final String netName;
    private final String boardName;
    private final String portName;
    private final String fiberName;
    private final String scramblingId;
    private final String childName;
    private final String startTime;
    private final String endTime;
    private final String scramblingRate;
    private final String scramblingCode;
    private final String scramblingLoc;
    private HashMap<String, String> customRoom;
    private HashMap<String, String> customScrambling;

    public DataEntity(String roomName, String roomLoc, String netName, String boardName, String portName, String fiberName,
                      String scramblingId, String childName, String startTime, String endTime, String scramblingRate, String scramblingCode, String scramblingLoc,
                      HashMap<String, String> customRoom, HashMap<String, String> customScrambling) {
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
    }
}
