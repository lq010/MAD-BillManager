package com.mobile.madassignment.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.madassignment.R;
import com.mobile.madassignment.models.GroupMember;
import com.mobile.madassignment.util.DataFormat;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lq on 14/04/2017.
 */

public class BalanceListViewAdapter extends RecyclerView.Adapter<BalenceListViewHolder>{
    DataFormat df;
    private List<GroupMember> members = new ArrayList<>();

    private LayoutInflater inflater;



    public BalanceListViewAdapter(List<GroupMember> members, Context c) {
        this.members = members;
        this.inflater = inflater.from(c);
    }

    @Override
    public BalenceListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.balande_list_user_item,parent,false);

        return new BalenceListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BalenceListViewHolder holder, int position) {
        holder.name.setText(members.get(position).getName());
        GroupMember member =  members.get(position);

        float balance = member.getBalance();
        holder.cost.setText(df.myDFloatFormat(balance));
        if(balance > 0){
            holder.cost.setTextColor(Color.GREEN);
        }else if(balance<0){
            holder.cost.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }
}
