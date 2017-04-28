package com.mobile.madassignment.models;

import java.util.Date;
import java.util.Map;

/**
 * Created by lq on 05/04/2017.
 */

public class Group {
    private String name ;
    //private Timestamp timestamp;//firebase timestamp is a Map<String,String>
    private long createTime;

    private String currency;

    private Map<String,String> members;//<id,name>
    //private int  tempMenuId;


    public Group() {
        Date date = new Date();
        this.createTime = date.getTime();
    }

    public Group(String name, String currency, Map<String, String> members) {
        this.name = name;
        this.currency = currency;
        this.members = members;
        Date date = new Date();
        this.createTime = date.getTime();
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreateTime() {
        return createTime;
    }




    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Map<String, String> getMembers() {
        return members;
    }

    public void setMembers(Map<String, String> members) {
        this.members = members;
    }
}
