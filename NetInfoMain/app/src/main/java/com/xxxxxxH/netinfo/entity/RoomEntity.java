package com.xxxxxxH.netinfo.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Copyright (C) 2021,2021/5/26, a Tencent company. All rights reserved.
 *
 * User : v_xhangxie
 *
 * Desc :
 */
public class RoomEntity implements Parcelable {

    private String name;

    public RoomEntity(String name){
        this.name = name;
    }

    protected RoomEntity(Parcel in) {
        name = in.readString();
    }

    public static final Creator<RoomEntity> CREATOR = new Creator<RoomEntity>() {
        @Override
        public RoomEntity createFromParcel(Parcel in) {
            return new RoomEntity(in);
        }

        @Override
        public RoomEntity[] newArray(int size) {
            return new RoomEntity[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}
