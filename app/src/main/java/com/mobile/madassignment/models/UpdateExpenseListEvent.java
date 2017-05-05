package com.mobile.madassignment.models;

/**
 * Created by lq on 05/05/2017.
 */

public class UpdateExpenseListEvent {
    public final int expenseNum;

    public UpdateExpenseListEvent(int expenseNum) {
        this.expenseNum = expenseNum;
    }
}
