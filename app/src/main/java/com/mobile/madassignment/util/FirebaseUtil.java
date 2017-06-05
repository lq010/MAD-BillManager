package com.mobile.madassignment.util;

import com.google.firebase.database.FirebaseDatabase;

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


}
