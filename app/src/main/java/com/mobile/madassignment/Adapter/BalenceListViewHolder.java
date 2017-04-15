package com.mobile.madassignment.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.madassignment.R;

/**
 * Created by lq on 14/04/2017.
 */

public class BalenceListViewHolder extends RecyclerView.ViewHolder{
    public TextView name;
    public ImageView photo;
    public TextView cost;

    public BalenceListViewHolder(View v) {
        super(v);
        name = (TextView) itemView.findViewById(R.id.tv_balance_user_name);
        photo = (ImageView) itemView.findViewById(R.id.iv_balance_userPhoto);
        cost = (TextView) itemView.findViewById(R.id.tv_balance_cost);

    }
}