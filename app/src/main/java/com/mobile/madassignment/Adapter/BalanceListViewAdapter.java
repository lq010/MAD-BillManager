package com.mobile.madassignment.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.mobile.madassignment.R;
import com.mobile.madassignment.models.GroupMember;
import com.mobile.madassignment.util.DataFormat;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.IOException;
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
    public void onBindViewHolder(final BalenceListViewHolder holder, int position) {

        GroupMember member =  members.get(position);
        holder.name.setText(member.getName());
        float balance = member.getBalance();
        holder.cost.setText(df.myDFloatFormat(balance));
        if(balance > 0){
            holder.cost.setTextColor(Color.GREEN);
        }else if(balance<0){
            holder.cost.setTextColor(Color.RED);
        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

            Log.d(this.getClass().getName(),mAuth.getCurrentUser().getUid());
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://madassignment-1f6c6.appspot.com");
            StorageReference photoRef = storageRef.child(mAuth.getCurrentUser().getUid()+".jpg");
            try {
                final File localFile = File.createTempFile("images", "jpg");
                final long ONE_MEGABYTE = 1024*1024;
                photoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created

                        Picasso.with(holder.getContext()).load(localFile).into(holder.photo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        int errorCode = ((StorageException) exception).getErrorCode();
                        String errorMessage = exception.getMessage();
                        Log.d("userPhoto",errorMessage);
                    }
                });
            } catch (IOException e) {
                Log.d("userPhoto",e.getMessage());
            }

    }

    @Override
    public int getItemCount() {
        return members.size();
    }
}
