package com.mobile.madassignment.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by lq on 05/04/2017.
 */

public class Expense implements Serializable {
    private String id;
    private float cost;
    private String type;
    private String description;
    private String payer;
    private String status;
    private Map<String,String> participants;
    private String delete_reason;

    //private Timestamp timestamp;
    private long createTime;

    public Expense() {
        this.createTime = new Date().getTime();
        this.status = "valid";
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

    public Map<String, String> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, String> participants) {
        this.participants = participants;
    }
//    public  void addParticipant (String uid){
//        this.participants.put(uid,true);
//    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDelete_reason() {
        return delete_reason;
    }

    public void setDelete_reason(String delete_reason) {
        this.delete_reason = delete_reason;
    }

    @Exclude
    public  String getFormatDate(){
        Date date = new Date(createTime);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm dd-MMM-yyyy");
        return  df.format(date);
    }
}
