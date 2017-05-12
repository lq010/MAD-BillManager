package com.mobile.madassignment.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.madassignment.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lq on 12/04/2017.
 */

public class GroupMemberViewAdapter extends RecyclerView.Adapter<GroupMemberViewHolder> {

    private List<String> names = new ArrayList<>();

    private LayoutInflater inflater;



    public GroupMemberViewAdapter(List<String> names, Context c){
        this.inflater = LayoutInflater.from(c);
        this.names = names;
    }

    @Override
    public GroupMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.member_name_item, parent, false);
        return new GroupMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GroupMemberViewHolder holder, int position) {
        String name = names.get(position);
        holder.name.setText(name);
//        if(name=="add_new_member"){
//            holder.photo.setImageResource(R.drawable.add);
////            holder.itemView.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    Intent intent = new Intent(holder.getContext(), AddGroupActivity.class);
////                    holder.getContext().startActivity(intent);
////                }
////            });
//        }

    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }


}
