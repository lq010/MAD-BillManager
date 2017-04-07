package com.mobile.madassignment.models;

/**
 * Created by lq on 05/04/2017.
 */

public class Expense {

    private double cost;
    private String type;
    private String description;

    public Expense() {
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
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
}
