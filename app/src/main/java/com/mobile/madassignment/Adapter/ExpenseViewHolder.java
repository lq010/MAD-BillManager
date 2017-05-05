package com.mobile.madassignment.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.madassignment.R;

/**
 * Created by lq on 12/04/2017.
 */

public  class ExpenseViewHolder extends RecyclerView.ViewHolder {
    public TextView type;
    public ImageView typeImgView;
    public TextView cost;
    public TextView payer;
    public TextView createTime;
    //public CircleImageView messengerImageView;

    public ExpenseViewHolder(View v) {
        super(v);
        type = (TextView) itemView.findViewById(R.id.tv_item_item_type);
        typeImgView = (ImageView) itemView.findViewById(R.id.iv_expense_type_img);
        cost = (TextView) itemView.findViewById(R.id.tv_item_item_cost);
        payer = (TextView) itemView.findViewById(R.id.tv_payer);
        createTime = (TextView) v.findViewById(R.id.tv_expense_date);
    }
}