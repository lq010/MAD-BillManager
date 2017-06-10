package com.mobile.madassignment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.mobile.madassignment.Adapter.BalanceListViewAdapter;
import com.mobile.madassignment.R;
import com.mobile.madassignment.models.Expense;
import com.mobile.madassignment.models.Group;
import com.mobile.madassignment.models.GroupMember;
import com.mobile.madassignment.models.UserInfo;
import com.mobile.madassignment.util.DataFormat;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mobile.madassignment.util.Constants.Node_userInfo;

public class ExpenseDetailActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    private ImageView bt_back;
    private TextView tv_groupName;
    private TextView tv_expenseType;
    private TextView tv_expenseCost;
    private TextView tv_description;
    private TextView tv_payer_name;
    private TextView tv_total_payed;
    private TextView tv_currency;
    private TextView tv_currency1;
    private TextView tv_creator;
    private TextView tv_createTime;
    private TextView tv_status;
    private ImageView iv_payer_userPhoto;
    private TextView tv_world_reason;
    private TextView tv_reason;
    private ImageView bt_picture;

    private LinearLayout bottomBar;
    private LinearLayout delete;
    private LinearLayout edit;
    private Expense expense;
    private String group_key;
    private String group_name;
    private String currency;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private BalanceListViewAdapter balanceListViewAdapter ;
    private RecyclerView rv_participants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);
        expense = (Expense) getIntent().getExtras().getSerializable("expense");
        group_key = getIntent().getExtras().getString("group_key");
        group_name = getIntent().getExtras().getString("group_name");
        currency = getIntent().getExtras().getString("currency");
        bt_back =(ImageView) findViewById(R.id.iv_new_record_back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpenseDetailActivity.this.finish();
            }
        });
        tv_groupName = (TextView) findViewById(R.id.tv_groupName);
        tv_expenseType = (TextView) findViewById(R.id.tv_expenseType);
        tv_expenseCost = (TextView) findViewById(R.id.tv_expenseCost);
        tv_description = (TextView) findViewById(R.id.tv_description);
        tv_payer_name = (TextView) findViewById(R.id.tv_payer_name);
        tv_total_payed = (TextView) findViewById(R.id.tv_total_payed);
        tv_currency = (TextView) findViewById(R.id.tv_currency);
        tv_currency1 = (TextView) findViewById(R.id.tv_currency1);
        tv_creator = (TextView) findViewById(R.id.tv_creator);
        tv_createTime = (TextView) findViewById(R.id.tv_createTime);
        tv_status = (TextView) findViewById(R.id.tv_status);
        iv_payer_userPhoto = (ImageView)findViewById(R.id.iv_payer_userPhoto);
        rv_participants = (RecyclerView) findViewById(R.id.rv_participants);
        tv_world_reason = (TextView) findViewById(R.id.text_reason);
        tv_reason = (TextView) findViewById(R.id.tv_reason);
        bottomBar = (LinearLayout) findViewById(R.id.bottomBar);
        delete = (LinearLayout) findViewById(R.id.delete);
        edit = (LinearLayout) findViewById(R.id.edit);
        bt_picture = (ImageView) findViewById(R.id.img_has_pic);

        if(expense.getPicture()!=null){
            bt_picture.setVisibility(View.VISIBLE);
            bt_picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ExpenseDetailActivity.this, FullImgActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("groupKey",group_key);
                    bundle.putString("photoName",expense.getPicture());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        tv_groupName.setText(group_name);
        tv_expenseType.setText(expense.getType());
        tv_expenseCost.setText(expense.getCost()+"");
        Log.d(TAG,"decsription length :" +expense.getDescription().length());
        if(expense.getDescription().length()==0){
            tv_description.setText("no description");
            tv_description.setTextColor(Color.rgb(211,211,211));
            tv_description.setTextSize(10);
        }

        else
            tv_description.setText(expense.getDescription());

        Log.d(TAG,expense.getId()+"<"+expense.getPayer()+">");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mDatabase.child("users").child(expense.getPayer()).child(Node_userInfo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                        tv_payer_name.setText(userInfo.getName());
                        tv_total_payed.setText(expense.getCost()+"");
                        Log.d("userInfo.isPhotoExist()",userInfo.isPhotoExist()+"");
                        Log.d("userInfo.isPhotoExist()",userInfo.getProfilePhoto()+"");
                        if(userInfo.isPhotoExist()){
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReferenceFromUrl("gs://madassignment-1f6c6.appspot.com");
                            StorageReference photoRef = storageRef.child(userInfo.getProfilePhoto() + ".jpg");
                            try {
                                final File localFile = File.createTempFile("images", "jpg");
                                final long ONE_MEGABYTE = 1024 * 1024;
                                photoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        // Local temp file has been created

                                        Picasso.with(ExpenseDetailActivity.this).load(localFile).into(iv_payer_userPhoto);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Picasso.with(ExpenseDetailActivity.this).load(R.drawable.profile).into(iv_payer_userPhoto);
                                    }
                                });
                            } catch (IOException e) {
                                Log.d("userPhoto", e.getMessage());
                            }

                        }else {
                            Picasso.with(ExpenseDetailActivity.this).load(R.drawable.profile).into(iv_payer_userPhoto);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        final Map<String, String > participants = expense.getParticipants();
        int num_participants =  participants.size();
        final float avg_cost = expense.getCost()/num_participants;
        final List<GroupMember> members = new ArrayList<>();
        for(final String uid :  participants.keySet()){
            Log.d(TAG,"uid "+uid+",name "+participants.get(uid));
            mDatabase.child("users").child(uid).child(Node_userInfo).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                    GroupMember groupMember = new GroupMember(userInfo);
                    Log.d("mmmmm", "user..."+groupMember.getName()+ " id: "+groupMember.getId());

                    groupMember.setName(participants.get(uid));
                    groupMember.setBalance(0-avg_cost);
                    members.add(groupMember);

                    balanceListViewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        balanceListViewAdapter = new BalanceListViewAdapter(members,ExpenseDetailActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        layoutManager.requestSimpleAnimationsInNextLayout();
        rv_participants.setLayoutManager(layoutManager);
        rv_participants.setAdapter(balanceListViewAdapter);


        //set creator name
        tv_creator.setText(expense.getParticipants().get(expense.getPayer()));
        tv_createTime.setText(expense.getFormatDate());
        tv_currency.setText(currency);
        tv_currency1.setText(currency);

        if(expense.getStatus()==null||expense.getStatus().matches("valid")){
            tv_status.setText("valid");
        }else if(expense.getStatus().matches("deleted")){
            tv_status.setText("deleted");
            tv_status.setTextColor(Color.parseColor("#EC473F"));
            tv_world_reason.setVisibility(View.VISIBLE);
            tv_reason.setVisibility(View.VISIBLE);
            tv_reason.setText(expense.getDelete_reason());
        }

        if(expense.getPayer().matches(mAuth.getCurrentUser().getUid())&&
                !expense.getStatus().matches("deleted")){
            Log.d(TAG,expense.getStatus());
            //bottom bar
            bottomBar.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "delete " + expense.getId());
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseDetailActivity.this);
                    builder.setTitle("Confirm Delete");
                    builder.setMessage("Are you sure you want to delete expense?");
                    final TextInputEditText input = new TextInputEditText(builder.getContext());
                    final TextInputLayout inputLayout = new TextInputLayout(builder.getContext());

                    inputLayout.setHint("reason");
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    inputLayout.addView(input);
                    input.setPadding(25,20,25,20);
                    builder.setView(inputLayout);

                    builder.setNegativeButton("Cancel",null);
                    builder.setPositiveButton("YES",null);
                    final AlertDialog dialog = builder.create();

                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            Button positiveB = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                            Button negativeB = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                            positiveB.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String reason = input.getText().toString();
                                    if(reason.trim().matches("")||reason.length()==0){
                                        Log.d(TAG,"true-reason empty");
                                        input.setError("please input a reason!");
                                        //input.requestFocus();
                                    }else{
                                        Log.d(TAG,"false-reason correct");

                                        deleteExpense(group_key, expense, reason);
                                        dialog.dismiss();
                                        ExpenseDetailActivity.this.finish();
                                    }
                                }
                            });

                            negativeB.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

                        }
                    });


                    dialog.show();

                }


            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ExpenseDetailActivity.this,"not available, please recreate it",Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"edit " +expense.getId());
                }
            });
        }

    }

    private  void deleteExpense(String group_key, Expense expense, String reason){
        expense.setDelete_reason(reason);
        expense.setStatus("deleted");
        mDatabase.child("expenses").child(group_key).child(expense.getId()).setValue(expense);

    }


}
