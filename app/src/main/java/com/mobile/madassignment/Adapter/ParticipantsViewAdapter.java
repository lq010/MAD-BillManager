package com.mobile.madassignment.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.madassignment.R;
import com.mobile.madassignment.models.GroupMember;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lq on 08/06/2017.
 */

public class ParticipantsViewAdapter extends RecyclerView.Adapter<ParticipantsViewHolder>{

    private List<GroupMember> users;
    Context context;

    public ParticipantsViewAdapter(List<GroupMember> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @Override
    public ParticipantsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.participants_switch, parent, false);
        return new ParticipantsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ParticipantsViewHolder holder, int position) {
        holder.setData(users.get(position), context);
        if(position!=0) return;
        final Map<View,String[]> map = new HashMap<>();
        map.put(holder.switchButton,new String[]{"Partecipants","You can choose who you want to share the expense "});
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
