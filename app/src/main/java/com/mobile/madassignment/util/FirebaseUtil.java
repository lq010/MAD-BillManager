package com.mobile.madassignment.util;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.mobile.madassignment.R;
import com.mobile.madassignment.models.GroupMember;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by lq on 25/05/2017.
 */

public class FirebaseUtil {

        private static FirebaseDatabase mDatabase;

        public static FirebaseDatabase getDatabase() {
            if (mDatabase == null) {
                mDatabase = FirebaseDatabase.getInstance();
                mDatabase.setPersistenceEnabled(true);
            }
            return mDatabase;
        }

    public static void LoadUserImageProfile(CircleImageView cv, Context context, String userProfile) {
        Glide.with(getApplicationContext())

                .load(FirebaseStorage.getInstance().getReference()
                        .child(userProfile))
                .into(cv);
    }

}
