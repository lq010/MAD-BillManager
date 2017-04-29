package com.mobile.madassignment.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mobile.madassignment.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lq on 12/04/2017.
 */

public class GroupMemberViewHolder extends RecyclerView.ViewHolder{
    public CircleImageView photo;
    public TextView name;
    public Context context;

    public GroupMemberViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        photo = (CircleImageView)itemView.findViewById(R.id.Iv_member_photo);
        name =(TextView)itemView.findViewById(R.id.tv_member_name);
    }

    public Context getContext() {
        return context;
    }
}
