package com.mobile.madassignment.models;

import java.util.Date;

/**
 * Created by lq on 05/04/2017.
 */

public class Expense {

    private float cost;
    private String type;
    private String description;
    private String payer;

    //private Timestamp timestamp;
    private long createTime;

    public Expense() {
        this.createTime = new Date().getTime();
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public long getCreateTime() {
        return createTime;
    }
}
