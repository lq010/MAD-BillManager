package com.mobile.madassignment.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobile.madassignment.R;
import com.mobile.madassignment.models.Group;
import com.mobile.madassignment.models.GroupMember;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lq on 12/04/2017.
 */

public class GroupMemberViewAdapter extends RecyclerView.Adapter<GroupMemberViewHolder> {

    private List<GroupMember> users = new ArrayList<>();

    private LayoutInflater inflater;



    public GroupMemberViewAdapter(List<GroupMember> users, Context c){
        this.inflater = LayoutInflater.from(c);
        this.users = users;
    }

    @Override
    public GroupMemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.member_name_item, parent, false);
        return new GroupMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GroupMemberViewHolder holder, int position) {
        GroupMember user = users.get(position);
        holder.name.setText(user.getName());
        if(user.isPhotoExist()){
            Log.d(this.getClass().getName(),user.getProfilePhoto());
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl("gs://madassignment-1f6c6.appspot.com");
            StorageReference photoRef = storageRef.child(user.getProfilePhoto()+".jpg");
            Log.d("profile", user.getProfilePhoto());///
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
                        // Handle any errors
                        //Toast.makeText(UserProfileActivity.this, "photo download failed",Toast.LENGTH_SHORT ).show();
                    }
                });
            } catch (IOException e) {
                Log.d("userPhoto",e.getMessage());
            }
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public List<GroupMember> getusers() {
        return users;
    }

    public void setUsers(List<GroupMember> users) {
        this.users = users;
    }


}
