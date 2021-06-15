package com.xxxxxxH.netinfo.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Copyright (C) 2021,2021/6/2, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public class BoardDetailsEntity implements Parcelable {

    private String boardContent;
    private String portName;
    private String portContent;
    private String fiberName;
//    private String fiberRx;
//    private String fiberTx;

    public BoardDetailsEntity(){

    }

    public BoardDetailsEntity(String boardContent,String portName,String portContent,String fiberName,String fiberRx,String fiberTx){
        this.boardContent = boardContent;
        this.portName = portName;
        this.portContent = portContent;
        this.fiberName = fiberName;
//        this.fiberRx = fiberRx;
//        this.fiberTx = fiberTx;
    }

    protected BoardDetailsEntity(Parcel in) {
        boardContent = in.readString();
        portName = in.readString();
        portContent = in.readString();
        fiberName = in.readString();
//        fiberRx = in.readString();
//        fiberTx = in.readString();
    }

    public static final Creator<BoardDetailsEntity> CREATOR = new Creator<BoardDetailsEntity>() {
        @Override
        public BoardDetailsEntity createFromParcel(Parcel in) {
            return new BoardDetailsEntity(in);
        }

        @Override
        public BoardDetailsEntity[] newArray(int size) {
            return new BoardDetailsEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(boardContent);
        dest.writeString(portName);
        dest.writeString(portContent);
        dest.writeString(fiberName);
//        dest.writeString(fiberRx);
//        dest.writeString(fiberTx);
    }

    public String getBoardContent() {
        return boardContent;
    }

    public void setBoardContent(String boardContent) {
        this.boardContent = boardContent;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getPortContent() {
        return portContent;
    }

    public void setPortContent(String portContent) {
        this.portContent = portContent;
    }

    public String getFiberName() {
        return fiberName;
    }

    public void setFiberName(String fiberName) {
        this.fiberName = fiberName;
    }

//    public String getFiberRx() {
//        return fiberRx;
//    }

//    public void setFiberRx(String fiberRx) {
//        this.fiberRx = fiberRx;
//    }

//    public String getFiberTx() {
//        return fiberTx;
//    }

//    public void setFiberTx(String fiberTx) {
//        this.fiberTx = fiberTx;
//    }

    public static Creator<BoardDetailsEntity> getCREATOR() {
        return CREATOR;
    }
}
