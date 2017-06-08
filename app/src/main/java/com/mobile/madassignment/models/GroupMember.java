package com.mobile.madassignment.models;

import java.text.DecimalFormat;

import static com.mobile.madassignment.util.Constants.Default_Photo;

/**
 * Created by lq on 13/04/2017.
 */

public class GroupMember extends UserInfo{

    private float spending;
    private float payed;
    private float balance;
    boolean isParticipant;

    public GroupMember(UserInfo userInfo) {
        super(userInfo.getId(), userInfo.getName(), userInfo.getEmail(), userInfo.getProfilePhoto());
    }

    public GroupMember(){

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

    public float getBalance() {
        return this.balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public boolean isParticipant() {
        return isParticipant;
    }

    public void setParticipant(boolean participant) {
        isParticipant = participant;
    }

    public void reset(){
        this.spending = 0;
        this.payed = 0;
        this.balance = 0;
    }




    public boolean isPhotoExist(){
        if(!super.getProfilePhoto().matches(Default_Photo))
            return true;
        else
            return  false;
    }

}
