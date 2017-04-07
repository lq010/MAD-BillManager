package com.mobile.madassignment.models;

import android.util.ArraySet;

import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by lq on 05/04/2017.
 */

public class Group {
    private String name ;
    private Map<String, String> timestamp;
    private String type;
    private String currency;
    private ArraySet<String> members;
    //private int  tempMenuId;


    public Group() {
    }

    public Group(String name, Map<String, String> timestamp, String currency, String type) {
        this.name = name;
        this.timestamp = timestamp;
        this.currency = currency;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public ArraySet<String> getMembers() {
        return members;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimestamp(Map<String, String> timestamp) {
        this.timestamp = timestamp;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMembers(ArraySet<String> members) {
        this.members = members;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

//    public int getTempMenuId() {
//        return tempMenuId;
//    }
//
//    public void setTempMenuId(int tempMenuId) {
//        this.tempMenuId = tempMenuId;
//    }

}
