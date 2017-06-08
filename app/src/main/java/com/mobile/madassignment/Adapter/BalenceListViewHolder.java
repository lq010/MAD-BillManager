package com.mobile.madassignment.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.madassignment.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lq on 14/04/2017.
 */

public class BalenceListViewHolder extends RecyclerView.ViewHolder{
    public TextView name;
    public CircleImageView photo;
    public TextView cost;
    public Context context;

    public BalenceListViewHolder(View v) {
        super(v);
        name = (TextView) itemView.findViewById(R.id.tv_balance_user_name);
        photo = (CircleImageView) itemView.findViewById(R.id.iv_balance_userPhoto);
        cost = (TextView) itemView.findViewById(R.id.tv_balance_cost);

    }

    public Context getContext() {
        return context;
    }
}
