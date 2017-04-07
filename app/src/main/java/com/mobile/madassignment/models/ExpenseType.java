package com.mobile.madassignment.models;

/**
 * Created by lq on 04/04/2017.
 */

public class ExpenseType {
    private int resId;
    private String name;
    private boolean selected;

    public ExpenseType(String name, int resId) {
        this.name = name;
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
