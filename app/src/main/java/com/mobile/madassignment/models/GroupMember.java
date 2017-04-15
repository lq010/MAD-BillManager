package com.mobile.madassignment.models;

import java.text.DecimalFormat;

/**
 * Created by lq on 13/04/2017.
 */

public class GroupMember {

    private String name;
    private float spending;
    private float payed;

    public GroupMember() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getSpending() {
        return spending;
    }

    public void setSpending(float spending) {
        this.spending = spending;
    }

    public float getPayed() {
        return payed;
    }

    public void setPayed(float payed) {
        this.payed = payed;
    }


}
