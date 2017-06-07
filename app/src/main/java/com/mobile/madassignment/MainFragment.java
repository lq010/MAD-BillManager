package com.mobile.madassignment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile.madassignment.Adapter.ExpenseViewHolder;
import com.mobile.madassignment.Adapter.GroupMemberViewAdapter;
import com.mobile.madassignment.models.Expense;
import com.mobile.madassignment.models.GroupMember;
import com.mobile.madassignment.util.DataFormat;
import com.mobile.madassignment.models.UpdateExpenseListEvent;
import com.mobile.madassignment.util.SimpleDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;


public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private static final int REQUEST_INVITE = 0;
    private static final int REQUEST_BALANCE =1;


    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView myExpenseRecyclerView;
    private ListView main_lv;
    private FloatingActionButton add_expense;
    private String group_key;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference groupRef;
    private FirebaseRecyclerAdapter<Expense, ExpenseViewHolder> mFirebaseAdapter;
    private RelativeLayout rv_balance;
    private TextView tv_balence_value;
    private TextView tv_owenORowendText;
    private TextView tv_mySpending;
    private TextView tv_totalSpending;
    private RecyclerView rv_members;
    private LinearLayout ll_invite_button;
    private TextView tv_currency_name;
    private TextView deletedMark;

    private List<String> expenseKeys;
    private int numOfmembers=1;
    private float totalSpending;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private HashMap<String, String> userId_nameMap = new HashMap<>();;
    private GroupMember me = new GroupMember();
    private String groupName;
    private String currency;
    private float my_balance = -1;

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if(bundle!= null){

            group_key = bundle.getString("group_key");
            Log.v("get_arg ", group_key);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG,"event bus start");
        EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateExpenseListEvent event) {
        String balanceId = event.balanceId;
        try {
            updateExpenseList(balanceId);
        }catch (IllegalArgumentException e){
            //if expenseNUm == 0
            Log.d("query Error",e.getMessage());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        add_expense = (FloatingActionButton) v.findViewById(R.id.bt_add_expense);
        myExpenseRecyclerView = (RecyclerView) v.findViewById(R.id.expenseRecyclerView);
        rv_members = (RecyclerView)v.findViewById(R.id.rv_members);
        rv_balance = (RelativeLayout)v.findViewById(R.id.rv_balance);
        ll_invite_button = (LinearLayout)v.findViewById(R.id.add_user_to_group);
        tv_balence_value = (TextView)v.findViewById(R.id.tv_my_balance);
        tv_owenORowendText= (TextView)v.findViewById(R.id.tv_own_or_owned);
        tv_mySpending= (TextView)v.findViewById(R.id.tv_my_spending);
        tv_totalSpending= (TextView)v.findViewById(R.id.tv_group_spending);
        tv_currency_name = (TextView)v.findViewById(R.id.tv_currency_name);

        expenseKeys = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mDatabaseRef  = FirebaseDatabase.getInstance().getReference();

        mDatabaseRef.child("groups").child(group_key).child("members").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userId_nameMap.put(dataSnapshot.getKey(),dataSnapshot.getValue().toString());
                Log.d("put to map", dataSnapshot.getKey()+"= "+dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabaseRef.child("groups").child(group_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 groupName = dataSnapshot.child("name").getValue().toString();
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                Log.d(TAG,"actionbar = "+actionBar.toString());
                if(actionBar!=null){
                    actionBar.setTitle(groupName);
                 }



                currency = dataSnapshot.child("currency").getValue().toString();
                tv_currency_name.setText(currency);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseRef.child("balances").child(group_key).orderByChild("settledUp").equalTo(false)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot data : dataSnapshot.getChildren()) {
                            Log.d("balance", data.child("settledUp").getValue().toString());
                            int numOfexpenses =  (int)data.child("expenses").getChildrenCount();
                            Log.d("balance-num", data.child("expenses").getChildrenCount()+"");
                            Log.d("balance-id", data.getKey());
                            String balanceId = data.getKey();
                            EventBus.getDefault().post(new UpdateExpenseListEvent(balanceId));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "firebase Error(balances/{groupId}/settledUp"+databaseError.getMessage());
                        //Toast.makeText(getActivity(),"Internet error",Toast.LENGTH_SHORT).show();
                    }
                });



        DatabaseReference groupMembersRef = mDatabaseRef.child("groups").child(group_key).child("members");

        final DatabaseReference userRef = mDatabaseRef.child("users");


        //get group members
        List<GroupMember> members = new ArrayList<>();
        final GroupMemberViewAdapter groupAdapter = new GroupMemberViewAdapter(members , this.getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity(),LinearLayoutManager.HORIZONTAL,false);

        // groupAdapter.getNames().add("add_new_member");

        rv_members.setLayoutManager(layoutManager);
        rv_members.setAdapter(groupAdapter);

        groupMembersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numOfmembers = (int)dataSnapshot.getChildrenCount();
                for(final DataSnapshot data: dataSnapshot.getChildren()){
                    String userId = data.getKey();
                    String userName = data.getValue().toString();
                    Log.v("test key",userId);
                    userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            GroupMember user = new GroupMember();
                            user.setName(dataSnapshot.child("name").getValue().toString());
                            if (dataSnapshot.child("photoFile").exists()){
                                user.setPhotoFile(dataSnapshot.child("photoFile").getValue().toString());
                            }

                            Log.d(TAG, "user..."+user.getName()+user.getPhotoFile());
                            groupAdapter.getusers().add(user);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                groupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("group_key",group_key);
                bundle.putSerializable("group_members", userId_nameMap);
                Intent intent = new Intent(getActivity(),AddNewExpenseActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        rv_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),BalanceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("group_key",group_key);
                intent.putExtras(bundle);
                startActivityForResult(intent,REQUEST_BALANCE);
            }
        });

        ll_invite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                        .setMessage(getString(R.string.invitation_message))
                        .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)+"$"+group_key))
                        .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                        .setCallToActionText(getString(R.string.invitation_cta))

                        .build();
                startActivityForResult(intent,REQUEST_INVITE);
            }
        });


        return v;

    }



    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == android.app.Activity.RESULT_OK) {
               // DatabaseReference invitations = mDatabaseRef.child("invitations");
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);

                for (String id : ids) {

                    Log.d(TAG, "onActivityResult: sent invitation " + id);

                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // [START_EXCLUDE]
                showMessage(getString(R.string.send_failed));
                // [END_EXCLUDE]
            }
        }else if(requestCode == REQUEST_BALANCE){
            if(resultCode == Activity.RESULT_OK){
                Log.d("result_activitey","requestCode="+requestCode+", resultCOde= OK");
                me.reset();
                totalSpending = 0;
                my_balance = 0;
                MainFragment currentFragment = this;
                FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                fragTransaction.detach(currentFragment);
                fragTransaction.attach(currentFragment);
                fragTransaction.commit();


            }else if(resultCode == Activity.RESULT_CANCELED){

                Log.d("result_activitey","requestCode="+requestCode+", resultCOde= canceled");
            }
        }
    }
    // [END on_activity_result]

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void updateExpenseList(final String balanceId){

        mDatabaseRef.child("balances").child(group_key).child(balanceId).child("balance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float total_spending = 0;
                float my_spending = 0;

                Log.d(TAG,"balance=" +dataSnapshot.toString());
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    total_spending += data.child("spending").getValue(Float.class);
                    if(user.getUid().matches(data.getKey())){
                        my_spending = data.child("spending").getValue(Float.class);
                        my_balance = data.child("balance").getValue(Float.class);
                    }
                }
                tv_balence_value.setText(DataFormat.myDFloatFormat(my_balance));
                if (my_balance < 0) {
                    Log.d("Mybalance",my_balance+"");
                    tv_owenORowendText.setText("you owen");
                    tv_owenORowendText.setBackgroundColor(Color.RED);
                }

                tv_mySpending.setText(DataFormat.myDFloatFormat(my_spending));
                tv_totalSpending.setText(DataFormat.myDFloatFormat(total_spending));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Log.v(TAG, "balanceid "+balanceId);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Expense, ExpenseViewHolder>(
                Expense.class,
                R.layout.expense_item,
                ExpenseViewHolder.class,
                mDatabaseRef.child("expenses").child(group_key).orderByKey().startAt(balanceId)) {
            @Override
            protected void populateViewHolder(ExpenseViewHolder viewHolder, final Expense expense, final int position) {
                Log.v("expense.type",expense.getType());
                Log.v("expense.cost",expense.getCost()+"");
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Expense clickedExpense = mFirebaseAdapter.getItem(position);
                        Intent intent = new Intent(getActivity(),ExpenseDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("group_key",group_key);
                        bundle.putString("group_name",groupName);
                        bundle.putString("currency",currency);
                        bundle.putSerializable("expense",expense);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                String type = expense.getType();
                if(expense.getDescription().matches("")||expense.getDescription()==null){
                    viewHolder.type.setText(type);

                }else {
                    viewHolder.type.setText(expense.getDescription());
                }
                viewHolder.cost.setText(expense.getCost()+"" );
                if(expense.getPayer().matches(user.getUid())){  //TODO
                    viewHolder.payer.setText("you payed ");
                    viewHolder.payer.setTextColor(Color.GREEN);
                }else {

                    viewHolder.payer.setText( userId_nameMap.get(expense.getPayer())+ " payed ");
                    viewHolder.payer.setTextColor(Color.RED);
                }
                switch (type){
                    case "home":
                        viewHolder.typeImgView.setImageResource(R.mipmap.ic_home);
                        break;
                    case "trip":
                        viewHolder.typeImgView.setImageResource(R.mipmap.ic_bus);
                        break;
                    case "shopping" :
                        viewHolder.typeImgView.setImageResource(R.mipmap.ic_handcart);
                        break;
                    case "food":
                        viewHolder.typeImgView.setImageResource(R.mipmap.ic_bowl);
                        break;
                    case "other":
                        viewHolder.typeImgView.setImageResource(R.mipmap.ic_other);
                        break;
                }
                viewHolder.createTime.setText(expense.getFormatDate());

               if( expense.getStatus().matches("deleted")){
                   viewHolder.deletedMark.setVisibility(View.VISIBLE);
                   viewHolder.deleteMark_line.setVisibility(View.VISIBLE);
               }

//                if(!expenseKeys.contains(expense.getId())){
//
//                    totalSpending += expense.getCost();
//
//                    if(expense.getParticipants().containsKey(user.getUid())){
//                        if (expense.getPayer().matches(user.getUid())) {
//                            me.setPayed(me.getPayed() + expense.getCost());
//                        }
//                        float avgSpending = expense.getCost() / expense.getParticipants().size();
//                        me.setSpending(me.getSpending()+avgSpending);
//                        me.setBalance(me.getPayed()-me.getSpending());
//                    }
//                    Log.d("payerId", expense.getPayer());
//                    Log.d("me_payed", me.getPayed() + "");
//                    Log.d("me_spending", me.getSpending() + "");
//                    Log.d("totalSpending ", totalSpending + "");
//                    Log.d("participants ", expense.getParticipants().size() + "");
//                    Log.d("mySpending ", me.getSpending() + "");
//
//                    tv_balence_value.setText(DataFormat.myDFloatFormat(me.getBalance()));
//                    if (me.getBalance() < 0) {
//                        Log.d("Mybalance",me.getBalance()+"");
//                        tv_owenORowendText.setText("you owen");
//                        tv_owenORowendText.setBackgroundColor(Color.RED);
//                    }
//
//                    tv_mySpending.setText(DataFormat.myDFloatFormat(me.getSpending()));
//                    tv_totalSpending.setText(DataFormat.myDFloatFormat(totalSpending));
//                    expenseKeys.add(expense.getId());
//                }

            }
        };

        mLinearLayoutManager = new LinearLayoutManager(this.getActivity());
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager.setReverseLayout(true);
        myExpenseRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this.getActivity()));
        myExpenseRecyclerView.setLayoutManager(mLinearLayoutManager);
        myExpenseRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_exit_group:
                if(!isInternetAvailable(this.getContext())){
                    showMessage("Please connect to the Internet");
                }else{
                    if (my_balance!=0){
                        showMessage("Your balance is "+my_balance+ ". You can not exit this group yet");
                    }else {
                        mDatabaseRef.child("users").child(user.getUid()).child("groups").child(group_key).removeValue();
                        mDatabaseRef.child("groups").child(group_key).child("members").child(user.getUid()).removeValue();
                        showMessage("Exit group successful");
                        MainFragment currentFragment = this;
                        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
                        fragTransaction.detach(currentFragment);
                        fragTransaction.addToBackStack(null);
                        fragTransaction.commit();

                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //check Internet connection
    public boolean isInternetAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return  isConnected;

    }
    private void showMessage(String msg) {
        ViewGroup container = (ViewGroup) getActivity().findViewById(R.id.snackbar_layout);
        Snackbar.make(container, msg, Snackbar.LENGTH_SHORT).show();
    }



}
