package com.mobile.madassignment.models;

/**
 * Created by lq on 05/05/2017.
 */

public class UpdateExpenseListEvent {
//    public final String expenseNum;
//
//    public UpdateExpenseListEvent(int expenseNum) {
//        this.expenseNum = expenseNum;
//    }
public final String balanceId;

    public UpdateExpenseListEvent(String balanceId) {
        this.balanceId = balanceId;
    }
}
