package com.mobile.madassignment;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.madassignment.models.Group;

public class JoinGroupActivity extends AppCompatActivity {

    private Button done;
    private ImageView back;
    private EditText group_name;
    private EditText pin_code;

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        done = (Button)findViewById(R.id.bt_join_group);
        back = (ImageView)findViewById(R.id.iv_new_group_back);
        group_name = (EditText)findViewById(R.id.group_name);
        pin_code = (EditText)findViewById(R.id.pin_code);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JoinGroupActivity.this.finish();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = group_name.getText().toString();
                String pin = pin_code.getText().toString();
                boolean checkpass = true;
                if(name == null || pin == null) {
                    checkpass = false;
                    Toast.makeText(JoinGroupActivity.this,"empty group-name or pin-code", Toast.LENGTH_SHORT).show();
                }
                else {
                    final String group_name = name;
                    final int pin_code = Integer.parseInt(pin);
                    setFlag(false);
                    mFirebaseDatabaseReference.child("groups").addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Group g = dataSnapshot.getValue(Group.class);
                                    if(g.getName() == group_name && SimpleHash(dataSnapshot.getKey().toString()) == pin_code) {
                                        mFirebaseDatabaseReference.child("groups").child(dataSnapshot.getKey().toString()).child("members").setValue(user.getUid(),user.getDisplayName());
                                        mFirebaseDatabaseReference.child("users").child(user.getUid()).child("groups").setValue(dataSnapshot.getKey().toString(), "true");
                                        setFlag(true);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.v("JoinGroupActivity", "getUser:onCancelled", databaseError.toException());
                                }
                            }
                    );
                    if(getFlag()){
                        Toast.makeText(JoinGroupActivity.this,"join succeed.", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(JoinGroupActivity.this,"join failed:can not find the group name match with the pin", Toast.LENGTH_SHORT).show();
                    }
                    JoinGroupActivity.this.finish();
                }
            }
        });
    }

    private void setFlag(boolean b) {
        flag = b;
    }


    private boolean getFlag() {
        return flag;
    }

    private int SimpleHash(String s) {
        int count = 0;
        for(int i = 0; i < s.length(); i++) {
            count += ((int) s.lastIndexOf(i));
        }
        return (count % 9999 + 1);
    }
}
