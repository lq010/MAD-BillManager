package com.mobile.madassignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InvitationActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = InvitationActivity.class.getSimpleName();

    private FirebaseUser user;
    private FirebaseAuth auth;
    private String  group_key = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invation);
        // Button click listener
        findViewById(R.id.button_ok).setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }


    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        if (AppInviteReferral.hasReferral(intent)) {
            processReferralIntent(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }

    private void processReferralIntent(Intent intent) {
        // Extract referral information from the intent
        String invitationId = AppInviteReferral.getInvitationId(intent);

        String deepLink = AppInviteReferral.getDeepLink(intent);

        Log.d("deeplink ", deepLink );
        String[] s = deepLink.split("\\$",2);
        group_key = s[1];
        // Display referral information
        // [START_EXCLUDE]
        Log.d(TAG, "Found Referral: " + invitationId + ":" + deepLink);
        DatabaseReference mdatabaseRef = FirebaseDatabase.getInstance().getReference();
        mdatabaseRef.child("groups").child(group_key).child("name")

                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ((TextView) findViewById(R.id.invitation_text))
                                .setText(getString(R.string.invitation_msg_fmt,"\""+dataSnapshot.getValue().toString()+"\""));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        // [END_EXCLUDE]
    }
    // [END process_referral_intent]


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_cancel) {
            finish();
        }else if(i==R.id.button_ok){
            if(user!=null){
                if(!group_key.matches("false")){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("groups").child(group_key).child("members").child(user.getUid()).setValue(user.getDisplayName());
                    databaseReference.child("users").child(user.getUid()).child("groups").child(group_key).setValue("true");
                }else {
                    Log.d("error","group_key is :" +group_key);
                }
                this.finish();

            }else if(user == null){
                Toast.makeText(this, "please login", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
