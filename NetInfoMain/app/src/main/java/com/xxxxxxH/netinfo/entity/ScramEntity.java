package com.xxxxxxH.netinfo.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class ScramEntity {
    private String scramblingId = "";
    private String childName = "";
    private String startTime = "";
    private String endTime = "";
    private String scramblingRate = "";
    private String scramblingCode = "";
    private String scramblingLoc = "";
    private HashMap<String, String> customScrambling;
    private ArrayList<String> imgScramblingList;//现场图片

    public String getScramblingId() {
        return scramblingId;
    }

    public void setScramblingId(String scramblingId) {
        this.scramblingId = scramblingId;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getScramblingRate() {
        return scramblingRate;
    }

    public void setScramblingRate(String scramblingRate) {
        this.scramblingRate = scramblingRate;
    }

    public String getScramblingCode() {
        return scramblingCode;
    }

    public void setScramblingCode(String scramblingCode) {
        this.scramblingCode = scramblingCode;
    }

    public String getScramblingLoc() {
        return scramblingLoc;
    }

    public void setScramblingLoc(String scramblingLoc) {
        this.scramblingLoc = scramblingLoc;
    }

    public HashMap<String, String> getCustomScrambling() {
        return customScrambling;
    }

    public void setCustomScrambling(HashMap<String, String> customScrambling) {
        this.customScrambling = customScrambling;
    }

    public ArrayList<String> getImgScramblingList() {
        return imgScramblingList;
    }

    public void setImgScramblingList(ArrayList<String> imgScramblingList) {
        this.imgScramblingList = imgScramblingList;
    }
}
