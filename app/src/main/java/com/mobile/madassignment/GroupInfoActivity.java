package com.mobile.madassignment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.madassignment.Adapter.GroupMemberViewAdapter;
import com.mobile.madassignment.models.GroupMember;
import com.mobile.madassignment.models.UserInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.mobile.madassignment.util.Constants.Node_userInfo;

public class GroupInfoActivity extends Activity {

    private RecyclerView view_members;
    private ImageView back;
    private TextView groupName;
    private TextView expenseType;
    private TextView creator;
    private TextView createTime;
    private Button editName;
    private Button CancelEditName;
    private Button SaveEditName;
    private LinearLayout showNameL;
    private LinearLayout editNameL;
    private EditText nameEditArea;
    private EditText infoArea;
    private Button saveInfo;

    private String group_key;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group_info);
        view_members = (RecyclerView)findViewById(R.id.group_members);
        back = (ImageView)findViewById(R.id.group_info_back);
        groupName = (TextView)findViewById(R.id.tv_groupName);
        expenseType = (TextView)findViewById(R.id.tv_currency);
        creator = (TextView)findViewById(R.id.tv_creator);
        createTime = (TextView)findViewById(R.id.tv_createTime);
        editName = (Button)findViewById(R.id.edit_group_name);
        showNameL = (LinearLayout)findViewById(R.id.show_name);
        editNameL = (LinearLayout)findViewById(R.id.edit_name);
        CancelEditName = (Button)findViewById(R.id.cancel_edit);
        SaveEditName = (Button)findViewById(R.id.save_edit);
        nameEditArea = (EditText)findViewById(R.id.tv_groupNameChange);
        infoArea = (EditText)findViewById(R.id.tv_groupInfoChange);
        saveInfo = (Button)findViewById(R.id.bt_update_show_table);

        group_key = getIntent().getExtras().getString("group_key");
        mDatabaseRef  = FirebaseDatabase.getInstance().getReference();

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNameL.setVisibility(View.INVISIBLE);
                editNameL.setVisibility(View.VISIBLE);
            }
        });

        CancelEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNameL.setVisibility(View.VISIBLE);
                editNameL.setVisibility(View.INVISIBLE);
            }
        });

        SaveEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = groupName.getText().toString();
                String newName = nameEditArea.getText().toString();
                if(newName.equals(name)) {
                    Toast.makeText(GroupInfoActivity.this, "The group name is not changed.",Toast.LENGTH_SHORT ).show();
                } else {
                    mDatabaseRef.child("groups").child(group_key).child("name").setValue(nameEditArea.getText().toString());
                    Toast.makeText(GroupInfoActivity.this, "Change succeed.",Toast.LENGTH_SHORT ).show();
                    showNameL.setVisibility(View.VISIBLE);
                    editNameL.setVisibility(View.INVISIBLE);
                }
            }
        });

        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newInfo = infoArea.getText().toString();
                mDatabaseRef.child("groups").child(group_key).child("info").setValue(newInfo);
                Toast.makeText(GroupInfoActivity.this, "Save succeed.",Toast.LENGTH_SHORT ).show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent result = new Intent();
                setResult(Activity.RESULT_OK,result);
                GroupInfoActivity.this.finish();
            }
        });

        DatabaseReference groupRef = mDatabaseRef.child("groups").child(group_key);
        DatabaseReference groupMembersRef = groupRef.child("members");
        final DatabaseReference userRef = mDatabaseRef.child("users");
        List<GroupMember> members = new ArrayList<>();
        final GroupMemberViewAdapter groupAdapter = new GroupMemberViewAdapter(members, GroupInfoActivity.this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        view_members.setLayoutManager(layoutManager);
        view_members.setAdapter(groupAdapter);

        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupName.setText(dataSnapshot.child("name").getValue().toString());
                expenseType.setText(dataSnapshot.child("currency").getValue().toString());
                DataSnapshot d = dataSnapshot.child("info");
                if(d != null && d.getValue() != null) {
                    infoArea.setText(d.getValue().toString());
                }
                long cTime = Long.parseLong(dataSnapshot.child("createTime").getValue().toString());
                SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d1=new Date(cTime);
                String t1=format.format(d1);
                createTime.setText(t1);
                boolean creatorB = true;
                for(final DataSnapshot data: dataSnapshot.child("members").getChildren()){
                    if(creatorB) {
                        creator.setText(data.getValue().toString());
                        creatorB = false;
                    }
                    String userId = data.getKey();
                    userRef.child(userId).child("userInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                            GroupMember groupMember = new GroupMember(userInfo);
                            groupAdapter.getusers().add(groupMember);
                            groupAdapter.notifyDataSetChanged();
                            view_members.setAdapter(groupAdapter);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
