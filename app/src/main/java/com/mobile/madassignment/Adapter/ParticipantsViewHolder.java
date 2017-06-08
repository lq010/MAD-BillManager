package com.mobile.madassignment.Adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.mobile.madassignment.R;
import com.mobile.madassignment.models.GroupMember;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lq on 08/06/2017.
 */

public class ParticipantsViewHolder extends RecyclerView.ViewHolder{
    CircleImageView img_profile;
    TextView name;
    public SwitchCompat switchButton;
    Context context;

    public ParticipantsViewHolder(View itemView) {
        super(itemView);
        this.img_profile = (CircleImageView) itemView.findViewById(R.id.iv_person_image);
        this.name = (TextView) itemView.findViewById(R.id.tv_name_member);
        this.switchButton = (SwitchCompat) itemView.findViewById(R.id.switch_button);
    }


    public CircleImageView getImg_profile() {
        return img_profile;
    }

    public TextView getName() {
        return name;
    }

    public SwitchCompat getSwitchButton() {
        return switchButton;
    }

    public Context getContext() {
        return context;
    }

    public void setData(Object c, Context context) {
//        if (!(c instanceof GroupMember)) return;
//        final GroupMember us = (GroupMember) c;
//        name.setText(us.getName());
//        Glide.with(context)
//                .using(new FirebaseImageLoader())
//                .load(FirebaseStorage.getInstance().getReference("users")
//                        .child(us.getId())
//                        .child(us.getiProfile()))
//                .asBitmap()
//                .placeholder(R.drawable.user_placeholder)
//                .into(img_profile);
//
//        switchButton.setChecked(true);
//        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(buttonView.isChecked()){
//                    us.setExcluded(false);
//                }
//                else {
//                    if (!(us.getId().equals(Singleton.getInstance().getCurrentUser().getId())))
//                        us.setExcluded(true);
//                    else {
//                        Snackbar.make(itemView,  "You have already payed", Toast.LENGTH_SHORT).show();
//                        switchButton.setChecked(true);
//                    }
//                }
//            }
//        });

    }
}
